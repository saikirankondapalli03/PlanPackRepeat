package com.travellerapp.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travellerapp.rest.model.Traveller;

@RestController
@RequestMapping(path = "/employees")
public class TravelController 
{
   
    @GetMapping(path="/", produces = "application/json")
    public List<Traveller> getEmployees() 
    {
    	List<Traveller> list = new ArrayList<Traveller>();
    	list.add(new Traveller(1,"Jenelee","B","Jenelee@gmail.com"));
    	list.add(new Traveller(1,"Saransh","B","Saransh@gmail.com"));
    	list.add(new Traveller(1,"Maram","B","Maram@gmail.com"));
    	list.add(new Traveller(1,"Sai","B","Sai@gmail.com"));
    
        return list;
    }
  }
