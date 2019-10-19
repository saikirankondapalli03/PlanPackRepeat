package com.travellerapp.business;

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
		return (List<User>) userRepository.findAll();
	}
	
	@Override
	public User getUserById(String id) {
	   return userRepository.findUserBy_id(new ObjectId(id));
	}

	
	@Override
	public User getUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}

	@Override
	public User createUser(User user) {
		return userRepository.save(user);
	}
	
	
	@Override
	public User updateUser(ObjectId id, User user) {
		user.setId(id);
		return userRepository.save(user);
	}
	
	@Override
	public void deleteUser(ObjectId id) {
		userRepository.delete(userRepository.findUserBy_id(id));
	}
	
	
	@Override
	public List<User> createUsers(List<User> users) {
		return userRepository.saveAll(users);
	}
	

}
