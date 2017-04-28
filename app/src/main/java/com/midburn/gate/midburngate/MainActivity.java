package com.midburn.gate.midburngate;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity
		extends AppCompatActivity {

	private static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
	private static final String TAG         = "MIDBURN_GATE";

	private EditText invitationNumberEditText;
	private EditText TicketNumberEditText;

	private DialogInterface.OnClickListener mNeedToDownloadScannerAppClickListener;
	private DialogInterface.OnClickListener mBackPressedClickListener;

	public void manuallyInput(View view) {
		String invitationNumber = invitationNumberEditText.getText()
		                                                  .toString();
		String ticketNumber = TicketNumberEditText.getText()
		                                          .toString();
		if (TextUtils.isEmpty(invitationNumber) || TextUtils.isEmpty(ticketNumber)) {
			createAndShowDialog(this, getString(R.string.manually_validate_dialog_title), getString(R.string.manually_validate_dialog_message), getString(R.string.ok), null, null, android.R.drawable.ic_dialog_alert);
		}
		else {
			//TODO call server
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
			createAndShowDialog(this, "סורק לא נמצא", "להוריד אפליקציית סורק?", "כן", "לא", mNeedToDownloadScannerAppClickListener, android.R.drawable.ic_dialog_alert);
		}

		//		Intent intent = new Intent(this, ShowActivity.class);
		//		String date = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", new java.util.Date()));
		//		//for now using mock ticket
		//		Ticket ticket = new Ticket("123456", "876543", "רותם מתיתיהו", "רגיל", date);
		//		intent.putExtra("ticketDetails", ticket);
		//		startActivity(intent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
				toast.show();
				//TODO call server
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
				finishAffinity();
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

	private static void createAndShowDialog(final Context context, String title, String message, @Nullable String positiveButtonText, @Nullable String negativeButtonText, @Nullable DialogInterface.OnClickListener onClickListener, int iconId) {
		new AlertDialog.Builder(context).setTitle(title)
		                                .setMessage(message)
		                                .setPositiveButton(positiveButtonText, onClickListener)
		                                .setNegativeButton(negativeButtonText, null)
		                                .setIcon(iconId)
		                                .show();
	}

	@Override
	public void onBackPressed() {
		createAndShowDialog(this, "האם ברצונך לצאת?", "", "כן", "לא", mBackPressedClickListener, android.R.drawable.ic_dialog_alert);
	}
}
