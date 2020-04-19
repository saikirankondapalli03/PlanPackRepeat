package com.travellerapp.business;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.travellerapp.cdd.ItineraryStatus;
import com.travellerapp.domain.Destination;
import com.travellerapp.domain.Itinerary;
import com.travellerapp.domain.LikeItinerary;
import com.travellerapp.domain.ReportOutputEmailWrapper;
import com.travellerapp.repositories.DestinationRepository;
import com.travellerapp.repositories.ItineraryRepository;
import com.travellerapp.util.PdfReportUtil;


@Service
public class ItineraryServiceImpl implements ItineraryService{

	@Autowired
	private ItineraryRepository itineraryRepo;
	
	@Autowired
	private LikeItineraryServiceImpl likeRepo;
	
	
	@Autowired
	private DestinationRepository destRepo;
	
	
	@Override
	public List<Itinerary> listAllItineraries() {
		List<Itinerary> list = (List<Itinerary>) itineraryRepo.findAll();
		list.stream().forEach(x -> {
				LikeItinerary lk = likeRepo.retrieveLikeItiByItineraryId(x.getId());
					x.setLikesDetails(lk);
			
		});
		return list;
	}
	
	@Override
	public List<Itinerary> getActiveItineraryByEmail(String email) {
		return itineraryRepo.findItineraryByEmail(email);
	}
	
	
	@Override
	public Itinerary getActiveItineraryById(String Id) {
		Itinerary itinerary =itineraryRepo.findItineraryBy_id(new ObjectId(Id));
		LikeItinerary  likesDetails=  likeRepo.retrieveLikeItiByItineraryId(Id);
		itinerary.setLikesDetails(likesDetails);
		return itinerary;
	}


	@Override
	public Itinerary createItinerary(Itinerary itinerary) {
		Itinerary copyObj= itinerary;
		//Itinerary currentIt=itineraryRepo.findItineraryByEmail(itinerary.getEmail());
		itinerary.getDestinations().forEach(x->{
			x.setStatus(ItineraryStatus.ABOUT_TO_TRAVEL.getValue());
			x.setEmailId(copyObj.getEmail());
			Destination des= destRepo.save(x);
			x= des; 
		});
		itinerary.setStatus(ItineraryStatus.ABOUT_TO_TRAVEL.getValue());
		itinerary= itineraryRepo.save(itinerary);
		final String itineraryId= itinerary.getId();
		itinerary.getDestinations().forEach(x->{
			x.setItineraryId(itineraryId);
			destRepo.save(x);
		});
		return itinerary;
	}
	
	@Override
	public Itinerary updateItinerary(ObjectId id, Itinerary itinerary) {
		itinerary.setId(id);
		return itineraryRepo.save(itinerary);
	}
	
	@Override
	public void deleteItinerary(ObjectId id) {
		Itinerary itr= itineraryRepo.findItineraryBy_id(id);
		if(itr!=null) {
			destRepo.deleteAll(itr.getDestinations());
			itineraryRepo.delete(itr);
		}
	}
	
	
	@Override
	public List<Itinerary> createItineraries(List<Itinerary> itinerary) {
		return itineraryRepo.saveAll(itinerary);
	}
	
	@Override 
	public void deleteDestinationFromItinerary(String email,List<String> destinationIds) {
		//get current itinerary based on email 
		Itinerary itr= getActiveItineraryByEmail(email).get(0);
		if(itr!=null)
		{
			List<Destination> destinations= itr.getDestinations();
			//getCurrentDestId's
			List<String> existingDest= destinations.stream().map(Destination::getId).collect(Collectors.toList());
			//check what can be deleted from existing
			List<String> toBeDeletedDest = existingDest.stream()
	                .filter(destinationIds::contains)
	                .collect(Collectors.toList());
			//if toBeDeletedDest is not empty , then go ahead and delete it
			if(!CollectionUtils.isEmpty(toBeDeletedDest)) {
				toBeDeletedDest.forEach(x->destRepo.deleteById(x));
			}
			//delink the deleteddest from itinerary
			List<Destination> updateActiveDest=  destinations.stream().filter(x->!toBeDeletedDest.contains(x.getId())).collect(Collectors.toList());
			//update Active destination
			itr.setDestinations(updateActiveDest);
			updateItinerary(new ObjectId(itr.getId()), itr);
		}
		
		
	}

	@Override
	public ReportOutputEmailWrapper getAllItinerariesReport() {
		ReportOutputEmailWrapper s= new ReportOutputEmailWrapper();
		List<Itinerary> list = listAllItineraries();
		ByteArrayInputStream br= PdfReportUtil.citiesReport(list);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String fullValue="All_Itineraries"+ "_" + dtf.format(now);
		s.setKey(fullValue);
		s.setBs(br);
		return s;
	}
	
	@Override
	public ReportOutputEmailWrapper getItineraryByIdReport(String id) {
		ReportOutputEmailWrapper s= new ReportOutputEmailWrapper();
		Itinerary itinerary= getActiveItineraryById(id);
		List<Itinerary> list= new ArrayList<Itinerary>();
		list.add(itinerary);
		ByteArrayInputStream br= PdfReportUtil.citiesReport(list);
		s.setBs(br);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String fullValue="id"+ "_" + dtf.format(now);
		s.setKey(fullValue);
		return s;
	}
	
	
	@Override
	public ReportOutputEmailWrapper getItineraryByEmailReport(String email) {
		ReportOutputEmailWrapper s= new ReportOutputEmailWrapper();
		List<Itinerary> list= getActiveItineraryByEmail(email);
		ByteArrayInputStream br= PdfReportUtil.citiesReport(list);
		s.setBs(br);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String fullValue=email+ "_" + dtf.format(now);
		s.setKey(fullValue);
		return s;
	}
	
	
}
