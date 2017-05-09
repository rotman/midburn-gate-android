package com.midburn.gate.midburngate.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

	private TextView mInvitationNumberTextView;
	private TextView mTicketNumberTextView;
	private TextView mTicketOwnerNameTextView;
	private TextView mTicketTypeTextView;
	private TextView mEntranceDateTextView;
	private TextView mTicketFirstEntranceDateTextView;
	private TextView mTicketLastExitDateTextView;
	private TextView mGateCodeTextView;

	private Button      mEntranceButton;
	private Button      mExitButton;
	private ProgressBar mProgressBar;


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

		HttpUrl url = new HttpUrl.Builder().scheme("http")
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

		final ArrayList<Group> groupsArrayList = mTicket.getGroups();
		int groupsArrayListSize = groupsArrayList.size();
		if (groupsArrayListSize > 1) {
			CharSequence groupsArray[] = new CharSequence[groupsArrayListSize];
			for (int i = 0 ; i < groupsArrayListSize ; i++) {
				groupsArray[i] = groupsArrayList.get(i)
				                                .getName();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("בחר קבוצה");
			builder.setItems(groupsArray, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Group selectedGroup = groupsArrayList.get(which);
					Log.d(AppConsts.TAG, selectedGroup.getName() + " was clicked. id: " + selectedGroup.getId());
					mProgressBar.setVisibility(View.VISIBLE);

					SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					String barcode = sharedPref.getString(getString(R.string.barcode), "");

					HttpUrl url = new HttpUrl.Builder().scheme("http")
					                                   .host(AppConsts.SERVER_URL)
					                                   .addPathSegment("api")
					                                   .addPathSegment("gate")
					                                   .addPathSegment("gate-enter")
					                                   .build();

					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("gate_code", mGateCode);
						jsonObject.put("barcode", barcode);
						jsonObject.put("group_id", selectedGroup.getId());

					} catch (JSONException e) {
						Log.e(AppConsts.TAG, e.getMessage());
					}

					AppUtils.doPOSTHttpRequest(url, jsonObject.toString(), mHttpRequestListener);
				}
			});
			builder.show();
		}
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
						               String errorMessage = (String) jsonObject.get("message");
						               AppUtils.createAndShowDialog(ShowActivity.this, "שגיאה", errorMessage, getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
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
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		bindView();

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mGateCode = sharedPref.getString(getString(R.string.gate_code_key), "");

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
			mGateCodeTextView.setText(mGateCode);
			mInvitationNumberTextView.setText(ticket.getInvitationNumber());
			mTicketNumberTextView.setText(ticket.getTicketNumber());
			mTicketOwnerNameTextView.setText(ticket.getTicketOwnerName());
			mTicketTypeTextView.setText(ticket.getTicketType());
			mEntranceDateTextView.setText(ticket.getEntranceDate()
			                                    .toString());
			mTicketFirstEntranceDateTextView.setText(ticket.getFirstEntranceDate()
			                                               .toString());
			mTicketLastExitDateTextView.setText(ticket.getLastExitDate()
			                                          .toString());
			toggleButtonsState(ticket.isInsideEvent());
		}
	}

	private void toggleButtonsState(boolean isInsideEvent) {
		mEntranceButton.setEnabled(!isInsideEvent);
		mExitButton.setEnabled(isInsideEvent);
		if (isInsideEvent) {
			mEntranceButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
			mEntranceButton.setAlpha(.5f);
			mExitButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
		}
		else {
			mExitButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
			mExitButton.setAlpha(.5f);
			mEntranceButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				Intent upIntent = new Intent(this, MainActivity.class);
				NavUtils.navigateUpTo(this, upIntent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void bindView() {
		mInvitationNumberTextView = (TextView) findViewById(R.id.invitationNumberTextView_ShowActivity);
		mTicketNumberTextView = (TextView) findViewById(R.id.ticketNumberTextView_ShowActivity);
		mTicketOwnerNameTextView = (TextView) findViewById(R.id.ticketOwnerTextView_ShowActivity);
		mTicketTypeTextView = (TextView) findViewById(R.id.ticketTypeTextView_ShowActivity);
		mEntranceDateTextView = (TextView) findViewById(R.id.entranceDateTextView_ShowActivity);
		mEntranceButton = (Button) findViewById(R.id.entranceButton_ShowActivity);
		mExitButton = (Button) findViewById(R.id.exitButton_ShowActivity);
		mGateCodeTextView = (TextView) findViewById(R.id.gateCodeTextView_ShowActivity);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_ShowActivity);
		mTicketFirstEntranceDateTextView = (TextView) findViewById(R.id.firstEntranceDateTextView_ShowActivity);
		mTicketLastExitDateTextView = (TextView) findViewById(R.id.lastExitDateTextView_ShowActivity);
	}

	@Override
	public void onBackPressed() {
		Intent upIntent = new Intent(this, MainActivity.class);
		NavUtils.navigateUpTo(this, upIntent);
	}
}
