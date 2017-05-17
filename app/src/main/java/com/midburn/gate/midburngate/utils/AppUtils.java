package com.midburn.gate.midburngate.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;

import com.midburn.gate.midburngate.HttpRequestListener;
import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.application.MainApplication;
import com.midburn.gate.midburngate.consts.AppConsts;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppUtils {

	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

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

	public static void doGETHttpRequest(final HttpUrl url, final HttpRequestListener httpRequestListener) {
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
					Log.e(AppConsts.TAG, e.getMessage());
					httpRequestListener.onResponse(null);
				}
			}
		});
		thread.start();
	}

	public static void doPOSTHttpRequest(final HttpUrl url, final String requestBodyJson, final HttpRequestListener httpRequestListener) {
		Log.d(AppConsts.TAG, "url: " + url);
		Log.d(AppConsts.TAG, "requestBody: " + requestBodyJson);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				//TODO don't forget to remove the credentials when server is in production
				String username = "spark@midburn.org";
				String password = "spark";
				String credentials = username + ":" + password;
				final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
				RequestBody body = RequestBody.create(JSON, requestBodyJson);
				Request request = new Request.Builder().url(url)
				                                       .header("Authorization", basic)
				                                       .post(body)
				                                       .build();
				try {
					Response response = MainApplication.getHttpClient()
					                                   .newCall(request)
					                                   .execute();

					httpRequestListener.onResponse(response);
				} catch (IOException e) {
					Log.e(AppConsts.TAG, e.getMessage());
					httpRequestListener.onResponse(null);
				}
			}
		});
		thread.start();
	}

	public static String getErrorMessage(Context context, String error) {
		Log.d(AppConsts.TAG, "error to message: " + error);
		switch (error) {
			case AppConsts.QUOTA_REACHED_ERROR:
				return context.getString(R.string.quota_reached_error_message);
			case AppConsts.USER_OUTSIDE_EVENT_ERROR:
				return context.getString(R.string.user_outside_event_error_message);
			case AppConsts.GATE_CODE_MISSING_ERROR:
				return context.getString(R.string.gate_code_missing_error_message);
			case AppConsts.TICKET_NOT_FOUND_ERROR:
				return context.getString(R.string.ticket_not_found_error_message);
			case AppConsts.BAD_SEARCH_PARAMETERS_ERROR:
				return context.getString(R.string.bad_search_params_error_message);
			case AppConsts.ALREADY_INSIDE_ERROR:
				return context.getString(R.string.already_inside_error_message);
			case AppConsts.TICKET_NOT_IN_GROUP_ERROR:
				return context.getString(R.string.ticker_not_in_group_error_message);
			case AppConsts.INTERNAL_ERROR:
				return context.getString(R.string.internal_error_message);
			default:
				return error;

		}
	}
}
