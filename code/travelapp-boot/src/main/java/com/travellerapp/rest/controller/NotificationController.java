package com.travellerapp.rest.controller;

import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.travellerapp.business.NotificationService;
import com.travellerapp.domain.Destination;
import com.travellerapp.domain.Notification;

@RestController
@RequestMapping(path = "/notification")
public class NotificationController 
{
	@Autowired
	private NotificationService notificationService;
    
    @RequestMapping(value = "/deleteAllNotifications", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAllNotifications() 
	{
    	ResponseEntity<String> response= new ResponseEntity<String>(HttpStatus.OK);
    	notificationService.deleteAllNotifications();
		return response;
	}
    
    @GetMapping(path="/getNotificationByEmail/{email}", produces = "application/json")
    public ResponseEntity<Notification> getNotificationByEmail(@PathVariable String email) 
    {
    	Notification notification= notificationService.getNotificationByEmail(email);
    	if(notification !=null) {
    		return ResponseEntity.status(HttpStatus.OK).body(notification);
    	}else {
    		return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    	}
	}
    
    @GetMapping(path="/getNotificationDates/{startDate}/{endDate}", produces = "application/json")
    public ResponseEntity<List<Notification>> getNotificationByEmail(@PathVariable String startDate,@PathVariable String endDate) throws ParseException
    {
    	List<Notification> notification= notificationService.getNotificationByDate(startDate,endDate);
    	if(notification !=null) {
    		return ResponseEntity.status(HttpStatus.OK).body(notification);
    	}else {
    		return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    	}
	}
    

    @RequestMapping(value = "/saveByDest", method = RequestMethod.PUT)
    public ResponseEntity<String> updateNotification(@Valid @RequestBody List<Destination> destinations) 
	{
    	ResponseEntity<String> response= new ResponseEntity<String>(HttpStatus.OK);
    	notificationService.saveNotificationsFromDestination(destinations);
    	return response;
	}
    
    
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<String> insertNotification(@Valid @RequestBody List<Notification> notifications) 
	{
    	ResponseEntity<String> response= new ResponseEntity<String>(HttpStatus.OK);
    	notificationService.saveNotifications(notifications);
    	return response;
	}
    
    
 }
