package com.midburn.gate.midburngate.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.midburn.gate.midburngate.HttpRequestListener;
import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.application.MainApplication;
import com.midburn.gate.midburngate.consts.AppConsts;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class AppUtils {

	public static void createAndShowDialog(final Context context, String title, String message, @Nullable String positiveButtonText, @Nullable String negativeButtonText, @Nullable DialogInterface.OnClickListener onClickListener, int iconId) {
		new AlertDialog.Builder(context).setTitle(title)
		                                .setMessage(message)
		                                .setPositiveButton(positiveButtonText, onClickListener)
		                                .setNegativeButton(negativeButtonText, null)
		                                .setIcon(iconId)
		                                .show();
	}

	public static void playMusic(Context context, int which) {
		switch (which) {
			case AppConsts.OK_MUSIC:
				MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ok);
				mediaPlayer.start();
				break;
			case AppConsts.ERROR_MUSIC:
				mediaPlayer = MediaPlayer.create(context, R.raw.error);
				mediaPlayer.start();
				break;
		}
	}

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnected();
	}

	public static void doHttpRequest(final HttpUrl url, final HttpRequestListener httpRequestListener) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Log.d(AppConsts.TAG, "url: " + url);
					Request request = new Request.Builder().url(url)
					                                       .build();
					Response response = MainApplication.getHttpClient()
					                                   .newCall(request)
					                                   .execute();
					httpRequestListener.onResponse(response);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
}
