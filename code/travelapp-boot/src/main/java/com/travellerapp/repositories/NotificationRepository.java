package com.travellerapp.repositories;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.travellerapp.domain.Notification;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

	Notification findNotificationBy_id(ObjectId _id);
	
	Notification findNotificationByemailId(String email);
	
	@Query("{'plannedTime':{ $gte: ?0, $lte: ?1}}}")                 
	public List<Notification> getNotificationByDate(Date from,Date To); 

}
