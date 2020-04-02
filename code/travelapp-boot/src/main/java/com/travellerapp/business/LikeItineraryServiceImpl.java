package com.travellerapp.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travellerapp.domain.LikeItinerary;
import com.travellerapp.repositories.LikeItineraryRepository;

@Service
public class LikeItineraryServiceImpl implements LikeItineraryService{

	@Autowired
	private LikeItineraryRepository likeItineraryRepo;
		
	public void savelikeItinerary(String email, String itineraryId) {
		LikeItinerary like=retrieveLikeItiByItineraryId(itineraryId);
		if(like==null) 
		{
			LikeItinerary l = new LikeItinerary();
			l.setItineraryId(itineraryId);
			List<String> users=new ArrayList<String>();
			users.add(email);
			l.setListOfUsers(users);
			likeItineraryRepo.save(l);
		}
		else {
			
			List<String> lists= like.getListOfUsers();
			if(!lists.contains(email)) {
				lists.add(email);
				like.setListOfUsers(lists);
				likeItineraryRepo.save(like);	
			}
		}
	}
	
	
	public LikeItinerary retrieveLikeItiByItineraryId(String itineraryId){
		LikeItinerary l=  likeItineraryRepo.findLikeItineraryByItineraryId(itineraryId);
		return l;
	}


	@Override
	public void saveunLikeItinerary(String email, String itineraryId) {
		// TODO Auto-generated method stub
		LikeItinerary like=retrieveLikeItiByItineraryId(itineraryId);
		like.setItineraryId(itineraryId);
		List<String> list= like.getListOfUsers();
		list.remove(email);
		like.setListOfUsers(list);
		likeItineraryRepo.save(like);
	}
}
