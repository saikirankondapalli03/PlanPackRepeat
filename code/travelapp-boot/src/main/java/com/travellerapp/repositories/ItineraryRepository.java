package com.travellerapp.repositories;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.travellerapp.domain.Itinerary;

@Repository
public interface ItineraryRepository extends MongoRepository<Itinerary, String> {

	Itinerary findItineraryBy_id(ObjectId _id);
	
	@Query("{ 'email' : { '$regex' : ?0 , $options: 'i'}}")
	List<Itinerary> findItineraryByEmail(String email);
}
