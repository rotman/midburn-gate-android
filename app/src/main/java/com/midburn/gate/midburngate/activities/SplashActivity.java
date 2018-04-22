package com.midburn.gate.midburngate.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.consts.AppConsts;
import com.midburn.gate.midburngate.network.NetworkApi;
import com.midburn.gate.midburngate.utils.AppUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity
		extends AppCompatActivity {

	public static final String EVENTS_LIST = "EVENTS_LIST";
	private int failureCounter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String gateCode = sharedPref.getString(getString(R.string.gate_code_key), "");

		if (TextUtils.isEmpty(gateCode)) {
			AppUtils.fetchNewEventsCode(this, new NetworkApi.Callback<List<String>>() {
				@Override
				public void onSuccess(List<String> response) {
					if (response == null) {
						onFailure(new Exception("response body is null"));
						return;
					}
					if (response.size() <= 0) {
						onFailure(new Exception("events list is empty"));
						return;
					}
					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					intent.putStringArrayListExtra(EVENTS_LIST, (ArrayList<String>) response);
					startActivity(intent);
				}

				@Override
				public void onFailure(@NotNull Throwable throwable) {
					failureCounter++;
					Log.e(AppConsts.TAG, throwable.getMessage() + " failureCounter: " + failureCounter);
					//TODO show error dialog instaed of moving to main activity
				}
			});
		}
	}
}
