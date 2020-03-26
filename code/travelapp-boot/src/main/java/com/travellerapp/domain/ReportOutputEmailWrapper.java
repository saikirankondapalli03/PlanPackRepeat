package com.travellerapp.domain;

import java.io.ByteArrayInputStream;

import lombok.Data;

@Data
public class ReportOutputEmailWrapper {
 
	private ByteArrayInputStream bs;
	private String key;
	public ByteArrayInputStream getBs() {
		return bs;
	}
	public void setBs(ByteArrayInputStream bs) {
		this.bs = bs;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	

}
