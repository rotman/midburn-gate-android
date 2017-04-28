package com.midburn.gate.midburngate;

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

public class InsertEventActivity
		extends AppCompatActivity {

	private EditText                        eventIdEditText;
	private DialogInterface.OnClickListener mBackPressedClickListener;

	public void eventIdInserted(View view) {
		String eventId = eventIdEditText.getText()
		                                .toString();
		if (!TextUtils.isEmpty(eventId)) {
			//TODO check event id validation

			//save event id in shared prefs
			Log.d(MainActivity.TAG, "inserted event_id: " + eventId);
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(getString(R.string.event_id_key), eventId);
			editor.apply();

			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		else {
			MainActivity.playMusic(this, MainActivity.ERROR_MUSIC);
			MainActivity.createAndShowDialog(this, getString(R.string.manually_validate_dialog_title), getString(R.string.validate_event_id), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
		}
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

		//read event_id value from shared prefs
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String eventId = sharedPref.getString(getString(R.string.event_id_key), "");
		Log.d(MainActivity.TAG, "eventId: " + eventId);
		if (!TextUtils.isEmpty(eventId)) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}

		getSupportActionBar().setTitle(getString(R.string.event));
		bindViews();
	}

	private void bindViews() {
		eventIdEditText = (EditText) findViewById(R.id.eventIdEditText_InsertEventActivity);
	}

	@Override
	public void onBackPressed() {
		MainActivity.createAndShowDialog(this, "האם ברצונך לצאת?", "", "כן", "לא", mBackPressedClickListener, android.R.drawable.ic_dialog_alert);
	}
}
