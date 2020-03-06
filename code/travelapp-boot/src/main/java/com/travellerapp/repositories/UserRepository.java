package com.travellerapp.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.travellerapp.domain.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	
	User findUserBy_id(ObjectId _id);
	
	@Query("{ 'email' : { '$regex' : ?0 , $options: 'i'}}")
	User findUserByEmail(String email);
	
	// *find => Model(in this case  it is User)  => By => attribute
	@Query("{ 'lastName' : { '$regex' : ?0 , $options: 'i'}}")
	User findUserByLastName(String lastName);
}
