package com.midburn.gate.midburngate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.midburn.gate.midburngate.R;

public class SplashActivity
		extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this, InsertGateCodeActivity.class);
				startActivity(intent);

			}
		}, 3000);
	}
}
