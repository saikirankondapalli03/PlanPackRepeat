package com.travellerapp.business;

import java.util.List;

import org.bson.types.ObjectId;

import com.travellerapp.domain.User;


public interface UserService {
	List<User> listAllUsers();
	
	User getUserById(String id);
	User createUser(User user);
	User getUserByEmail(String email);
	List<User> createUsers(List<User> users);
	User updateUser(ObjectId id, User user);
	void deleteUser(ObjectId id);
}
