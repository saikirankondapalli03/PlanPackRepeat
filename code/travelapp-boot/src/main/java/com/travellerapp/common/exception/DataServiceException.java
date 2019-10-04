/**
 * 
 */
package com.travellerapp.common.exception;

public class DataServiceException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 54232653053667L;

	public DataServiceException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DataServiceException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public DataServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public DataServiceException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public DataServiceException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public DataServiceException(String key, String[] parameters) {
		super(key, parameters);
	}

	public DataServiceException(BaseException arg0) {
		super(arg0);
	}

}
