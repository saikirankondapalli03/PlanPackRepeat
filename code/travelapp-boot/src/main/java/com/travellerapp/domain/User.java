package com.travellerapp.domain;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="User")
public class User {
    public User() {

    }

    @Id
    private ObjectId _id;
    
    @NotEmpty(message = "first name must not be empty")
    private String firstName;
    
    @NotEmpty(message = "last name must not be empty")
    private String lastName;
    
    @NotEmpty(message = "email must not be empty")
    @Email(message = "email should be a valid email")
    private String email;
    
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
