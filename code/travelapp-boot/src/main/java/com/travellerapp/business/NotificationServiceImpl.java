package com.travellerapp.business;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Date fromDate = formatter.parse(from);
		Date toDate = formatter.parse(to);
		notification = notificationRepo.getNotificationByDate(fromDate,toDate);
		return notification;
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
