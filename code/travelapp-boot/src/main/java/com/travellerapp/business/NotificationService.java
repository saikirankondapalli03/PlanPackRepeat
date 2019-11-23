package com.travellerapp.business;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.mail.MessagingException;

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
	void sendEmail(String toEmailId)throws MessagingException, IOException;
	void startNotifications()throws MessagingException, IOException, ParseException;
	List<Destination>	getDestinationByDate(String from, String to) throws ParseException ;
	public void sendEmailScheduler(String emailId, String itineraryName, String firstName , List<Destination> destinations) throws MessagingException, IOException;
}

