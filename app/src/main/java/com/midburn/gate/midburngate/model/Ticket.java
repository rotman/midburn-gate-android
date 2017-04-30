package com.midburn.gate.midburngate.model;

import java.io.Serializable;

public class Ticket implements Serializable{
	private String mInvitationNumber;
	private String mTicketNumber;
	private String mTicketOwnerName;
	private String mTicketType;
	private String mEntranceDate;

	public Ticket(String invitationNumber, String ticketNumber, String ticketOwnerName, String ticketType, String entranceDate) {
		this.mInvitationNumber = invitationNumber;
		this.mTicketNumber = ticketNumber;
		this.mTicketOwnerName = ticketOwnerName;
		this.mTicketType = ticketType;
		this.mEntranceDate = entranceDate;
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

	public String getEntranceDate() {
		return mEntranceDate;
	}
}
