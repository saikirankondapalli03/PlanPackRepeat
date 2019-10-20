package com.travellerapp.domain;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "Itinerary")
public class Itinerary {
	public Itinerary() {

	}

	@Id
	private ObjectId _id;
	private Date startDate;
	private Date endDate;
	private String email;
	private boolean isPublic;
	private boolean isActive;
	private ObjectId budgetId;
	
	public Itinerary(ObjectId _id, Date startDate, Date endDate, String email, boolean isPublic, boolean isActive, ObjectId budgetId,
			List<Destination> destinations) {
		super();
		this._id = _id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.email = email;
		this.isPublic = isPublic;
		this.budgetId = budgetId;
		this.destinations = destinations;
		this.isActive=isActive;
	}

	@DBRef
	private List<Destination> destinations;

	
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	
	
	
	public ObjectId getBudgetId() {
		return budgetId;
	}

	public void setBudgetId(ObjectId budgetId) {
		this.budgetId = budgetId;
	}

	public String getId() {
		return _id.toHexString();
	}

	public void setId(ObjectId _id) {
		this._id = _id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public List<Destination> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<Destination> destinations) {
		this.destinations = destinations;
	}

}
