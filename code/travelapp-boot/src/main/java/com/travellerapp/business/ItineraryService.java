package com.travellerapp.business;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.bson.types.ObjectId;

import com.travellerapp.domain.Itinerary;


public interface ItineraryService {
	List<Itinerary> listAllItineraries();
	List<Itinerary> getActiveItineraryByEmail(String email);
	Itinerary createItinerary(Itinerary itinerary);
	List<Itinerary> createItineraries(List<Itinerary> itinerary);
	Itinerary updateItinerary(ObjectId id, Itinerary itinerary);
	void deleteItinerary(ObjectId id);
	void deleteDestinationFromItinerary(String email,List<String> destinationIds);
	Itinerary getActiveItineraryById(String Id);
	ByteArrayInputStream generateAndDownloadItineararyReport(); 
	
}
