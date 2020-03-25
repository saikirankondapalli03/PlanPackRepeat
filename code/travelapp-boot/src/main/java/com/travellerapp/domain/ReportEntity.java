package com.travellerapp.domain;

import java.util.Date;

public class ReportEntity {

	public ReportEntity() {
		
	}
	
	public String itineararyName; 
	public String destinationName;
	public Date plannedTime;
	public String email;
	public String address;
	public String getItineararyName() {
		return itineararyName;
	}
	public void setItineararyName(String itineararyName) {
		this.itineararyName = itineararyName;
	}
	public String getDestinationName() {
		return destinationName;
	}
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	public Date getPlannedTime() {
		return plannedTime;
	}
	public void setPlannedTime(Date plannedTime) {
		this.plannedTime = plannedTime;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
