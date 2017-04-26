package com.midburn.gate.midburngate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class MainActivity
		extends AppCompatActivity {

	private EditText invitationNumberEditText;
	private EditText TicketNumberEditText;

	public void manuallyInput(View view) {
		String invitationNumber = invitationNumberEditText.getText()
		                                                  .toString();
		String ticketNumber = TicketNumberEditText.getText()
		                                          .toString();
		if (TextUtils.isEmpty(invitationNumber) || TextUtils.isEmpty(ticketNumber)) {
			createAndShowDialog(this, getString(R.string.manually_validate_dialog_title), getString(R.string.manually_validate_dialog_message), getString(R.string.ok), null, android.R.drawable.ic_dialog_alert);
		}
		else {
			//TODO call server
		}
	}

	public void scanBarcode(View view) {
		//TODO open scanner
		Intent intent = new Intent(this, ShowActivity.class);
		String date = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", new java.util.Date()));
		//for now using mock ticket
		Ticket ticket = new Ticket("123456", "876543", "רותם מתיתיהו", "רגיל", date);
		intent.putExtra("ticketDetails", ticket);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		bindView();
	}

	private void bindView() {
		invitationNumberEditText = (EditText) findViewById(R.id.invitationNumberEditText_MainActivity);
		TicketNumberEditText = (EditText) findViewById(R.id.ticketNumberEditText_MainActivity);
	}

	private static void createAndShowDialog(Context context, String title, String message, @Nullable String positiveButtonText, @Nullable String negativeButtonText, int iconId) {
		new AlertDialog.Builder(context).setTitle(title)
		                                .setMessage(message)
		                                .setPositiveButton(positiveButtonText, null)
		                                .setNegativeButton(negativeButtonText, null)
		                                .setIcon(iconId)
		                                .show();
	}
}
