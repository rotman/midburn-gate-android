package com.midburn.gate.midburngate.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.midburn.gate.midburngate.R;

public class CarsDialog
		extends Dialog {

	private Button mCarEnterButton;
	private Button mCarExitButton;

	private View.OnClickListener mOnCarEnterListener;
	private View.OnClickListener mOnCarExitListener;

	public CarsDialog(Context context, View.OnClickListener onCarEnterListener, View.OnClickListener onCarExitListener) {
		super(context);
		mOnCarEnterListener = onCarEnterListener;
		mOnCarExitListener = onCarExitListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cars_dialog);
		mCarEnterButton = findViewById(R.id.carEnter_button);
		mCarExitButton = findViewById(R.id.carExit_button);
		if (mOnCarEnterListener != null) {
			mCarEnterButton.setOnClickListener(mOnCarEnterListener);
		}
		if (mOnCarExitListener != null) {
			mCarExitButton.setOnClickListener(mOnCarExitListener);
		}
	}
}
