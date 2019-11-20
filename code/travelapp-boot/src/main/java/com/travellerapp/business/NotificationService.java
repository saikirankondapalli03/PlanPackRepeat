package com.travellerapp.business;

import java.text.ParseException;
import java.util.List;

import org.bson.types.ObjectId;

import com.travellerapp.domain.Destination;
import com.travellerapp.domain.Notification;


public interface NotificationService {
	Notification getNotification(String Id);
	void saveNotificationsFromDestination(List<Destination> destinations);
	void saveNotifications(List<Notification> notifications);
	void deleteAllNotifications();
	Notification getNotificationById(String id);
	Notification getNotificationByEmail(String email);
	List<Notification> getNotificationByDate(String from, String to) throws ParseException;
	void deleteNotification(ObjectId id);
}
