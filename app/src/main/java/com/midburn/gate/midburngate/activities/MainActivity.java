package com.midburn.gate.midburngate.activities;

import android.app.ProgressDialog;
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

import com.midburn.gate.midburngate.HttpRequestListener;
import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.application.MainApplication;
import com.midburn.gate.midburngate.consts.AppConsts;
import com.midburn.gate.midburngate.dialogs.CarsDialog;
import com.midburn.gate.midburngate.model.Group;
import com.midburn.gate.midburngate.model.Ticket;
import com.midburn.gate.midburngate.network.NetworkApi;
import com.midburn.gate.midburngate.utils.AppUtils;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import okhttp3.HttpUrl;
import okhttp3.Response;

import static com.midburn.gate.midburngate.activities.SplashActivity.EVENTS_LIST;

public class MainActivity
		extends AppCompatActivity {

	private EditText    mInvitationNumberEditText;
	private EditText    mTicketNumberEditText;

	private DialogInterface.OnClickListener mNeedToDownloadScannerAppClickListener;
	private DialogInterface.OnClickListener mBackPressedClickListener;

	private HttpRequestListener mHttpRequestListener;

	private String mGateCode = "dadsadsa";

	private CarsDialog     mCarsDialog;
	private ProgressDialog mProgressDialog;

	public void manuallyInput(View view) {
		final String invitationNumber = mInvitationNumberEditText.getText()
		                                                         .toString();
		final String ticketNumber = mTicketNumberEditText.getText()
		                                                 .toString();
		if (TextUtils.isEmpty(invitationNumber) || TextUtils.isEmpty(ticketNumber)) {
			AppUtils.playMusic(this, AppConsts.ERROR_MUSIC);
			AppUtils.createAndShowDialog(this, getString(R.string.manually_validate_dialog_title), getString(R.string.manually_validate_dialog_message), getString(R.string.ok), null, null, null, android.R.drawable.ic_dialog_alert);
			return;
		}
		boolean hasInternetConnection = AppUtils.isConnected(this);
		if (!hasInternetConnection) {
			AppUtils.createAndShowDialog(this, getString(R.string.no_network_dialog_title), getString(R.string.no_network_dialog_message), getString(R.string.ok), null, null, null, android.R.drawable.ic_dialog_alert);
			return;
		}
		AppUtils.showProgressDialog(mProgressDialog);

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

	public void showCarDialog(View view) {
		mCarsDialog = new CarsDialog(this, v -> {
			Log.d(AppConsts.TAG, "carEnter");

			AppUtils.showProgressDialog(mProgressDialog);
			if (mCarsDialog != null) {
				mCarsDialog.dismiss();
			}
			NetworkApi.INSTANCE.enterCar(this, mGateCode, new NetworkApi.Callback<Unit>() {
				@Override
				public void onSuccess(Unit response) {
					mProgressDialog.dismiss();
				}

				@Override
				public void onFailure(@NotNull Throwable throwable) {
					mProgressDialog.dismiss();
				}
			});

		}, v -> {
			Log.d(AppConsts.TAG, "carExit");
			AppUtils.showProgressDialog(mProgressDialog);
			if (mCarsDialog != null) {
				mCarsDialog.dismiss();
			}
			NetworkApi.INSTANCE.exitCar(MainActivity.this, mGateCode, new NetworkApi.Callback<Unit>() {
				@Override
				public void onSuccess(Unit response) {
					mCarsDialog.dismiss();
				}

				@Override
				public void onFailure(@NotNull Throwable throwable) {
					mCarsDialog.dismiss();
				}
			});
		});
		mCarsDialog.show();
	}

	private void handleServerResponse(final Response response) {
		MainApplication.getsMainThreadHandler()
		               .post(new Runnable() {
			               @Override
			               public void run() {
				               if (response == null) {
					               Log.e(AppConsts.TAG, "response is null");
					               AppUtils.playMusic(MainActivity.this, AppConsts.ERROR_MUSIC);
					               AppUtils.createAndShowDialog(MainActivity.this, "פעולה נכשלה", null, getString(R.string.ok), null, null, null, android.R.drawable.ic_dialog_alert);
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
						               //bullet proof null properties
						               if (!jsonObject.isNull("gate_status")) {
							               ticket.setGateStatus((String) jsonObject.get("gate_status"));
						               }

						               if (!ticketJsonObject.isNull("barcode")) {
							               ticket.setBarCode((String) ticketJsonObject.get("barcode"));
						               }
						               else {
							               Log.e(AppConsts.TAG, "returned barcode is null. can't continue!!!");
						               }
						               if (!ticketJsonObject.isNull("ticket_number")) {
							               ticket.setTicketNumber((int) ticketJsonObject.get("ticket_number"));
						               }
						               if (!ticketJsonObject.isNull("order_id")) {
							               ticket.setInvitationNumber((int) ticketJsonObject.get("order_id"));
						               }
						               if (!ticketJsonObject.isNull("holder_name")) {
							               ticket.setTicketOwnerName((String) ticketJsonObject.get("holder_name"));
						               }
						               if (!ticketJsonObject.isNull("type")) {
							               ticket.setTicketType((String) ticketJsonObject.get("type"));
						               }
						               if (!ticketJsonObject.isNull("inside_event")) {
							               ticket.setInsideEvent((int) ticketJsonObject.get("inside_event"));
						               }
						               if (!ticketJsonObject.isNull("israeli_id")) {
							               ticket.setTicketOwnerId((String) ticketJsonObject.get("israeli_id"));
						               }
						               if (!ticketJsonObject.isNull("disabled_parking")) {
							               ticket.setIsDisabled((int) ticketJsonObject.get("disabled_parking"));
						               }
						               if (!ticketJsonObject.isNull("entrance_group_id")) {
							               ticket.setEntranceGroupId((int) ticketJsonObject.get("entrance_group_id"));
						               }
						               if (!ticketJsonObject.isNull("groups")) {
							               JSONArray groupsJsonArray = ticketJsonObject.getJSONArray("groups");
							               ArrayList<Group> groups = new ArrayList<>();
							               for (int i = 0 ; i < groupsJsonArray.length() ; i++) {
								               JSONObject groupJsonObject = groupsJsonArray.getJSONObject(i);
								               Group newGroup = new Group();
								               if (!groupJsonObject.isNull("id") && !groupJsonObject.isNull("name") && !groupJsonObject.isNull("type")) {
									               newGroup.setId((int) groupJsonObject.get("id"));
									               newGroup.setName((String) groupJsonObject.get("name"));
									               newGroup.setType((String) groupJsonObject.get("type"));
									               groups.add(newGroup);
								               }
								               else {
									               Log.e(AppConsts.TAG, "one of the group's fields is null");
								               }
							               }
							               ticket.setGroups(groups);
						               }

						               Log.d(AppConsts.TAG, ticket.toString());

						               Intent intent = new Intent(MainActivity.this, ShowActivity.class);
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
						               AppUtils.createAndShowDialog(MainActivity.this, "שגיאה", AppUtils.getErrorMessage(MainActivity.this, errorMessage), getString(R.string.ok), null, null, null, android.R.drawable.ic_dialog_alert);
					               }
				               } catch (IOException | JSONException e) {
					               Log.e(AppConsts.TAG, e.getMessage());
					               AppUtils.playMusic(MainActivity.this, AppConsts.ERROR_MUSIC);
					               AppUtils.createAndShowDialog(MainActivity.this, "שגיאה", e.getMessage(), getString(R.string.ok), null, null, null, android.R.drawable.ic_dialog_alert);
				               }
			               }
		               });
	}

	public void scanQR(View view) {
		try {
			//start the scanning activity from the com.google.zxing.client.android.SCAN intent
			Intent intent = new Intent(AppConsts.ACTION_SCAN);
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			intent.setPackage("com.google.zxing.client.android");
			startActivityForResult(intent, 0);
		} catch (ActivityNotFoundException anfe) {
			//on catch, show the download dialog
			AppUtils.playMusic(this, AppConsts.ERROR_MUSIC);
			AppUtils.createAndShowDialog(this, "סורק לא נמצא", "להוריד אפליקציית סורק?", "כן", "לא", mNeedToDownloadScannerAppClickListener, null, android.R.drawable.ic_dialog_alert);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				final String barcode = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.d(AppConsts.TAG, "barcode: " + barcode + " | format: " + format);

				AppUtils.showProgressDialog(mProgressDialog);

				HttpUrl url = new HttpUrl.Builder().scheme("https")
				                                   .host(AppConsts.SERVER_URL)
				                                   .addPathSegment("api")
				                                   .addPathSegment("gate")
				                                   .addPathSegment("get-ticket")
				                                   .build();

				JSONObject jsonObject = new JSONObject();
				try {
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
		checkForUpdates();

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String gateCode = sharedPref.getString(getString(R.string.gate_code_key), "");

		if (TextUtils.isEmpty(gateCode)) {
			List<String> events = getIntent().getStringArrayListExtra(EVENTS_LIST);

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkForCrashes();
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterManagers();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterManagers();
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

						AppUtils.fetchNewEventsCode(MainActivity.this, new NetworkApi.Callback<List<String>>() {
							@Override
							public void onSuccess(List<String> response) {

							}

							@Override
							public void onFailure(@NotNull Throwable throwable) {

							}
						});
					}
				}, null, android.R.drawable.ic_dialog_alert);
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
						mProgressDialog.dismiss();
					}
				});
				Log.d(AppConsts.TAG, "onResponse called");
				handleServerResponse(response);
			}
		};
	}

	private void bindView() {
		mInvitationNumberEditText = findViewById(R.id.invitationNumberEditText_MainActivity);
		mTicketNumberEditText = findViewById(R.id.ticketNumberEditText_MainActivity);
		mProgressDialog = new ProgressDialog(this);
	}


	@Override
	public void onBackPressed() {
		AppUtils.createAndShowDialog(this, "האם ברצונך לצאת?", "", "כן", "לא", mBackPressedClickListener, null, android.R.drawable.ic_dialog_alert);
	}


	private void checkForCrashes() {
		CrashManager.register(this);
	}

	private void checkForUpdates() {
		// Remove this for store builds!
		UpdateManager.register(this);
	}

	private void unregisterManagers() {
		UpdateManager.unregister();
	}
}
