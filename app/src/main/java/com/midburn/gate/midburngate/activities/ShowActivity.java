package com.midburn.gate.midburngate.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.midburn.gate.midburngate.HttpRequestListener;
import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.application.MainApplication;
import com.midburn.gate.midburngate.consts.AppConsts;
import com.midburn.gate.midburngate.model.Group;
import com.midburn.gate.midburngate.model.Ticket;
import com.midburn.gate.midburngate.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.Response;

public class ShowActivity
		extends AppCompatActivity {

	private TextView     mTicketNumberTextView;
	private TextView     mTicketOwnerNameTextView;
	private TextView     mTicketTypeTextView;
	private TextView     mEntranceDateTextView;
	private LinearLayout mDisabledLayout;
	//	private TextView mTicketFirstEntranceDateTextView;
	//	private TextView mTicketLastExitDateTextView;
	private TextView     mTicketOwnerIdTextView;

	private Button      mEntranceButton;
	private Button      mExitButton;
	private Button      mCancelButton;
	private ProgressBar mProgressBar;

	private enum State {
		ERALY_ENTRANCE,
		MIDBURN
	}

	private State mState;

	private HttpRequestListener mHttpRequestListener;

	private String mGateCode;
	private Ticket mTicket;

	public void exit(View view) {
		boolean hasInternetConnection = AppUtils.isConnected(this);
		if (!hasInternetConnection) {
			AppUtils.createAndShowDialog(this, getString(R.string.no_network_dialog_title), getString(R.string.no_network_dialog_message), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
			return;
		}
		if (mTicket == null) {
			Log.e(AppConsts.TAG, "ticket is null");
			return;
		}

		mProgressBar.setVisibility(View.VISIBLE);

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String barcode = sharedPref.getString(getString(R.string.barcode), "");
		Log.d(AppConsts.TAG, "user barcode to exit: " + barcode);

		HttpUrl url = new HttpUrl.Builder().scheme("https")
		                                   .host(AppConsts.SERVER_URL)
		                                   .addPathSegment("api")
		                                   .addPathSegment("gate")
		                                   .addPathSegment("gate-exit")
		                                   .build();

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("gate_code", mGateCode);
			jsonObject.put("barcode", barcode);
			jsonObject.put("group_id", mTicket.getEntranceGroupId());
		} catch (JSONException e) {
			Log.e(AppConsts.TAG, e.getMessage());
		}

		AppUtils.doPOSTHttpRequest(url, jsonObject.toString(), mHttpRequestListener);
	}

	public void entrance(View view) {
		boolean hasInternetConnection = AppUtils.isConnected(this);
		if (!hasInternetConnection) {
			AppUtils.createAndShowDialog(this, getString(R.string.no_network_dialog_title), getString(R.string.no_network_dialog_message), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
			return;
		}
		if (mTicket == null) {
			Log.e(AppConsts.TAG, "ticket is null");
			return;
		}
		if (mState.equals(State.ERALY_ENTRANCE)) {
			handleGroupTypes();

		}
		else if (mState.equals(State.MIDBURN)) {
			sendEntranceRequestWithoutGroups();
		}
		else {
			Log.e(AppConsts.TAG, "unknown state. mState: " + mState);
		}
	}

	private void handleGroupTypes() {
		final ArrayList<String> groupsTypes = new ArrayList<>();
		for (Group group : mTicket.getGroups()) {
			if (TextUtils.equals(group.getType(), AppConsts.GROUP_TYPE_PRODUCTION) && !groupsTypes.contains(AppConsts.GROUP_TYPE_PRODUCTION)) {
				groupsTypes.add(AppConsts.GROUP_TYPE_PRODUCTION);
			}
			else if (TextUtils.equals(group.getType(), AppConsts.GROUP_TYPE_ART) && !groupsTypes.contains(AppConsts.GROUP_TYPE_ART)) {
				groupsTypes.add(AppConsts.GROUP_TYPE_ART);
			}
			else if (TextUtils.equals(group.getType(), AppConsts.GROUP_TYPE_CAMP) && !groupsTypes.contains(AppConsts.GROUP_TYPE_CAMP)) {
				groupsTypes.add(AppConsts.GROUP_TYPE_CAMP);
			}
		}
		//show groups types selection dialog
		int groupsTypesArrayListSize = groupsTypes.size();
		Log.d(AppConsts.TAG, "groupsTypes.size(): " + groupsTypesArrayListSize);
		if (groupsTypesArrayListSize > 0) {
			final CharSequence groupsTypeArray[] = groupsTypes.toArray(new CharSequence[groupsTypesArrayListSize]);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("בחר סוג קבוצה");
			builder.setItems(groupsTypeArray, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String selectedGroupType = groupsTypes.get(which);
					Log.d(AppConsts.TAG, selectedGroupType + " was clicked.");
					handleGroupName(selectedGroupType);
				}
			});
			builder.show();
		}
	}

	private void handleGroupName(final String selectedGroupType) {
		switch (selectedGroupType) {
			case AppConsts.GROUP_TYPE_PRODUCTION:
				sendEntranceRequestWithoutGroups();
				break;
			case AppConsts.GROUP_TYPE_ART:
				ArrayList<String> artGroupsNames = new ArrayList<>();
				for (Group group : mTicket.getGroups()) {
					if (TextUtils.equals(group.getType(), AppConsts.GROUP_TYPE_ART)) {
						artGroupsNames.add(group.getName());
					}
				}
				showGroupsNamesPickerDialogAndSendToServer(artGroupsNames);
				break;
			case AppConsts.GROUP_TYPE_CAMP:
				ArrayList<String> campGroupsNames = new ArrayList<>();
				for (Group group : mTicket.getGroups()) {
					if (TextUtils.equals(group.getType(), AppConsts.GROUP_TYPE_CAMP)) {
						campGroupsNames.add(group.getName());
					}
				}
				showGroupsNamesPickerDialogAndSendToServer(campGroupsNames);
				break;

			default:
				Log.e(AppConsts.TAG, "unknown group type. selectedGroupType: " + selectedGroupType);
		}
	}

	private void showGroupsNamesPickerDialogAndSendToServer(final ArrayList<String> groupsNames) {
		//show groups names from the specific type selection dialog
		int groupsNameArrayListSize = groupsNames.size();
		Log.d(AppConsts.TAG, "groupsNames.size(): " + groupsNameArrayListSize);
		if (groupsNameArrayListSize > 0) {
			final CharSequence groupsNamesArray[] = groupsNames.toArray(new CharSequence[groupsNameArrayListSize]);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("בחר קבוצה");
			builder.setItems(groupsNamesArray, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String selectedGroupName = groupsNames.get(which);
					Log.d(AppConsts.TAG, selectedGroupName + " was clicked.");
					//take the group id and send to server
					for (Group group : mTicket.getGroups()) {
						if (TextUtils.equals(group.getName(), selectedGroupName)) {
							sendEntranceRequest(group.getId());
							break;
						}
					}
				}
			});
			builder.show();
		}
		else {
			Log.e(AppConsts.TAG, "No groups inside this type");
		}
	}

	private void sendEntranceRequestWithoutGroups() {
		String barcode = mTicket.getBarCode();
		Log.d(AppConsts.TAG, "user barcode to enter: " + barcode);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("barcode", barcode);
		} catch (JSONException e) {
			Log.e(AppConsts.TAG, e.getMessage());
		}
		HttpUrl url = new HttpUrl.Builder().scheme("https")
		                                   .host(AppConsts.SERVER_URL)
		                                   .addPathSegment("api")
		                                   .addPathSegment("gate")
		                                   .addPathSegment("gate-enter")
		                                   .build();

		AppUtils.doPOSTHttpRequest(url, jsonObject.toString(), mHttpRequestListener);
	}

	private void sendEntranceRequest(int groupId) {
		String barcode = mTicket.getBarCode();
		Log.d(AppConsts.TAG, "user barcode to enter: " + barcode);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("barcode", barcode);
			jsonObject.put("gate_code", mGateCode);
			jsonObject.put("group_id", groupId);
		} catch (JSONException e) {
			Log.e(AppConsts.TAG, e.getMessage());
		}
		HttpUrl url = new HttpUrl.Builder().scheme("https")
		                                   .host(AppConsts.SERVER_URL)
		                                   .addPathSegment("api")
		                                   .addPathSegment("gate")
		                                   .addPathSegment("gate-enter")
		                                   .build();

		AppUtils.doPOSTHttpRequest(url, jsonObject.toString(), mHttpRequestListener);
	}

	public void cancel(View view) {
		Intent intent = new Intent(ShowActivity.this, MainActivity.class);
		startActivity(intent);
	}

	private void handleServerResponse(final Response response) {
		MainApplication.getsMainThreadHandler()
		               .post(new Runnable() {
			               @Override
			               public void run() {
				               if (response == null) {
					               Log.e(AppConsts.TAG, "response is null");
					               AppUtils.playMusic(ShowActivity.this, AppConsts.ERROR_MUSIC);
					               AppUtils.createAndShowDialog(ShowActivity.this, "פעולה נכשלה", null, getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
					               return;
				               }
				               try {
					               String responseBodyString = response.body()
					                                                   .string();
					               Log.d(AppConsts.TAG, "response.body():" + responseBodyString);
					               if (response.code() == AppConsts.RESPONSE_OK) {
						               JSONObject jsonObject = new JSONObject(responseBodyString);
						               AppUtils.playMusic(ShowActivity.this, AppConsts.OK_MUSIC);
						               String resultMessage = (String) jsonObject.get("message");
						               Log.d(AppConsts.TAG, "resultMessage: " + resultMessage);
						               Intent intent = new Intent(ShowActivity.this, MainActivity.class);
						               startActivity(intent);
					               }
					               else {
						               Log.e(AppConsts.TAG, "response code: " + response.code() + " | response body: " + responseBodyString);
						               AppUtils.playMusic(ShowActivity.this, AppConsts.ERROR_MUSIC);
						               JSONObject jsonObject = new JSONObject(responseBodyString);
						               String errorMessage = (String) jsonObject.get("error");
						               AppUtils.createAndShowDialog(ShowActivity.this, "שגיאה", AppUtils.getErrorMessage(ShowActivity.this, errorMessage), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
					               }
				               } catch (IOException | JSONException e) {
					               Log.e(AppConsts.TAG, e.getMessage());
					               AppUtils.playMusic(ShowActivity.this, AppConsts.ERROR_MUSIC);
					               AppUtils.createAndShowDialog(ShowActivity.this, "שגיאה", e.getMessage(), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
				               }
			               }
		               });
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show);
		getSupportActionBar().setTitle(getString(R.string.ticket_details));
		bindView();

		//fetch gate code from shared prefs
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mGateCode = sharedPref.getString(getString(R.string.gate_code_key), "");

		//decided that when gate code is "171819", this is early entrance
		boolean isEarlyEntrance = TextUtils.equals(mGateCode, "171819");
		if (isEarlyEntrance) {
			mState = State.ERALY_ENTRANCE;
		}
		else {
			//otherwise, this is the real deal
			mState = State.MIDBURN;
		}

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

		Ticket ticket = (Ticket) getIntent().getSerializableExtra("ticketDetails");
		if (ticket != null) {
			mTicket = ticket;
			mTicketNumberTextView.setText(String.valueOf(ticket.getTicketNumber()));
			mTicketOwnerNameTextView.setText(ticket.getTicketOwnerName());
			mTicketTypeTextView.setText(ticket.getTicketType());
			mTicketOwnerIdTextView.setText(ticket.getTicketOwnerId());
			//			mEntranceDateTextView.setText(ticket.getEntranceDate()
			//			                                    .toString());
			//			mTicketFirstEntranceDateTextView.setText(ticket.getFirstEntranceDate()
			//			                                               .toString());
			//			mTicketLastExitDateTextView.setText(ticket.getLastExitDate()
			//			                                          .toString());

			//decide which button to show (entrance/exit)
			if (ticket.isInsideEvent() == 0) {
				//the user is outside the event
				toggleButtonsState(false);
			}
			else if (ticket.isInsideEvent() == 1) {
				//the user is inside the event
				toggleButtonsState(true);
			}
			else {
				Log.e(AppConsts.TAG, "unknown isInsideEvent state. isInsideEvent: " + ticket.isInsideEvent());
			}
			//decide if disabled layout should be displayed
			if (ticket.getIsDisabled() == 1) {
				//show disabled parking
				mDisabledLayout.setVisibility(View.VISIBLE);
			}
			else {
				mDisabledLayout.setVisibility(View.GONE);
			}
		}
	}

	private void toggleButtonsState(boolean isInsideEvent) {
		if (isInsideEvent) {
			mEntranceButton.setVisibility(View.GONE);
			mExitButton.setVisibility(View.VISIBLE);
		}
		else {
			mExitButton.setVisibility(View.GONE);
			mEntranceButton.setVisibility(View.VISIBLE);
		}
	}

	private void bindView() {
		mTicketNumberTextView = (TextView) findViewById(R.id.ticketNumberTextView_ShowActivity);
		mTicketOwnerNameTextView = (TextView) findViewById(R.id.ticketOwnerTextView_ShowActivity);
		mTicketTypeTextView = (TextView) findViewById(R.id.ticketTypeTextView_ShowActivity);
		mEntranceButton = (Button) findViewById(R.id.entranceButton_ShowActivity);
		mExitButton = (Button) findViewById(R.id.exitButton_ShowActivity);
		mCancelButton = (Button) findViewById(R.id.cancelButton_ShowActivity);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_ShowActivity);
		mTicketOwnerIdTextView = (TextView) findViewById(R.id.ticketOwnerIdTextView_ShowActivity);
		mDisabledLayout = (LinearLayout) findViewById(R.id.disabledLayout_ShowActivity);
		//		mTicketFirstEntranceDateTextView = (TextView) findViewById(R.id.firstEntranceDateTextView_ShowActivity);
		//		mTicketLastExitDateTextView = (TextView) findViewById(R.id.lastExitDateTextView_ShowActivity);
	}

	@Override
	public void onBackPressed() {
		Intent upIntent = new Intent(this, MainActivity.class);
		NavUtils.navigateUpTo(this, upIntent);
	}
}
