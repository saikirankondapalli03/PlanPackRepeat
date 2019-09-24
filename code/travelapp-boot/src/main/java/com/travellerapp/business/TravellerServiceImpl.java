package com.travellerapp.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travellerapp.domain.Traveller;
import com.travellerapp.repositories.TravellerRepository;


@Service
public class TravellerServiceImpl implements TravellerService{

	@Autowired
	private TravellerRepository travellerRepository;
	
	@Override
	public List<Traveller> listAllTravellers() {
		// TODO Auto-generated method stub
		List<Traveller> travellers = new ArrayList<>();
		try {
			 travellers =(List<Traveller>) travellerRepository.findAll();
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		 //fun with Java 8
        return travellers;
	}
	


}
