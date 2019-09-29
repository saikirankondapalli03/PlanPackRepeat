package com.travellerapp.cdd;

import java.io.Serializable;

import lombok.Data;

@Data
@SuppressWarnings({"serial"})
public class Message implements Serializable {
	private int id;
	private String key;
	private String description;
	private String type;
	
	
	public Message(){
		
	}
	
	public Message(String key, String description) {
		this.key = key;
		this.description = description;
	}
	
	public Message(int id, String description) {
		super();
		this.id = id;
		this.description = description;
	}

	public Message(int id, String key, String description) {
		super();
		this.id = id;
		this.key = key;
		this.description = description;
	}

}
