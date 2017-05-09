package com.midburn.gate.midburngate.application;

import android.app.Application;
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
	}

	public static OkHttpClient getHttpClient() {
		return sHttpClient;
	}

	public static Handler getsMainThreadHandler() {
		return sMainThreadHandler;
	}
}
