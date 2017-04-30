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
import android.widget.ProgressBar;

import com.midburn.gate.midburngate.HttpRequestListener;
import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.consts.AppConsts;
import com.midburn.gate.midburngate.utils.AppUtils;

import okhttp3.Response;

public class InsertEventActivity
		extends AppCompatActivity {

	private EditText    mEventIdEditText;
	private ProgressBar mProgressBar;

	private DialogInterface.OnClickListener mBackPressedClickListener;

	private HttpRequestListener mHttpRequestListener;

	public void eventIdInserted(View view) {
		String eventId = mEventIdEditText.getText()
		                                 .toString();
		if (TextUtils.isEmpty(eventId)) {
			AppUtils.playMusic(this, AppConsts.ERROR_MUSIC);
			AppUtils.createAndShowDialog(this, getString(R.string.manually_validate_dialog_title), getString(R.string.validate_event_id), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
			return;
		}
		boolean hasInternetConnection = AppUtils.isConnected(this);
		if (!hasInternetConnection) {
			AppUtils.createAndShowDialog(this, getString(R.string.no_network_dialog_title), getString(R.string.no_network_dialog_message), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
			return;
		}

			//TODO check event id validation
			//			mProgressBar.setVisibility(View.VISIBLE);

		//save event id in shared prefs
			Log.d(AppConsts.TAG, "inserted event_id: " + eventId);
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(getString(R.string.event_id_key), eventId);
			editor.apply();

			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert_event);

		mBackPressedClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finishAffinity();
			}
		};

		mHttpRequestListener = new HttpRequestListener() {
			@Override
			public void onResponse(Response response) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mProgressBar.setVisibility(View.GONE);
					}
				});
				handleServerResponse(response);
			}
		};

		//read event_id value from shared prefs
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String eventId = sharedPref.getString(getString(R.string.event_id_key), "");
		Log.d(AppConsts.TAG, "eventId: " + eventId);
		if (!TextUtils.isEmpty(eventId)) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}

		getSupportActionBar().setTitle(getString(R.string.event));
		bindViews();
	}

	private void handleServerResponse(Response response) {
		if (response != null) {
			AppUtils.playMusic(this, AppConsts.OK_MUSIC);
			//TODO handle response
			//TODO add audio playMusic();

			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
		else {
			AppUtils.playMusic(this, AppConsts.ERROR_MUSIC);
		}
	}

	private void bindViews() {
		mEventIdEditText = (EditText) findViewById(R.id.eventIdEditText_InsertEventActivity);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_InsertEventActivity);
	}

	@Override
	public void onBackPressed() {
		AppUtils.createAndShowDialog(this, "האם ברצונך לצאת?", "", "כן", "לא", mBackPressedClickListener, android.R.drawable.ic_dialog_alert);
	}
}
