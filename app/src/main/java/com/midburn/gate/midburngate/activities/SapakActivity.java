package com.midburn.gate.midburngate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.adapters.sapak.SapakEntranceListAdapter;
import com.midburn.gate.midburngate.model.SapakEntrance;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rotem.matityahu on 3/5/2018.
 */

public class SapakActivity
		extends AppCompatActivity {

	private RecyclerView             mRecyclerView;
	private SapakEntranceListAdapter mAdapter;

	public void createNewEntrance(View view) {
		//TODO new sapak entrance dialog
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
		final ArrayList<SapakEntrance> sapakEntrances = new ArrayList<>();
		sapakEntrances.add(new SapakEntrance("14/7/18", 3, "321323", false));
		sapakEntrances.add(new SapakEntrance("15/7/18", 1, "300323", true));
		sapakEntrances.add(new SapakEntrance("14/7/18", 3, "321323", false));
		sapakEntrances.add(new SapakEntrance("14/7/18", 3, "321323", true));
		sapakEntrances.add(new SapakEntrance("14/7/18", 3, "321323", false));
		sapakEntrances.add(new SapakEntrance("14/7/18", 3, "321323", true));

		Collections.sort(sapakEntrances);

		mAdapter = new SapakEntranceListAdapter(this, "היסטוריית יציאות", sapakEntrances);
		mRecyclerView.setAdapter(mAdapter);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setHasFixedSize(true);
	}
}
