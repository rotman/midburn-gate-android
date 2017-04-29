package com.midburn.gate.midburngate.model;

import java.io.Serializable;

public class Ticket implements Serializable{
	private String invitationNumber;
	private String ticketNumber;
	private String ticketOwnerName;
	private String ticketType;
	private String entranceDate;

	public Ticket(String invitationNumber, String ticketNumber, String ticketOwnerName, String ticketType, String entranceDate) {
		this.invitationNumber = invitationNumber;
		this.ticketNumber = ticketNumber;
		this.ticketOwnerName = ticketOwnerName;
		this.ticketType = ticketType;
		this.entranceDate = entranceDate;
	}

	public String getInvitationNumber() {
		return invitationNumber;
	}

	public String getTicketNumber() {
		return ticketNumber;
	}

	public String getTicketOwnerName() {
		return ticketOwnerName;
	}

	public String getTicketType() {
		return ticketType;
	}

	public String getEntranceDate() {
		return entranceDate;
	}
}
