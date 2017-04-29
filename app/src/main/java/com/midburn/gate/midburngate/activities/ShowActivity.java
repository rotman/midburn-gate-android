package com.midburn.gate.midburngate.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.application.MainApplication;
import com.midburn.gate.midburngate.consts.AppConsts;
import com.midburn.gate.midburngate.model.Ticket;
import com.midburn.gate.midburngate.utils.AppUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class ShowActivity
		extends AppCompatActivity {

	private String mTicketId;

	private TextView invitationNumberTextView;
	private TextView ticketNumberTextView;
	private TextView ticketOwnerNameTextView;
	private TextView ticketTypeTextView;
	private TextView entranceDateTextView;
	private TextView eventIdTextView;
	private Button   entranceButton;
	private Button   exitButton;

	public void exit(View view) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpUrl url = new HttpUrl.Builder().scheme("https")
					                                   .host(AppConsts.SERVER_URL)
					                                   .addPathSegment("gate")
					                                   .addPathSegment("event_in")
					                                   .addPathSegment("id")
					                                   .addPathSegment(mTicketId)
					                                   .build();
					Log.d(AppConsts.TAG, "url: " + url);
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

	public void entrance(View view) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					HttpUrl url = new HttpUrl.Builder().scheme("https")
					                                   .host(AppConsts.SERVER_URL)
					                                   .addPathSegment("gate")
					                                   .addPathSegment("event_in")
					                                   .addPathSegment("id")
					                                   //					                                   .addPathSegment(mTicketId)
					                                   .build();
					Log.d(AppConsts.TAG, "url: " + url);
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

		//TODO get from server user status (is user inside/outside)
		boolean isInsideEvent = false;
		toggleButtonsState(isInsideEvent);
		mTicketId = getIntent().getStringExtra("ticketId");

		Ticket ticket = (Ticket) getIntent().getSerializableExtra("ticketDetails");
		if (ticket != null) {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String eventId = sharedPref.getString(getString(R.string.event_id_key), "");
			eventIdTextView.setText(eventId);
			invitationNumberTextView.setText(ticket.getInvitationNumber());
			ticketNumberTextView.setText(ticket.getTicketNumber());
			ticketOwnerNameTextView.setText(ticket.getTicketOwnerName());
			ticketTypeTextView.setText(ticket.getTicketType());
			entranceDateTextView.setText(ticket.getEntranceDate());
		}
	}

	private void toggleButtonsState(boolean isInsideEvent) {
		entranceButton.setEnabled(!isInsideEvent);
		exitButton.setEnabled(isInsideEvent);
		if (isInsideEvent) {
			entranceButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
			entranceButton.setAlpha(.5f);
			exitButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
		}
		else {
			exitButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
			exitButton.setAlpha(.5f);
			entranceButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
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
		invitationNumberTextView = (TextView) findViewById(R.id.invitationNumberTextView_ShowActivity);
		ticketNumberTextView = (TextView) findViewById(R.id.ticketNumberTextView_ShowActivity);
		ticketOwnerNameTextView = (TextView) findViewById(R.id.ticketOwnerTextView_ShowActivity);
		ticketTypeTextView = (TextView) findViewById(R.id.ticketTypeTextView_ShowActivity);
		entranceDateTextView = (TextView) findViewById(R.id.entranceDateTextView_ShowActivity);
		entranceButton = (Button) findViewById(R.id.entranceButton_ShowActivity);
		exitButton = (Button) findViewById(R.id.exitButton_ShowActivity);
		eventIdTextView = (TextView) findViewById(R.id.eventIdTextView_ShowActivity);
	}

	@Override
	public void onBackPressed() {
		Intent upIntent = new Intent(this, MainActivity.class);
		NavUtils.navigateUpTo(this, upIntent);
	}
}
