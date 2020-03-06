package com.travellerapp.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.travellerapp.domain.LikeItinerary;

@Repository
public interface LikeItineraryRepository extends MongoRepository<LikeItinerary, String> {

	LikeItinerary findLikeItineraryBy_id(ObjectId _id);
	
	LikeItinerary findLikeItineraryByItineraryId(String itineraryId);

}