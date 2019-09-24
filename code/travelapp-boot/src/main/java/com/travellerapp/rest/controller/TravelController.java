package com.travellerapp.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travellerapp.business.TravellerServiceImpl;
import com.travellerapp.domain.Traveller;
import com.travellerapp.rest.model.TravellerDTO;

@RestController
@RequestMapping(path = "/employees")
public class TravelController 
{
	@Autowired
	private TravellerServiceImpl travellerService;
	 
	 
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
    
    
    @GetMapping(path="/getEmployeesFromDb", produces = "application/json")
    public List<Traveller> getEmployeesFromDb() 
    {
    	List<Traveller> list = new ArrayList<Traveller>();
    	try {
    		list=travellerService.listAllTravellers();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
    
    
        return list;
    }

    
    @GetMapping(path="/getConfiguration", produces = "application/json")
    public String getConfiguration() 
    {
    	return welcomeMessage;
    }
    
    
  }
