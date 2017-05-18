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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.midburn.gate.midburngate.HttpRequestListener;
import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.application.MainApplication;
import com.midburn.gate.midburngate.consts.AppConsts;
import com.midburn.gate.midburngate.model.Group;
import com.midburn.gate.midburngate.model.Ticket;
import com.midburn.gate.midburngate.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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

	private String mGateCode;

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
		mProgressBar.setVisibility(View.VISIBLE);

		HttpUrl url = new HttpUrl.Builder().scheme("https")
		                                   .host(AppConsts.SERVER_URL)
		                                   .addPathSegment("api")
		                                   .addPathSegment("gate")
		                                   .addPathSegment("get-ticket")
		                                   .build();

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("gate_code", mGateCode);
			jsonObject.put("ticket", ticketNumber);
			jsonObject.put("order", invitationNumber);

		} catch (JSONException e) {
			Log.e(AppConsts.TAG, e.getMessage());
		}

		AppUtils.doPOSTHttpRequest(url, jsonObject.toString(), mHttpRequestListener);
	}

	private void handleServerResponse(final Response response) {
		MainApplication.getsMainThreadHandler()
		               .post(new Runnable() {
			               @Override
			               public void run() {
				               if (response == null) {
					               Log.e(AppConsts.TAG, "response is null");
					               AppUtils.playMusic(MainActivity.this, AppConsts.ERROR_MUSIC);
					               AppUtils.createAndShowDialog(MainActivity.this, "פעולה נכשלה", null, getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
					               return;
				               }

				               try {
					               String responseBodyString = response.body()
					                                                   .string();
					               Log.d(AppConsts.TAG, "response.body():" + responseBodyString);
					               if (response.code() == AppConsts.RESPONSE_OK) {
						               AppUtils.playMusic(MainActivity.this, AppConsts.OK_MUSIC);

						               JSONObject jsonObject = new JSONObject(responseBodyString);
						               JSONObject ticketJsonObject = (JSONObject) jsonObject.get("ticket");

						               Ticket ticket = new Ticket();
						               ticket.setTicketNumber((int) ticketJsonObject.get("ticket_number"));
						               ticket.setTicketOwnerName((String) ticketJsonObject.get("holder_name"));
						               ticket.setTicketType((String) ticketJsonObject.get("type"));
						               ticket.setInsideEvent((int) ticketJsonObject.get("inside_event"));
						               //						               ticket.setTicketOwnerId((String) ticketJsonObject.get("israeli_id"));
						               //						               ticket.setEntranceDate((Date) ticketJsonObject.get("entrance_timestamp"));
						               //						               ticket.setFirstEntranceDate((Date) ticketJsonObject.get("first_entrance_timestamp"));
						               //						               ticket.setLastExitDate((Date) ticketJsonObject.get("last_exit_timestamp"));
						               //						               ticket.setEntranceGroupId((int) ticketJsonObject.get("entrance_group_id"));

						               JSONArray groupsJsonArray = ticketJsonObject.getJSONArray("groups");
						               ArrayList<Group> groups = new ArrayList<>();
						               //mock groups
//						               groups.add(new Group(3, "music", "rabbits"));
//						               groups.add(new Group(15, "art", "sunshine"));
//						               groups.add(new Group(3, "chill", "handy camp"));

						               for (int i = 0 ; i < groupsJsonArray.length() ; i++) {
							               JSONObject groupJsonObject = groupsJsonArray.getJSONObject(i);
							               Group newGroup = new Group();
							               newGroup.setId((int) groupJsonObject.get("id"));
							               newGroup.setName((String) groupJsonObject.get("name"));
							               newGroup.setType((String) groupJsonObject.get("type"));
							               groups.add(newGroup);
						               }
						               ticket.setGroups(groups);

						               Intent intent = new Intent(MainActivity.this, ShowActivity.class);
						               //for now using mock ticket
						               //Ticket ticket = new Ticket("123456", "876543", "רותם מתיתיהו", "רגיל", date);
						               intent.putExtra("ticketDetails", ticket);
						               startActivity(intent);
					               }
					               else {
						               Log.e(AppConsts.TAG, "response code: " + response.code() + " | response body: " + responseBodyString);
						               AppUtils.playMusic(MainActivity.this, AppConsts.ERROR_MUSIC);
						               JSONObject jsonObject = new JSONObject(responseBodyString);
						               String errorMessage;
						               try {
							               errorMessage = (String) jsonObject.get("error");
						               } catch (ClassCastException e) {
							               Log.e(AppConsts.TAG, e.getMessage());
							               errorMessage = (String) jsonObject.get("message");
						               }
						               AppUtils.createAndShowDialog(MainActivity.this, "שגיאה", AppUtils.getErrorMessage(MainActivity.this, errorMessage), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
					               }
				               } catch (IOException | JSONException e) {
					               Log.e(AppConsts.TAG, e.getMessage());
					               AppUtils.playMusic(MainActivity.this, AppConsts.ERROR_MUSIC);
					               AppUtils.createAndShowDialog(MainActivity.this, "שגיאה", e.getMessage(), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
				               }
			               }
		               });
	}

	public void scanQR(View view) {
		try {
			//start the scanning activity from the com.google.zxing.client.android.SCAN intent
			Intent intent = new Intent(AppConsts.ACTION_SCAN);
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
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
				final String barcode = intent.getStringExtra("SCAN_RESULT");

				//persist barCode
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(getString(R.string.barcode), barcode);
				editor.apply();

				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.d(AppConsts.TAG, "barcode: " + barcode + " | format: " + format);

				mProgressBar.setVisibility(View.VISIBLE);

				HttpUrl url = new HttpUrl.Builder().scheme("https")
				                                   .host(AppConsts.SERVER_URL)
				                                   .addPathSegment("api")
				                                   .addPathSegment("gate")
				                                   .addPathSegment("get-ticket")
				                                   .build();

				JSONObject jsonObject = new JSONObject();
				try {
					//add event_id?
					jsonObject.put("gate_code", mGateCode);
					jsonObject.put("barcode", barcode);

				} catch (JSONException e) {
					Log.e(AppConsts.TAG, e.getMessage());
				}

				AppUtils.doPOSTHttpRequest(url, jsonObject.toString(), mHttpRequestListener);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bindView();
		setListeners();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mGateCode = sharedPref.getString(getString(R.string.gate_code_key), "");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_layout, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_menu:
				AppUtils.createAndShowDialog(this, "הכנס קוד אירוע חדש?", "פעולה זו תמחק את קוד האירוע הישן", "כן", "לא", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//clear event id from shared prefs
						SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putString(getString(R.string.gate_code_key), "");
						editor.apply();

						Intent intent = new Intent(MainActivity.this, InsertGateCodeActivity.class);
						startActivity(intent);
					}
				}, android.R.drawable.ic_dialog_alert);
				return true;
			default:
				// If we got here, the user's action was not recognized.
				// Invoke the superclass to handle it.
				return super.onOptionsItemSelected(item);

		}
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
				//exit app
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
				Log.d(AppConsts.TAG, "onResponse called");
				handleServerResponse(response);
			}
		};
	}

	private void bindView() {
		mInvitationNumberEditText = (EditText) findViewById(R.id.invitationNumberEditText_MainActivity);
		mTicketNumberEditText = (EditText) findViewById(R.id.ticketNumberEditText_MainActivity);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_MainActivity);
	}


	@Override
	public void onBackPressed() {
		AppUtils.createAndShowDialog(this, "האם ברצונך לצאת?", "", "כן", "לא", mBackPressedClickListener, android.R.drawable.ic_dialog_alert);
	}
}