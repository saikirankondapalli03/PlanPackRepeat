package com.travellerapp.domain;



import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="User")
public class User {
    public User() {

    }

    @Id
    private ObjectId _id;
    
    private String firstName;
   
    private String lastName;
    
    private String email;
    
    private String profileImageUrl;
    
    private String biography;
    
    private List<SocialMediaModel> smdetails;
    
    public List<SocialMediaModel> getSmdetails() {
		return smdetails;
	}

	public void setSmdetails(List<SocialMediaModel> smdetails) {
		this.smdetails = smdetails;
	}

	public String getBiography() {
		return biography;
	}

	public void setBiography(String biography) {
		this.biography = biography;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	private String mobileNumber;
    
    
    
    public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	private String favtDest;

    public String getFavtDest() {
		return favtDest;
	}

	public void setFavtDest(String favtDest) {
		this.favtDest = favtDest;
	}

	public String getId() {
        return _id.toHexString();
    }

    public void setId(ObjectId id) {
        this._id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Traveller [id=" + _id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + "]";
    }
}
