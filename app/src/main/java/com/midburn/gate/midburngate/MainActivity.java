package com.midburn.gate.midburngate;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity
		extends AppCompatActivity {

	public static final String TAG = "MIDBURN_GATE";
	public static final String SERVER_URL = "www.google.com";

	private static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

	//audio
	public static final int OK_MUSIC    = 1;
	public static final int ERROR_MUSIC = 2;

	private EditText invitationNumberEditText;
	private EditText TicketNumberEditText;

	private DialogInterface.OnClickListener mNeedToDownloadScannerAppClickListener;
	private DialogInterface.OnClickListener mBackPressedClickListener;

	public void manuallyInput(View view) {
		final String invitationNumber = invitationNumberEditText.getText()
		                                                        .toString();
		final String ticketNumber = TicketNumberEditText.getText()
		                                                .toString();
		if (TextUtils.isEmpty(invitationNumber) || TextUtils.isEmpty(ticketNumber)) {
			playMusic(this, ERROR_MUSIC);
			createAndShowDialog(this, getString(R.string.manually_validate_dialog_title), getString(R.string.manually_validate_dialog_message), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
		}
		else {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {

						HttpUrl url = new HttpUrl.Builder().scheme("https")
						                                   .host(SERVER_URL)
						                                   .addPathSegment("gate")
						                                   .addQueryParameter("action", "manual_entrance")
						                                   .addQueryParameter("order", invitationNumber)
						                                   .addQueryParameter("ticket", ticketNumber)
						                                   .build();
						Log.d(TAG, "url: " + url);
						Request request = new Request.Builder().url(url)
						                                       .build();
						Response response = MainApplication.getHttpClient()
						                                   .newCall(request)
						                                   .execute();
						handleServerResponse(response);

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
		}
	}

	private void handleServerResponse(Response response) {
		if (response != null) {
			playMusic(this, OK_MUSIC);
			//TODO handle response
			//TODO add audio playMusic();
		}
		else {
			playMusic(this, ERROR_MUSIC);
		}

	}

	public void scanBarcode(View view) {
		try {
			//start the scanning activity from the com.google.zxing.client.android.SCAN intent
			Intent intent = new Intent(ACTION_SCAN);
			intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
			startActivityForResult(intent, 0);
		} catch (ActivityNotFoundException anfe) {
			//on catch, show the download dialog
			playMusic(this, ERROR_MUSIC);
			createAndShowDialog(this, "סורק לא נמצא", "להוריד אפליקציית סורק?", "כן", "לא", mNeedToDownloadScannerAppClickListener, android.R.drawable.ic_dialog_alert);
		}

				Intent intent = new Intent(this, ShowActivity.class);
				String date = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", new java.util.Date()));
				//for now using mock ticket
				Ticket ticket = new Ticket("123456", "876543", "רותם מתיתיהו", "רגיל", date);
				intent.putExtra("ticketDetails", ticket);
				startActivity(intent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				final String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.d(TAG, "contents: " + contents + " | format: " + format);
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {

							HttpUrl url = new HttpUrl.Builder().scheme("https")
							                                   .host(SERVER_URL)
							                                   .addPathSegment("gate")
							                                   .addQueryParameter("id", contents)
							                                   .build();
							Log.d(TAG, "url: " + url);
							Request request = new Request.Builder().url(url)
							                                       .build();
							Response response = MainApplication.getHttpClient()
							                                   .newCall(request)
							                                   .execute();

							handleServerResponse(response);

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				thread.start();
			}
			else {
				playMusic(this, ERROR_MUSIC);
				createAndShowDialog(this, "הפעולה נכשלה", "נא נסה שוב או הזן פרטים ידנית", getString(R.string.ok), "", null, android.R.drawable.ic_dialog_alert);
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
					Log.e(TAG, anfe.getMessage());
				}
			}
		};

		mBackPressedClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//remove event id from shared prefs
				Log.d(TAG, "removing event id value");
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(getString(R.string.event_id_key), "");
				editor.apply();
				Intent intent = new Intent(MainActivity.this, InsertEventActivity.class);
				startActivity(intent);
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
		invitationNumberEditText = (EditText) findViewById(R.id.invitationNumberEditText_MainActivity);
		TicketNumberEditText = (EditText) findViewById(R.id.ticketNumberEditText_MainActivity);
	}

	public static void createAndShowDialog(final Context context, String title, String message, @Nullable String positiveButtonText, @Nullable String negativeButtonText, @Nullable DialogInterface.OnClickListener onClickListener, int iconId) {
		new AlertDialog.Builder(context).setTitle(title)
		                                .setMessage(message)
		                                .setPositiveButton(positiveButtonText, onClickListener)
		                                .setNegativeButton(negativeButtonText, null)
		                                .setIcon(iconId)
		                                .show();
	}

	@Override
	public void onBackPressed() {
		createAndShowDialog(this, "האם ברצונך להזין מספר אירוע חדש?", "פעולה זו תמחוק את מספר האירוע הנוכחי", "כן", "לא", mBackPressedClickListener, android.R.drawable.ic_dialog_alert);
	}

	public static void playMusic(Context context, int which) {
		switch (which) {
			case OK_MUSIC:
				MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ok);
				mediaPlayer.start();
				break;
			case ERROR_MUSIC:
				mediaPlayer = MediaPlayer.create(context, R.raw.error);
				mediaPlayer.start();
				break;
		}
	}
}
