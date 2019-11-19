package com.travellerapp.business;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travellerapp.domain.Destination;
import com.travellerapp.repositories.DestinationRepository;


@Service
public class DestinationServiceImpl implements DestinationService{

	@Autowired
	private DestinationRepository destRepo;
	
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
		destRepo.delete(destRepo.findDestinationBy_id(id));
	}
	
	
	@Override
	public void deleteAllDestinations(){
		List<Destination> destinations= destRepo.findAll();
		destinations.forEach(x-> deleteDestination(x.get_id()));
	} 
}
