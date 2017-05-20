package com.midburn.gate.midburngate.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Ticket
		implements Serializable {

	private String           mBarCode;
	private String           mInvitationNumber;
	private int              mTicketNumber;
	private String           mTicketOwnerName;
	private String           mTicketType;
	private Date             mEntranceDate;
	private Date             mFirstEntranceDate;
	private Date             mLastExitDate;
	private int              mIsInsideEvent;
	private int              mEntranceGroupId;
	private String           mTicketOwnerId;
	private int              mIsDisabled;
	private String			 mGateStatus;
	private ArrayList<Group> mGroups;

	public Ticket(String invitationNumber, int ticketNumber, String ticketOwnerName, String ticketType, Date entranceDate) {
		this.mInvitationNumber = invitationNumber;
		this.mTicketNumber = ticketNumber;
		this.mTicketOwnerName = ticketOwnerName;
		this.mTicketType = ticketType;
		this.mEntranceDate = entranceDate;
	}

	public Ticket() {

	}

	public String getGateStatus() {
		return mGateStatus;
	}

	public void setGateStatus(String gateStatus) {
		this.mGateStatus = gateStatus;
	}

	public String getInvitationNumber() {
		return mInvitationNumber;
	}

	public int getTicketNumber() {
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

	public int isInsideEvent() {
		return mIsInsideEvent;
	}

	public Date getFirstEntranceDate() {
		return mFirstEntranceDate;
	}

	public void setFirstEntranceDate(Date firstEntranceDate) {
		mFirstEntranceDate = firstEntranceDate;
	}

	public String getBarCode() {
		return mBarCode;
	}

	public void setBarCode(String barCode) {
		mBarCode = barCode;
	}

	public int getIsInsideEvent() {
		return mIsInsideEvent;
	}

	public void setIsInsideEvent(int isInsideEvent) {
		mIsInsideEvent = isInsideEvent;
	}

	public int getIsDisabled() {
		return mIsDisabled;
	}

	public void setIsDisabled(int isDisabled) {
		mIsDisabled = isDisabled;
	}

	public String getTicketOwnerId() {
		return mTicketOwnerId;
	}

	public void setTicketOwnerId(String ticketOwnerId) {
		mTicketOwnerId = ticketOwnerId;
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

	public void setInsideEvent(int insideEvent) {
		mIsInsideEvent = insideEvent;
	}

	public void setTicketNumber(int ticketNumber) {
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

	@Override
	public String toString() {
		return "Ticket{" +
				"mGateStatus='" + mGateStatus + '\'' +
				", mBarCode='" + mBarCode + '\'' +
				", mInvitationNumber='" + mInvitationNumber + '\'' +
				", mTicketNumber=" + mTicketNumber +
				", mTicketOwnerName='" + mTicketOwnerName + '\'' +
				", mTicketType='" + mTicketType + '\'' +
				", mEntranceDate=" + mEntranceDate +
				", mFirstEntranceDate=" + mFirstEntranceDate +
				", mLastExitDate=" + mLastExitDate +
				", mIsInsideEvent=" + mIsInsideEvent +
				", mEntranceGroupId=" + mEntranceGroupId +
				", mTicketOwnerId='" + mTicketOwnerId + '\'' +
				", mIsDisabled=" + mIsDisabled +
				", mGroups=" + mGroups +
				'}';
	}
}
