package com.midburn.gate.midburngate.model;

import android.support.annotation.NonNull;

/**
 * Created by rotem.matityahu on 3/6/2018.
 */

public class SapakEntrance
		implements Comparable<SapakEntrance> {

	String  mDate;
	int     mPeopleCount;
	String  mCarPlate;
	boolean mIsClosed;

	public SapakEntrance(String date, int peopleCount, String carPlate, boolean isClosed) {
		mDate = date;
		mPeopleCount = peopleCount;
		mCarPlate = carPlate;
		mIsClosed = isClosed;
	}

	@Override
	public int compareTo(@NonNull SapakEntrance sapakEntrance) {
		if (!sapakEntrance.isClosed()) {
			return 1;
		}
		else if (sapakEntrance.isClosed()) {
			return -1;

		}
		else {
			return 0;
		}
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		mDate = date;
	}

	public int getPeopleCount() {
		return mPeopleCount;
	}

	public void setPeopleCount(int peopleCount) {
		mPeopleCount = peopleCount;
	}

	public String getCarPlate() {
		return mCarPlate;
	}

	public void setCarPlate(String carPlate) {
		mCarPlate = carPlate;
	}

	public boolean isClosed() {
		return mIsClosed;
	}

	public void setClosed(boolean closed) {
		mIsClosed = closed;
	}
}
