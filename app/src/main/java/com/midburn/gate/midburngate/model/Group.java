package com.midburn.gate.midburngate.model;


import java.io.Serializable;

public class Group
		implements Serializable {

	private int    mId;
	private String mType;
	private String mName;

	public Group(int id, String type, String name) {
		mId = id;
		mType = type;
		mName = name;
	}

	public Group() {
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		mType = type;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	@Override
	public String toString() {
		return "Group{" +
				"mId=" + mId +
				", mType='" + mType + '\'' +
				", mName='" + mName + '\'' +
				'}';
	}
}

