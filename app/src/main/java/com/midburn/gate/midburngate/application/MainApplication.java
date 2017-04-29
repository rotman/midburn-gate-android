package com.midburn.gate.midburngate.application;

import android.app.Application;

import okhttp3.OkHttpClient;

public class MainApplication
		extends Application {

	private static OkHttpClient httpClient;

	@Override
	public void onCreate() {
		super.onCreate();
		httpClient = new OkHttpClient();
	}

	public static OkHttpClient getHttpClient() {
		return httpClient;
	}
}
