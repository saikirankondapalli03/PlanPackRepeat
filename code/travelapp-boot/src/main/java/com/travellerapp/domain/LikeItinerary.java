package com.travellerapp.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class LikeItinerary {
	
	@Id
	private ObjectId _id;
	private String itineraryName;
	private String itineraryId;  
	private List<String> listOfUsers;
	public String getItineraryName() {
		return itineraryName;
	}
	public void setItineraryName(String itineraryName) {
		this.itineraryName = itineraryName;
	}
	public String getItineraryId() {
		return itineraryId;
	}
	public void setItineraryId(String itineraryId) {
		this.itineraryId = itineraryId;
	}
	public List<String> getListOfUsers() {
		return listOfUsers;
	}
	public void setListOfUsers(List<String> listOfUsers) {
		this.listOfUsers = listOfUsers;
	}
	
	public String getId() {
		return _id.toHexString();
	}

	public void setId(ObjectId _id) {
		this._id = _id;
	}
	
}
