package com.travellerapp.business;

import com.travellerapp.domain.LikeItinerary;

public interface LikeItineraryService {
	void savelikeItinerary(String email, String  itineraryId);
	LikeItinerary retrieveLikeItiByItineraryId(String itineraryId);
	void saveunLikeItinerary(String email, String  itineraryId);
	
}
