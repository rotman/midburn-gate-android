package com.midburn.gate.midburngate.application;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;

import okhttp3.OkHttpClient;

public class MainApplication
		extends Application {

	private static OkHttpClient sHttpClient;
	private static Handler      sMainThreadHandler;

	@Override
	public void onCreate() {
		super.onCreate();
		sHttpClient = new OkHttpClient();
		sMainThreadHandler = new Handler();
		//turn volume to max
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//		am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
	}

	public static OkHttpClient getHttpClient() {
		return sHttpClient;
	}

	public static Handler getsMainThreadHandler() {
		return sMainThreadHandler;
	}
}
