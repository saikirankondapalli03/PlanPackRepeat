package com.travellerapp.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.travellerapp.domain.Destination;

@Repository
public interface DestinationRepository extends MongoRepository<Destination, String> {

	Destination findDestinationBy_id(ObjectId _id);

}
