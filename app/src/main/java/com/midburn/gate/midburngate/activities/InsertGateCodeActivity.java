package com.midburn.gate.midburngate.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.consts.AppConsts;
import com.midburn.gate.midburngate.utils.AppUtils;

public class InsertGateCodeActivity
		extends AppCompatActivity {

	private EditText    mGateCodeEditText;

	private DialogInterface.OnClickListener mBackPressedClickListener;

	public void eventIdInserted(View view) {
		String gateCode = mGateCodeEditText.getText()
		                                   .toString();
		//save event id in shared prefs
		Log.d(AppConsts.TAG, "inserted gate code: " + gateCode);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.gate_code_key), gateCode);
		editor.apply();

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert_gate);

		mBackPressedClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finishAffinity();
			}
		};

		//read event_id value from shared prefs
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String gateCode = sharedPref.getString(getString(R.string.gate_code_key), "");
		Log.d(AppConsts.TAG, "gateCode: " + gateCode);
		if (!TextUtils.isEmpty(gateCode)) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}

		getSupportActionBar().setTitle(getString(R.string.gate));
		bindViews();
	}

	private void bindViews() {
		mGateCodeEditText = (EditText) findViewById(R.id.eventIdEditText_InsertEventActivity);
	}

	@Override
	public void onBackPressed() {
		AppUtils.createAndShowDialog(this, "האם ברצונך לצאת?", "", "כן", "לא", mBackPressedClickListener, android.R.drawable.ic_dialog_alert);
	}
}
