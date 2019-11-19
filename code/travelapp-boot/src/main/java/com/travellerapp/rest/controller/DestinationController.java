package com.travellerapp.rest.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.travellerapp.business.DestinationService;
import com.travellerapp.domain.Destination;

@RestController
@RequestMapping(path = "/destination")
public class DestinationController 
{
	@Autowired
	private DestinationService destinationService;
    
    @RequestMapping(value = "/deleteAllDestinations", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAllDestinations() 
	{
    	ResponseEntity<String> response= new ResponseEntity<String>(HttpStatus.OK);
    	destinationService.deleteAllDestinations();
		return response;
	}
    
    

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Destination updateDestination(@Valid @RequestBody Destination destination) 
	{
    	return destinationService.saveDestination(destination);
	}
    
    
 }
