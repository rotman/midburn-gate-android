package com.midburn.gate.midburngate.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.midburn.gate.midburngate.HttpRequestListener;
import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.consts.AppConsts;
import com.midburn.gate.midburngate.model.Ticket;
import com.midburn.gate.midburngate.utils.AppUtils;

import okhttp3.HttpUrl;
import okhttp3.Response;

public class ShowActivity
		extends AppCompatActivity {

	private String mTicketId;

	private TextView    mInvitationNumberTextView;
	private TextView    mTicketNumberTextView;
	private TextView    mTicketOwnerNameTextView;
	private TextView    mTicketTypeTextView;
	private TextView    mEntranceDateTextView;
	private TextView    mEventIdTextView;
	private Button      mEntranceButton;
	private Button      mExitButton;
	private ProgressBar mProgressBar;


	private HttpRequestListener mHttpRequestListener;

	public void exit(View view) {
		boolean hasInternetConnection = AppUtils.isConnected(this);
		if (!hasInternetConnection) {
			AppUtils.createAndShowDialog(this, getString(R.string.no_network_dialog_title), getString(R.string.no_network_dialog_message), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
			return;
		}
		mProgressBar.setVisibility(View.VISIBLE);
		HttpUrl url = new HttpUrl.Builder().scheme("https")
		                                   .host(AppConsts.SERVER_URL)
		                                   .addPathSegment("gate")
		                                   .addPathSegment("event_in")
		                                   .addPathSegment("id")
		                                   .addPathSegment(mTicketId)
		                                   .build();

		AppUtils.doHttpRequest(url, mHttpRequestListener);
	}

	public void entrance(View view) {
		boolean hasInternetConnection = AppUtils.isConnected(this);
		if (!hasInternetConnection) {
			AppUtils.createAndShowDialog(this, getString(R.string.no_network_dialog_title), getString(R.string.no_network_dialog_message), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
			return;
		}
		mProgressBar.setVisibility(View.VISIBLE);
		HttpUrl url = new HttpUrl.Builder().scheme("https")
		                                   .host(AppConsts.SERVER_URL)
		                                   .addPathSegment("gate")
		                                   .addPathSegment("event_in")
		                                   .addPathSegment("id")
		                                   //					                                   .addPathSegment(mTicketId)
		                                   .build();

		AppUtils.doHttpRequest(url, mHttpRequestListener);
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show);
		getSupportActionBar().setTitle(getString(R.string.ticket_details));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		bindView();

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

		//TODO get from server user status (is user inside/outside)
		boolean isInsideEvent = false;
		toggleButtonsState(isInsideEvent);
		mTicketId = getIntent().getStringExtra("ticketId");

		Ticket ticket = (Ticket) getIntent().getSerializableExtra("ticketDetails");
		if (ticket != null) {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String eventId = sharedPref.getString(getString(R.string.event_id_key), "");
			mEventIdTextView.setText(eventId);
			mInvitationNumberTextView.setText(ticket.getInvitationNumber());
			mTicketNumberTextView.setText(ticket.getTicketNumber());
			mTicketOwnerNameTextView.setText(ticket.getTicketOwnerName());
			mTicketTypeTextView.setText(ticket.getTicketType());
			mEntranceDateTextView.setText(ticket.getEntranceDate());
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
		mEventIdTextView = (TextView) findViewById(R.id.eventIdTextView_ShowActivity);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_ShowActivity);

	}

	@Override
	public void onBackPressed() {
		Intent upIntent = new Intent(this, MainActivity.class);
		NavUtils.navigateUpTo(this, upIntent);
	}
}
