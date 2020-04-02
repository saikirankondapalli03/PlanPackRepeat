package com.travellerapp.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.travellerapp.business.LikeItineraryService;
import com.travellerapp.domain.LikeItinerary;

@RestController
@RequestMapping(path = "/notification")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LikeItineraryController {
	@Autowired
	private LikeItineraryService lis;

	@GetMapping(path = "/getUsersLikeCountByItinerary/{itineraryId}", produces = "application/json")
	public ResponseEntity<LikeItinerary> getLikeItinerary(@PathVariable String itineraryId) {
		LikeItinerary abcd = lis.retrieveLikeItiByItineraryId(itineraryId);
		if (abcd != null) {
			return ResponseEntity.status(HttpStatus.OK).body(abcd);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@RequestMapping(value = "/saveUserLiking/{email}/{itineraryId}", method = RequestMethod.POST)
	public ResponseEntity<String> insertNotification(String email,String itineraryId) {
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
		lis.savelikeItinerary(email,itineraryId);
		return response;
	}
	
	
	@RequestMapping(value = "/saveUserUnLiking/{email}/{itineraryId}", method = RequestMethod.POST)
	public ResponseEntity<String> UnlikeNotification(String email,String itineraryId) {
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
		lis.saveunLikeItinerary(email,itineraryId);
		return response;
	}

}
