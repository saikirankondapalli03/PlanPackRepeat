package com.travellerapp.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "destination")
public class Destination {
	@Id
	private ObjectId _id;
	private String destName;
	private String address;
	private Date plannedTime;
	private String status;
	private String imgUrl;
	private String emailId;
	private boolean isPublic;
	private BigDecimal budget; 
	private String itineraryId;
	private Timestamp createdTs;
	private Timestamp updatedTs;
	private Timestamp createdBy;
	private Timestamp updatedBy;
	private String latitude;
	private String longitude;
	
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public void setPlannedTime(Date plannedTime) {
		this.plannedTime = plannedTime;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public String getItineraryId() {
		return itineraryId;
	}
	public void setItineraryId(String itineraryId) {
		this.itineraryId = itineraryId;
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
	public BigDecimal getBudget() {
		if (budget==null) return new BigDecimal(0);
		return budget;
	}
	public void setBudget(BigDecimal budget) {
		this.budget = budget;
	}
	public ObjectId get_id() {
		return _id;
	}
	public String getId() {
		return _id.toHexString();
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
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
	public Date getPlannedTime() {
		return plannedTime;
	}
	public void setPlannedTime(Timestamp plannedTime) {
		this.plannedTime = plannedTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	

}
