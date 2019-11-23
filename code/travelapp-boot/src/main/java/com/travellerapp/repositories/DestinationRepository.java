package com.travellerapp.repositories;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.travellerapp.domain.Destination;

@Repository
public interface DestinationRepository extends MongoRepository<Destination, String> {

	Destination findDestinationBy_id(ObjectId _id);
	
	@Query("{'plannedTime':{ $gte: ?0, $lte: ?1},'isNotified':false}")              
	public List<Destination> getDestinationByDate(Date from,Date To,boolean isNotified); 

}
