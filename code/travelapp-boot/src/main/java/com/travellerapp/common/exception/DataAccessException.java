package com.travellerapp.common.exception;

public class DataAccessException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4051500723448485746L;

	public DataAccessException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DataAccessException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public DataAccessException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public DataAccessException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public DataAccessException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public DataAccessException(String key, String[] parameters) {
		super(key, parameters);
	}

	public DataAccessException(BaseException arg0) {
		super(arg0);
	}

}
