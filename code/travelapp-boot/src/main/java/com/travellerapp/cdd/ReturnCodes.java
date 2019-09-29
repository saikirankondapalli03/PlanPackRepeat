package com.travellerapp.cdd;

import java.io.Serializable;

public enum ReturnCodes implements Serializable
{
    Success(200, "Success"),
    BadRequest(400, "Bad Request"),
    UnauthorizedAccess(401, "Unauthorized Access"),
    NotFound(404, "Resource Not Found"),
    TimedOut(408, "Timed Out"),
    InternalServerError(500, "Internal Exception"),
    ExternalServerError(502, "External Dependency Exception"),

	DATANOTFOUND(500,"Data Not Found");
    private int id = 0;
    private String description = "";

    ReturnCodes(){}

    ReturnCodes(int id){
        this.id = id;
    }

    ReturnCodes(int id, String description)
    {
        this.id = id;
        this.description = description;
    }

    public int getValue()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }
}
