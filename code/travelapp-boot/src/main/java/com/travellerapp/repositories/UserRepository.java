package com.travellerapp.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.travellerapp.domain.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	
	User findUserBy_id(ObjectId _id);
	
	User findUserByEmail(String email);
}
