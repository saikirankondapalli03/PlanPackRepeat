package com.travellerapp.cdd;

import java.io.Serializable;

public enum ItineraryStatus implements Serializable
{
	IN_TRAVEL("IT", "IN_TRAVEL"),
    COMPLETED("C","COMPLETED"),
    ABOUT_TO_TRAVEL("ATT","ABOUT_TO_TRAVEL");
    private String key;
    private String description = "";

    ItineraryStatus(){}

    ItineraryStatus(String id){
        this.key = id;
    }

    ItineraryStatus(String key, String description)
    {
        this.key = key;
        this.description = description;
    }

    public String getValue()
    {
        return key;
    }

    public String getDescription()
    {
        return description;
    }
}
