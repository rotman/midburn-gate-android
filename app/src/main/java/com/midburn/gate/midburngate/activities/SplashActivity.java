package com.midburn.gate.midburngate.activities;

import android.content.Intent;
import android.os.Bundle;
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

		String gateCode = AppUtils.getEventId(this);

		if (TextUtils.isEmpty(gateCode)) {
			boolean hasInternetConnection = AppUtils.isConnected(this);
			if (!hasInternetConnection) {
				AppUtils.createAndShowDialog(this, getString(R.string.no_network_dialog_title), getString(R.string.no_network_dialog_message), getString(R.string.ok), null, null, null, android.R.drawable.ic_dialog_alert);
				return;
			}
			AppUtils.fetchNewEventsCode(this, new NetworkApi.Callback<List<String>>() {
				@Override
				public void onSuccess(List<String> response) {
					if (response == null) {
						String errorMessage = "response body is null";
						onFailure(new Exception(errorMessage));
						return;
					}
					if (response.size() <= 0) {
						String errorMessage = "events list is empty";
						onFailure(new Exception(errorMessage));
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
					AppUtils.createAndShowDialog(SplashActivity.this, "שגיאה", AppUtils.getErrorMessage(SplashActivity.this, throwable.getMessage()), getString(R.string.ok), null, null, null, android.R.drawable.ic_dialog_alert);
					finish();
				}
			});
		}
	}
}
