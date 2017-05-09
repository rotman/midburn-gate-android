package com.midburn.gate.midburngate.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Ticket
		implements Serializable {

	private String           mInvitationNumber;
	private String           mTicketNumber;
	private String           mTicketOwnerName;
	private String           mTicketType;
	private Date             mEntranceDate;
	private Date             mFirstEntranceDate;
	private Date             mLastExitDate;
	private boolean          mIsInsideEvent;
	private int              mEntranceGroupId;
	private ArrayList<Group> mGroups;

	public Ticket(String invitationNumber, String ticketNumber, String ticketOwnerName, String ticketType, Date entranceDate) {
		this.mInvitationNumber = invitationNumber;
		this.mTicketNumber = ticketNumber;
		this.mTicketOwnerName = ticketOwnerName;
		this.mTicketType = ticketType;
		this.mEntranceDate = entranceDate;
	}

	public Ticket() {

	}

	public String getInvitationNumber() {
		return mInvitationNumber;
	}

	public String getTicketNumber() {
		return mTicketNumber;
	}

	public String getTicketOwnerName() {
		return mTicketOwnerName;
	}

	public String getTicketType() {
		return mTicketType;
	}

	public Date getEntranceDate() {
		return mEntranceDate;
	}

	public boolean isInsideEvent() {
		return mIsInsideEvent;
	}

	public Date getFirstEntranceDate() {
		return mFirstEntranceDate;
	}

	public void setFirstEntranceDate(Date firstEntranceDate) {
		mFirstEntranceDate = firstEntranceDate;
	}

	public Date getLastExitDate() {
		return mLastExitDate;
	}

	public void setLastExitDate(Date lastExitDate) {
		mLastExitDate = lastExitDate;
	}

	public int getEntranceGroupId() {
		return mEntranceGroupId;
	}

	public void setEntranceGroupId(int entranceGroupId) {
		mEntranceGroupId = entranceGroupId;
	}

	public ArrayList<Group> getGroups() {
		return mGroups;
	}

	public void setGroups(ArrayList<Group> groups) {
		mGroups = groups;
	}

	public void setInsideEvent(boolean insideEvent) {
		mIsInsideEvent = insideEvent;
	}

	public void setInvitationNumber(String invitationNumber) {
		mInvitationNumber = invitationNumber;
	}

	public void setTicketNumber(String ticketNumber) {
		mTicketNumber = ticketNumber;
	}

	public void setTicketOwnerName(String ticketOwnerName) {
		mTicketOwnerName = ticketOwnerName;
	}

	public void setTicketType(String ticketType) {
		mTicketType = ticketType;
	}

	public void setEntranceDate(Date entranceDate) {
		mEntranceDate = entranceDate;
	}
}
