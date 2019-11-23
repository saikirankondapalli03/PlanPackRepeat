package com.travellerapp.business;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.travellerapp.domain.Destination;
import com.travellerapp.domain.Itinerary;
import com.travellerapp.domain.Notification;
import com.travellerapp.email.EmailService;
import com.travellerapp.email.Mail;
import com.travellerapp.repositories.DestinationRepository;
import com.travellerapp.repositories.ItineraryRepository;
import com.travellerapp.repositories.NotificationRepository;
import com.travellerapp.repositories.UserRepository;

@Service
public class NotificationServiceImpl implements NotificationService{

	@Autowired
	private NotificationRepository notificationRepo;
	
	@Autowired 
	private DestinationRepository destRepo;
	
	@Autowired 
	private ItineraryRepository itiRepo;
	
	@Autowired 
	private UserRepository userRepo;
	
	
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
    private EmailService emailService;
	
	
	@Autowired
	private MongoOperations  mongoOps;
	
	@Override
	public Notification getNotification(String id) {
		return notificationRepo.findNotificationBy_id(new ObjectId(id));
	}
	
	@Override
	public void saveNotificationsFromDestination(List<Destination> destinations) {
		List<Notification> notifications= new ArrayList<Notification>();
		destinations.forEach(x->{
			Notification n = new Notification();
			n.setAddress(x.getAddress());
			n.setBudget(x.getBudget());
			n.setEmailId(x.getEmailId());
			n.setImgUrl(x.getImgUrl());
			n.setItineraryId(x.getId());
			n.setPlannedTime(x.getPlannedTime());
			n.setCreatedBy("scheduler");
			n.setCreatedTs(new Timestamp(System.currentTimeMillis()));
			notifications.add(n);
		});
		notifications.forEach(x-> notificationRepo.save(x));
	}
	
	
	@Override
	public void saveNotifications(List<Notification> notifications) {
		 notificationRepo.saveAll(notifications);
	}
	
	@Override
	public List<Notification> getNotificationByDate(String from, String to) throws ParseException {
		List<Notification> notification = new ArrayList<Notification>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date fromDate =removeTime(new Date());
		LocalDateTime localDateTime = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		localDateTime = localDateTime.plusDays(2);
		Date currentDateTwoDays = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	  	notification = notificationRepo.getNotificationByDate(fromDate,currentDateTwoDays,false);
		return notification;
	}
	
	
	@Override
	public List<Destination> getDestinationByDate(String from, String to) throws ParseException {
		List<Destination> notification = new ArrayList<Destination>();
		Date fromDate =removeTime(new Date());
		LocalDateTime localDateTime = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		localDateTime = localDateTime.plusDays(2);
		Date currentDateTwoDays = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		notification = destRepo.getDestinationByDate(fromDate,currentDateTwoDays,false);
		return notification;
	}
	
	public static Date removeTime(Date date) {    
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date);  
        cal.set(Calendar.HOUR_OF_DAY, 0);  
        cal.set(Calendar.MINUTE, 0);  
        cal.set(Calendar.SECOND, 0);  
        cal.set(Calendar.MILLISECOND, 0);  
        return cal.getTime(); 
    }
	
	@Override
	public void deleteAllNotifications(){
		List<Notification> notifications= notificationRepo.findAll();
		notifications.forEach(x-> deleteNotification(x.get_id()));
	} 
	
	@Override
	public Notification getNotificationById(String id) {
		return notificationRepo.findNotificationBy_id(new ObjectId(id));
	}
	
	@Override
	public void deleteNotification(ObjectId id) {
		notificationRepo.delete(notificationRepo.findNotificationBy_id(id));
	}

	@Override
	public Notification getNotificationByEmail(String email) {
		return notificationRepo.findNotificationByemailId(email);
	}

	@Override
	public void sendEmail(String toEmailId) throws MessagingException, IOException {
		Mail mail = new Mail();
		mail.setFrom("planpackrepeat@gmail.com");
		mail.setTo(toEmailId);
		mail.setSubject("Your upcoming trips!");

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("name", "user");
		model.put("journeyTo", "Boston");
		model.put("startsAt", "2:30");
	 	model.put("signature", "plan pack repeat team");
	 	model.put("location", "Stevens Institute of Technology");
		mail.setModel(model);

		emailService.sendSimpleMessage(mail);
		
	}
	
	
	@Override
	public void sendEmailScheduler(String emailId, String itineraryName, String firstName , List<Destination> destinations) throws MessagingException, IOException {
		Mail mail = new Mail();
		mail.setFrom("planpackrepeat@gmail.com");
		mail.setTo(emailId);
		mail.setSubject("Your upcoming trips!");
		ModelMap model= new ModelMap();
		
	 	model.put("name", firstName);
		model.addAttribute("destinations", destinations);
		//model.put("journeyTo", destName);
		//model.put("startsAt", destinationDate);
		
		model.put("itinerary",itineraryName);
	 	model.put("signature", "plan pack repeat team");
	 	model.put("location", "Stevens Institute of Technology");
		mail.setModel(model);

		emailService.sendSimpleMessage(mail);
		
	}
	
	
	
	
	@Override
	public void startNotifications() throws ParseException, MessagingException, IOException{
		List<Destination> destinations= getDestinationByDate(null, null);
		Map<String,List<Destination>> groupByItineraries= destinations.stream().collect(Collectors.groupingBy(Destination::getItineraryId));
		groupByItineraries.entrySet().forEach(x->{
			Itinerary Itinerary= itiRepo.findItineraryBy_id(new ObjectId(x.getKey()));
			String name=Itinerary.getItineraryName();
			String emailId=Itinerary.getEmail();
			String firstName= userRepo.findUserByEmail(emailId).getFirstName();
			List<Destination> iti_destinations= x.getValue();
		 	try {
				this.sendEmailScheduler(emailId, name, firstName, iti_destinations);
			} catch (MessagingException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 	iti_destinations.stream().forEach(z-> z.setNotified(true));
		 	destRepo.saveAll(iti_destinations);
		});
	}
	
	
	
	
	 
}
