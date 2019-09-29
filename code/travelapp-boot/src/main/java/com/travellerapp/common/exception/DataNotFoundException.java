package com.travellerapp.common.exception;

import com.travellerapp.cdd.Message;

public class DataNotFoundException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 552337992958619765L;

	/**
	 * 
	 */

	public DataNotFoundException() {
	}

	public DataNotFoundException(String arg0) {
		super(arg0);
	}
	
	public DataNotFoundException(Throwable arg0) {
		super(arg0);
	}

	public DataNotFoundException( String key, String[] parameters) {
		super(key, parameters);
	}
	

	public DataNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

    public DataNotFoundException(String errorCode, String[] parameters, Throwable cause) {
   	    super(errorCode,parameters,cause);
    }

	public DataNotFoundException(Message message) {
		super(message);
	}

	public DataNotFoundException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public DataNotFoundException(BaseException arg0) {
		super(arg0);
	}

}
