package com.travellerapp.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.travellerapp.domain.Notification;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

	Notification findNotificationBy_id(ObjectId _id);
	
	Notification findNotificationBy_email(String email);

}
