package com.travellerapp.domain;

public class Destination {

	private String destName;
	private String address;
	private String plannedTime;
	private String status;
	private String imgUrl;

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getDestName() {
		return destName;
	}

	public void setDestName(String destName) {
		this.destName = destName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	
	public String getPlannedTime() {
		return plannedTime;
	}

	public void setPlannedTime(String plannedTime) {
		this.plannedTime = plannedTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
