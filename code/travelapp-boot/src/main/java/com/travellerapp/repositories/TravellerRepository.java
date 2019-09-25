package com.travellerapp.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.travellerapp.domain.Traveller;

@Repository
public interface TravellerRepository extends MongoRepository<Traveller, String> {
	
}
