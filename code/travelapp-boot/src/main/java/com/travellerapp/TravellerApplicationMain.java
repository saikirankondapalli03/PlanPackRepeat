package com.travellerapp;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan; 

@SpringBootApplication 
@EnableAutoConfiguration
@ComponentScan(basePackages  = {"com.travellerapp"})
public class TravellerApplicationMain extends SpringBootServletInitializer{


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TravellerApplicationMain.class);
    }
   
    public static void main(String[] args) {
        new TravellerApplicationMain().configure(new SpringApplicationBuilder(TravellerApplicationMain.class)).run(args);
    }
    
    
}

