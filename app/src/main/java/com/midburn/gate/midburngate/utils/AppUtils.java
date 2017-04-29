package com.midburn.gate.midburngate.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.consts.AppConsts;

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
}
