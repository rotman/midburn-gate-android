package com.midburn.gate.midburngate.adapters.sapak;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.midburn.gate.midburngate.R;
import com.midburn.gate.midburngate.contractors.Contractor;
import com.midburn.gate.midburngate.model.Header;

import java.util.ArrayList;

/**
 * Created by rotem.matityahu on 3/6/2018.
 */

public class SapakEntranceListAdapter
		extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int TYPE_HEADER = 0;
	private static final int TYPE_ITEM   = 1;

	private ArrayList<Contractor.SapakEntrance> mSapakEntrances = new ArrayList<>();
	private Context mContext;
	private Header  mHeader;

	private String mBarCode;

	public SapakEntranceListAdapter(Context context, String header, ArrayList<Contractor.SapakEntrance> sapakEntrances) {
		mSapakEntrances = sapakEntrances;
		mContext = context;
		mHeader = new Header();
		mHeader.setHeader(header);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		switch (viewType) {
			case TYPE_HEADER:
				View headerView = LayoutInflater.from(parent.getContext())
				                                .inflate(R.layout.sapak_header, parent, false);
				return new HeaderViewHolder(headerView);
			case TYPE_ITEM:
				View itemView = LayoutInflater.from(parent.getContext())
				                              .inflate(R.layout.sapak_entrance_item, parent, false);
				return new SapakEntranceViewHolder(itemView);
			default:
				return null;
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof HeaderViewHolder) {
			((HeaderViewHolder) holder).bindSapakHeader(mHeader.getHeader());
		}
		else if (holder instanceof SapakEntranceViewHolder) {
			((SapakEntranceViewHolder) holder).bindSapakEntrance(mSapakEntrances.get(position - 1));
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (isPositionHeader(position)) {
			return TYPE_HEADER;
		}

		return TYPE_ITEM;
	}

	@Override
	public int getItemCount() {
		return mSapakEntrances.size() + 1;
	}

	private boolean isPositionHeader(int position) {
		return position == 0;
	}


	// Inner classes
	class SapakEntranceViewHolder
			extends RecyclerView.ViewHolder {

		private TextView mDateTextView;
		private TextView mPersonCountTextView;
		private Button   mExitButton;

		public SapakEntranceViewHolder(View itemView) {
			super(itemView);
			mDateTextView = (TextView) itemView.findViewById(R.id.entrance_date_sapak);
			mPersonCountTextView = (TextView) itemView.findViewById(R.id.entrance_person_count);
			mExitButton = (Button) itemView.findViewById(R.id.exit_sapak_button);
//			mExitButton.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					ContractorsApi.Companion.get()
//					                        .departContractor(mBarCode, String.valueOf(mCarPlateTextView.getText()))
//					                        .enqueue(new Callback<Contractor>() {
//						                        @Override
//						                        public void onResponse(@NonNull Call<Contractor> call, @NonNull Response<Contractor> response) {
//							                        Log.d(AppConsts.TAG, "onResponse");
//						                        }
//
//						                        @Override
//						                        public void onFailure(@NonNull Call<Contractor> call, @NonNull Throwable throwable) {
//							                        Log.d(AppConsts.TAG, "onFailure");
//						                        }
//					                        });
//				}
//			});
		}

		private void bindSapakEntrance(Contractor.SapakEntrance sapakEntrance) {
			mDateTextView.setText(sapakEntrance.getDate());
			mPersonCountTextView.setText(String.valueOf(sapakEntrance.getPeopleCount()));
			if (sapakEntrance.isClosed()) {
				mExitButton.setVisibility(View.INVISIBLE);
			}
			else {
				mExitButton.setVisibility(View.VISIBLE);
			}
		}
	}

	class HeaderViewHolder
			extends RecyclerView.ViewHolder {

		private TextView mHeaderTextView;

		public HeaderViewHolder(View itemView) {
			super(itemView);
			mHeaderTextView = itemView.findViewById(R.id.sapak_list_header);
		}

		private void bindSapakHeader(String text) {
			mHeaderTextView.setText(text);
		}
	}
}
