package com.travellerapp.business;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.travellerapp.domain.Destination;
import com.travellerapp.domain.Notification;
import com.travellerapp.repositories.NotificationRepository;


@Service
public class NotificationServiceImpl implements NotificationService{

	@Autowired
	private NotificationRepository notificationRepo;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
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
		Date toDate = formatter.parse(to);
		notification = notificationRepo.getNotificationByDate(fromDate,currentDateTwoDays);
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
	
	
	
	
	
	
	 
}
