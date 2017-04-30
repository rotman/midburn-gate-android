package com.midburn.gate.midburngate.activities;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.midburn.gate.midburngate.HttpRequestListener;
import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.consts.AppConsts;
import com.midburn.gate.midburngate.model.Ticket;
import com.midburn.gate.midburngate.utils.AppUtils;

import okhttp3.HttpUrl;
import okhttp3.Response;

public class MainActivity
		extends AppCompatActivity {

	private EditText    mInvitationNumberEditText;
	private EditText    mTicketNumberEditText;
	private ProgressBar mProgressBar;

	private DialogInterface.OnClickListener mNeedToDownloadScannerAppClickListener;
	private DialogInterface.OnClickListener mBackPressedClickListener;

	private HttpRequestListener mHttpRequestListener;


	public void manuallyInput(View view) {
		final String invitationNumber = mInvitationNumberEditText.getText()
		                                                         .toString();
		final String ticketNumber = mTicketNumberEditText.getText()
		                                                 .toString();
		if (TextUtils.isEmpty(invitationNumber) || TextUtils.isEmpty(ticketNumber)) {
			AppUtils.playMusic(this, AppConsts.ERROR_MUSIC);
			AppUtils.createAndShowDialog(this, getString(R.string.manually_validate_dialog_title), getString(R.string.manually_validate_dialog_message), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
			return;
		}
		boolean hasInternetConnection = AppUtils.isConnected(this);
		if (!hasInternetConnection) {
			AppUtils.createAndShowDialog(this, getString(R.string.no_network_dialog_title), getString(R.string.no_network_dialog_message), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
			return;
		}
		HttpUrl url = new HttpUrl.Builder().scheme("https")
		                                   .host(AppConsts.SERVER_URL)
		                                   .addPathSegment("gate")
		                                   .addQueryParameter("action", "manual_entrance")
		                                   .addQueryParameter("order", invitationNumber)
		                                   .addQueryParameter("ticket", ticketNumber)
		                                   .build();

		mProgressBar.setVisibility(View.VISIBLE);
		AppUtils.doHttpRequest(url, mHttpRequestListener);

	}

	private void handleServerResponse(Response response) {

		if (response != null) {
			AppUtils.playMusic(this, AppConsts.OK_MUSIC);

			//TODO handle response

			Intent intent = new Intent(this, ShowActivity.class);
			String date = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", new java.util.Date()));
			//for now using mock ticket
			Ticket ticket = new Ticket("123456", "876543", "רותם מתיתיהו", "רגיל", date);
			intent.putExtra("ticketDetails", ticket);
			startActivity(intent);
		}
		else {
			AppUtils.playMusic(this, AppConsts.ERROR_MUSIC);
		}

	}

	public void scanBarcode(View view) {
		try {
			//start the scanning activity from the com.google.zxing.client.android.SCAN intent
			Intent intent = new Intent(AppConsts.ACTION_SCAN);
			intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
			startActivityForResult(intent, 0);
		} catch (ActivityNotFoundException anfe) {
			//on catch, show the download dialog
			AppUtils.playMusic(this, AppConsts.ERROR_MUSIC);
			AppUtils.createAndShowDialog(this, "סורק לא נמצא", "להוריד אפליקציית סורק?", "כן", "לא", mNeedToDownloadScannerAppClickListener, android.R.drawable.ic_dialog_alert);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				final String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.d(AppConsts.TAG, "contents: " + contents + " | format: " + format);

				HttpUrl url = new HttpUrl.Builder().scheme("https")
				                                   .host(AppConsts.SERVER_URL)
				                                   .addPathSegment("gate")
				                                   .addQueryParameter("id", contents)
				                                   .build();

				mProgressBar.setVisibility(View.VISIBLE);
				AppUtils.doHttpRequest(url, mHttpRequestListener);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bindView();
		setListeners();
	}

	private void setListeners() {
		mNeedToDownloadScannerAppClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				try {
					MainActivity.this.startActivity(intent);
				} catch (ActivityNotFoundException anfe) {
					Log.e(AppConsts.TAG, anfe.getMessage());
				}
			}
		};

		mBackPressedClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//remove event id from shared prefs
				Log.d(AppConsts.TAG, "removing event id value");
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(getString(R.string.event_id_key), "");
				editor.apply();
				Intent intent = new Intent(MainActivity.this, InsertEventActivity.class);
				startActivity(intent);
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
				Log.d(AppConsts.TAG, "onResponse called");
				handleServerResponse(response);
			}
		};
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return false;
	}

	private void bindView() {
		mInvitationNumberEditText = (EditText) findViewById(R.id.invitationNumberEditText_MainActivity);
		mTicketNumberEditText = (EditText) findViewById(R.id.ticketNumberEditText_MainActivity);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_MainActivity);
	}



	@Override
	public void onBackPressed() {
		AppUtils.createAndShowDialog(this, "האם ברצונך להזין מספר אירוע חדש?", "פעולה זו תמחוק את מספר האירוע הנוכחי", "כן", "לא", mBackPressedClickListener, android.R.drawable.ic_dialog_alert);
	}


}
