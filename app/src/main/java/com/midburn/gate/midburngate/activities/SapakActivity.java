package com.midburn.gate.midburngate.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.adapters.sapak.SapakEntranceListAdapter;
import com.midburn.gate.midburngate.network.Contractor;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rotem.matityahu on 3/5/2018.
 */

public class SapakActivity
		extends AppCompatActivity {

	private RecyclerView             mRecyclerView;
	private SapakEntranceListAdapter mAdapter;

	private String mBarCode;

	public void createNewEntrance(View view) {
		final EditText input = new EditText(this);
		new AlertDialog.Builder(this).setTitle("הכנס מספר רכב")
		                             .setView(input)
		                             .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			                             @Override
			                             public void onClick(DialogInterface dialog, int which) {
				                             //				                             Log.d(AppConsts.TAG, String.valueOf(input.getText()));
				                             //				                             ContractorsApi.Companion.get()
				                             //				                                                     .admitContractor(mBarCode, String.valueOf(input.getText()))
				                             //				                                                     .enqueue(new Callback<Contractor>() {
				                             //					                                                     @Override
				                             //					                                                     public void onResponse(Call<Contractor> call, Response<Contractor> response) {
				                             //						                                                     Log.d(AppConsts.TAG, "onResponse");
				                             //					                                                     }
				                             //
				                             //					                                                     @Override
				                             //					                                                     public void onFailure(Call<Contractor> call, Throwable t) {
				                             //						                                                     Log.d(AppConsts.TAG, "onFailure");
				                             //					                                                     }
				                             //				                                                     });
			                             }
		                             })
		                             .show();
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sapak);
		mRecyclerView = findViewById(R.id.sapak_entrance_recycler_view);
		getEntrances();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	private void getEntrances() {
		final ArrayList<Contractor.SapakEntrance> sapakEntrances = new ArrayList<>();

		Collections.sort(sapakEntrances);

		mAdapter = new SapakEntranceListAdapter(this, "היסטוריית יציאות", sapakEntrances);
		mRecyclerView.setAdapter(mAdapter);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setHasFixedSize(true);
	}
}
