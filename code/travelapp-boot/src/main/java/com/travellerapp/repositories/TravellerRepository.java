package com.travellerapp.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.travellerapp.domain.Traveller;

@Repository
public interface TravellerRepository extends CrudRepository<Traveller, String> {
	
}
