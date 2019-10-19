package com.travellerapp.business;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travellerapp.domain.Itinerary;
import com.travellerapp.repositories.ItineraryRepository;


@Service
public class ItineraryServiceImpl implements ItineraryService{

	@Autowired
	private ItineraryRepository itineraryRepo;
	
	@Override
	public List<Itinerary> listAllItineraries() {
		return (List<Itinerary>) itineraryRepo.findAll();
	}
	
	@Override
	public Itinerary getActiveItineraryByEmail(String email) {
		return itineraryRepo.findItineraryByEmail(email);
	}

	@Override
	public Itinerary createItinerary(Itinerary itinerary) {
		Itinerary currentIt=itineraryRepo.findItineraryByEmail(itinerary.getEmail());
		if(currentIt!= null) {
			currentIt.getDestinations().addAll(itinerary.getDestinations());
			return updateItinerary(new ObjectId(currentIt.getId()), itinerary);
		}
		return itineraryRepo.save(itinerary);
	}
	
	@Override
	public Itinerary updateItinerary(ObjectId id, Itinerary itinerary) {
		itinerary.setId(id);
		return itineraryRepo.save(itinerary);
	}
	
	@Override
	public void deleteItinerary(ObjectId id) {
		itineraryRepo.delete(itineraryRepo.findItineraryBy_id(id));
	}
	
	
	@Override
	public List<Itinerary> createItineraries(List<Itinerary> itinerary) {
		return itineraryRepo.saveAll(itinerary);
	}
	

	
}
