package com.travellerapp.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travellerapp.domain.Destination;
import com.travellerapp.domain.Itinerary;
import com.travellerapp.repositories.DestinationRepository;
import com.travellerapp.repositories.ItineraryRepository;


@Service
public class DestinationServiceImpl implements DestinationService{

	@Autowired
	private DestinationRepository destRepo;
	
	@Autowired
	private ItineraryRepository itineraryRepo;
	
	@Override
	public Destination getDestinationById(String email) {
		return destRepo.findDestinationBy_id(new ObjectId(email));
	}

	@Override
	public Destination saveDestination(Destination dest) {
		return destRepo.save(dest);
	}
	
	@Override
	public void deleteDestination(ObjectId id) {
		Destination dest=destRepo.findDestinationBy_id(id);
		Itinerary it= itineraryRepo.findItineraryBy_id(new ObjectId(dest.getId()));
		List<Destination> list =new ArrayList<Destination>();
		it.getDestinations().stream().filter(x->x.getId().equalsIgnoreCase(dest.getId())).forEach(y->{
			list.add(y);
		});
		if(!CollectionUtils.isEmpty(list)) 
		{
			it.setDestinations(list);
			itineraryRepo.save(it);
		}
		destRepo.delete(dest);
	}
	
	
	@Override
	public void deleteAllDestinations(){
		List<Destination> destinations= destRepo.findAll();
		destinations.forEach(x-> deleteDestination(x.get_id()));
	} 
}
