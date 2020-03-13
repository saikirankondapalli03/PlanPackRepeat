package com.travellerapp.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "Itinerary")
public class Itinerary 
{
	public Itinerary() 
	{

	}

	@Id
	private ObjectId _id;
	private Date startDate;
	private Date endDate;
	private String itineraryName;	
	private String email;
	private boolean isPublic;
	private boolean isActive;
	private String status;
	private int likes;
	private BigDecimal budgetId;
	private Timestamp createdTs;
	private Timestamp updatedTs;
	private Timestamp createdBy;
	private Timestamp updatedBy;
	private List<String> pictures; 
	private String visibilityKey;
	private LikeItinerary likesDetails;
	public LikeItinerary getLikesDetails() {
		return likesDetails;
	}

	public void setLikesDetails(LikeItinerary likesDetails) {
		this.likesDetails = likesDetails;
	}

	public List<String> getPictures() {
		return pictures;
	}

	public void setPictures(List<String> pictures) {
		this.pictures = pictures;
	}

	public String getVisibilityKey() {
		return visibilityKey;
	}

	public void setVisibilityKey(String visibilityKey) {
		this.visibilityKey = visibilityKey;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public String getItineraryName() {
		return itineraryName;
	}

	public void setItineraryName(String itineraryName) {
		this.itineraryName = itineraryName;
	}

	
	
	@DBRef
	private List<Destination> destinations;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCreatedTs() {
		return createdTs;
	}

	public void setCreatedTs(Timestamp createdTs) {
		this.createdTs = createdTs;
	}

	public Timestamp getUpdatedTs() {
		return updatedTs;
	}

	public void setUpdatedTs(Timestamp updatedTs) {
		this.updatedTs = updatedTs;
	}

	public Timestamp getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Timestamp createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Timestamp updatedBy) {
		this.updatedBy = updatedBy;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	
	
	
	public BigDecimal getBudgetId() {
		if(budgetId == null) return new BigDecimal(0); 
		return budgetId;
	}

	public void setBudgetId(BigDecimal budgetId) {
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
