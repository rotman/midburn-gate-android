package com.midburn.gate.midburngate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class ShowActivity
		extends AppCompatActivity {

	private TextView invitationNumberTextView;
	private TextView ticketNumberTextView;
	private TextView ticketOwnerNameTextView;
	private TextView ticketTypeTextView;
	private TextView entranceDateTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show);
		getSupportActionBar().setTitle(getString(R.string.ticket_details));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		bindView();
		Ticket ticket = (Ticket) getIntent().getSerializableExtra("ticketDetails");
		if (ticket != null) {
			invitationNumberTextView.setText(ticket.getInvitationNumber());
			ticketNumberTextView.setText(ticket.getTicketNumber());
			ticketOwnerNameTextView.setText(ticket.getTicketOwnerName());
			ticketTypeTextView.setText(ticket.getTicketType());
			entranceDateTextView.setText(ticket.getEntranceDate());
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
	}
}
