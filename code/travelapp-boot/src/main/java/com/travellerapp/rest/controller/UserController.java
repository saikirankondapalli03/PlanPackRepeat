package com.travellerapp.rest.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.travellerapp.business.UserServiceImpl;
import com.travellerapp.domain.User;
import com.travellerapp.rest.model.TravellerDTO;

@RestController
@RequestMapping(path = "/users")
public class UserController 
{
	@Autowired
	private UserServiceImpl userService;
	 
	@Value("${welcome.message}")
	private String welcomeMessage;
	
    @GetMapping(path="/", produces = "application/json")
    public List<TravellerDTO> getEmployees() 
    {
    	List<TravellerDTO> list = new ArrayList<TravellerDTO>();
    	list.add(new TravellerDTO(1,"Jenelee","B","Jenelee@gmail.com"));
    	list.add(new TravellerDTO(1,"Saransh","B","Saransh@gmail.com"));
    	list.add(new TravellerDTO(1,"Maram","B","Maram@gmail.com"));
    	list.add(new TravellerDTO(1,"Sai","B","Sai@gmail.com"));
        return list;
    }
    
    
    @GetMapping(path="/getAllUsers", produces = "application/json")
    public List<User> getAllUsers() 
    {
    	List<User> list = new ArrayList<User>();
    	try {
    		list=userService.listAllUsers();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
        return list;
    }

    
    @GetMapping(path="/getUserById/{id}", produces = "application/json")
    public User getUserById(@PathVariable String id) 
    {
    	User user = new User();
    	try {
    		user=userService.getUserById(id);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
        return user;
    }
    
    
    @GetMapping(path="/getUserByEmail/{email}", produces = "application/json")
    public ResponseEntity<User>  getUserByEmail(@PathVariable String email) 
    {
    	User user= userService.getUserByEmail(email);
    	if(user !=null) {
    		return ResponseEntity.status(HttpStatus.OK).body(user);
    		//return ResponseEntity.ok().body(user);
    	}else {
    		return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    	}
    	
    }
    
    @RequestMapping(value = "/createuser", method = RequestMethod.POST)
    public ResponseEntity<User> createUserWithDetails(@Valid @RequestBody User user) 
	{
    	
    	return ResponseEntity.ok().body(userService.createUser(user));
		
	}
    
    
    
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public User createUser(@Valid @RequestBody User user) 
	{
		try {
			user = userService.createUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
    
    
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public User updateUser(@PathVariable("id") ObjectId id,@Valid @RequestBody User user) 
	{
		try {
			user = userService.updateUser(id,user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
    
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void deleteUser(@PathVariable ObjectId id) {
		try {
			userService.deleteUser(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public List<User> createUsers(@Valid @RequestBody List<User> users) 
	{
		try {
			users = userService.createUsers(users);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}
    
    
    @GetMapping(path="/getConfiguration", produces = "application/json")
    public String getConfiguration() 
    {
    	return welcomeMessage;
    }
    
    
    
    
  }
