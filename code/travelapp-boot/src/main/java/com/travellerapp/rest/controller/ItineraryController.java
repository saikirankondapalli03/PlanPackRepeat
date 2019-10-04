package com.travellerapp.rest.controller;

/*@RestController
@RequestMapping(path = "/employees")
public class ItineraryController 
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
    
    
  }*/
