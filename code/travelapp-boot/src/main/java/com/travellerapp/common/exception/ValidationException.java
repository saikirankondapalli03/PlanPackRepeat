package com.travellerapp.common.exception;

import com.travellerapp.cdd.Message;;

public class ValidationException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 232660399449208077L;

	public ValidationException() {
	}

	public ValidationException(String arg0) {
		super(arg0);
	}

	public ValidationException(Throwable arg0) {
		super(arg0);
	}

	public ValidationException(String key, String[] parameters) {
		super(key, parameters);
	}

	public ValidationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ValidationException(String errorCode, String[] parameters, Throwable cause) {
		super(errorCode, parameters, cause);
	}

	public ValidationException(Message message) {
		super(message);
	}

	public ValidationException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

}
