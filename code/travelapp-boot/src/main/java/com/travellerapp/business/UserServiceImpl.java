package com.travellerapp.business;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travellerapp.domain.User;
import com.travellerapp.repositories.UserRepository;


@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public List<User> listAllUsers() {
		// TODO Auto-generated method stub
		List<User> travellers = new ArrayList<>();
		try {
			 travellers =(List<User>) userRepository.findAll();
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	   return travellers;
	}
	
	@Override
	public User getUserById(String id) {
		// TODO Auto-generated method stub
		User user = new User();
		try {
			 user = userRepository.findUserBy_id(new ObjectId(id));
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	   return user;
	}

	
	@Override
	public User getUserByEmail(String email) {
		// TODO Auto-generated method stub
		User user = new User();
		try {
			 user = userRepository.findUserByEmail(email);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	   return user;
	}

	
	
	
	
	
	
	
	@Override
	public User createUser(User user) {
		// TODO Auto-generated method stub
		try {
			 user = userRepository.save(user);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	   return user;
	}
	
	@Override
	public User updateUser(ObjectId id, User user) {
		// TODO Auto-generated method stub
		try {
			user.setId(id);
			user = userRepository.save(user);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	   return user;
	}

	
	
	@Override
	public void deleteUser(ObjectId id) {
		// TODO Auto-generated method stub
		try {
			userRepository.delete(userRepository.findUserBy_id(id));
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public List<User> createUsers(List<User> users) {
		// TODO Auto-generated method stub
		List<User> result = new ArrayList<User>();
		try {
			result = userRepository.saveAll(users);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	   return result;
	}
	

}
