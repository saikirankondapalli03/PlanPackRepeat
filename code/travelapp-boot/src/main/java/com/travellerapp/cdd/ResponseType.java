package com.travellerapp.cdd;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * Created by b004233 on 8/5/2016.
 */
@Data
public class ResponseType<T> implements Serializable {

    private String returnMessage;
    private int returnCode;
    private long timeTook;
    private T data;
    private String server;

    public ResponseType() {
    	server = System.getProperty("jboss.server.name"); 
    }

    public ResponseType(ReturnCodes returnCode) {
        setReturnCodeViaCode(returnCode);
    }

    public ResponseType(ReturnCodes returnCode, T data) {
        setReturnCodeViaCode(returnCode);
    }

    @JsonIgnore
    public void setReturnCodeViaCode(ReturnCodes returnCode) {
        this.returnCode = returnCode.getValue();
        if (returnMessage == null || returnMessage.equals(""))
            returnMessage = returnCode.getDescription();
    }

    public void setReturnCode(int returnCode){
        this.returnCode = returnCode;
    }

    public ResponseType<T> validationFailed(List<String> badFields) {
        setReturnCodeViaCode(ReturnCodes.BadRequest);
        return this;
    }

    public ResponseType<T> success() {
        setReturnCodeViaCode(ReturnCodes.Success);
        return this;
    }

    public ResponseType<T> success(String message) {
        //setReturnMessage(message);
        setReturnCodeViaCode(ReturnCodes.Success);
        return this;
    }

}

