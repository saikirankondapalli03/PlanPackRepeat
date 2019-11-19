package com.travellerapp.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travellerapp.domain.Destination;
import com.travellerapp.domain.Notification;
import com.travellerapp.repositories.NotificationRepository;


@Service
public class NotificationServiceImpl implements NotificationService{

	@Autowired
	private NotificationRepository notificationRepo;
	
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
	  notificationRepo.saveAll(notifications);
	}
	
	
	@Override
	public void saveNotifications(List<Notification> notifications) {
		 notificationRepo.saveAll(notifications);
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
		return notificationRepo.findNotificationBy_email(email);
	}
	
	
	
	
	
	
	 
}
