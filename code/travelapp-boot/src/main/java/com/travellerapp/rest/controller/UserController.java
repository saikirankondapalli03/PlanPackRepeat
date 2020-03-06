package com.travellerapp.rest.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
       List<User> users=userService.listAllUsers();
       return users;
    }

    
    @GetMapping(path="/getUserById/{id}", produces = "application/json")
    public User getUserById(@PathVariable String id) 
    {
    	return userService.getUserById(id);
	}
    
    
    @GetMapping(path="/getUserByEmail/{email}", produces = "application/json")
    public ResponseEntity<User>  getUserByEmail(@PathVariable String email) 
    {
    	User user= userService.getUserByEmail(email);
    	if(user !=null) {
    		return ResponseEntity.status(HttpStatus.OK).body(user);
    	}else {
    		return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    	}
    	
    }
    
    @RequestMapping(value = "/createuser", method = RequestMethod.POST)
    public ResponseEntity<User> createUserWithDetails(@Valid @RequestBody User user) 
	{
    	User savedUser=userService.createUser(user);
    	return ResponseEntity.ok().body(savedUser);
	}
    
    
    
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public User createUser(@Valid @RequestBody User user) 
	{
		return userService.createUser(user);
	}
    
    
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public User updateUser(@PathVariable("id") ObjectId id,@Valid @RequestBody User user) 
	{
    	return userService.updateUser(id,user);
	}
    
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteUser(@PathVariable ObjectId id) {
		ResponseEntity<String> response= new ResponseEntity<String>(HttpStatus.OK);
		userService.deleteUser(id);
		return response;
	}
    
    
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public List<User> createUsers(@Valid @RequestBody List<User> users) 
	{
		return userService.createUsers(users);
	}
    
    
    @RequestMapping(value = "/deleteAllUsers", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAllUsers() 
	{
    	ResponseEntity<String> response= new ResponseEntity<String>(HttpStatus.OK);
    	userService.listAllUsers().stream().forEach(x->{
				userService.deleteUser(new ObjectId(x.getId()));
			});;
		return response;
	}
    
    @GetMapping(path="/getConfiguration", produces = "application/json")
    public String getConfiguration() 
    {
    	return welcomeMessage;
    }
  }
