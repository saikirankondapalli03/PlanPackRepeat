package com.travellerapp.business;

import org.bson.types.ObjectId;

import com.travellerapp.domain.Destination;


public interface DestinationService {
	Destination getDestinationById(String Id);
	Destination saveDestination(Destination destination);
	void deleteDestination(ObjectId id);
}
