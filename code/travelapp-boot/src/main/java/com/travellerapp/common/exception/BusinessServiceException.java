package com.travellerapp.common.exception;

public class BusinessServiceException extends BaseException {

	public BusinessServiceException() {
		// TODO Auto-generated constructor stub
	}

	public BusinessServiceException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
 
	public BusinessServiceException( String key, String[] parameters) {
		super(key, parameters);
	}
	
	public BusinessServiceException(BaseException arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public BusinessServiceException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public BusinessServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public BusinessServiceException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

}
