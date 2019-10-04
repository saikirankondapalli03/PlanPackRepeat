package com.travellerapp.common.exception;

import java.util.Arrays;

import com.travellerapp.cdd.Message;

public class BaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String errorCode;
	protected String[] parameters;
	protected Message userMessage;

	// Define some constructors

	public BaseException(String errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
		this.userMessage = new Message(errorCode, null);
	}

	public BaseException(String errorCode, String[] parameters) {
		// Setting exception message by calling super constructor
		super(String.format("%s - %s", errorCode, Arrays.toString(parameters)));

		this.errorCode = errorCode;
		if (parameters != null) {
			this.parameters = Arrays.copyOf(parameters, parameters.length);
		}
		this.userMessage = new Message(errorCode, null);
	}

	public BaseException(String errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
		this.userMessage = new Message(errorCode, null);
	}

	public BaseException(String errorCode, String[] parameters, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
		if (parameters != null) {
			this.parameters = Arrays.copyOf(parameters, parameters.length);
		}
		this.userMessage = new Message(errorCode, null);
	}

	public BaseException(Message message) {
		// Setting exception message by calling super constructor
		super(message != null ? message.toString() : "");
		this.userMessage = message;
	}

	public BaseException() {
		// TODO Auto-generated constructor stub
	}

	public BaseException(BaseException be) {
		super(be.getMessage());
		this.setUserMessage(be.getUserMessage());
		this.errorCode = be.getErrorCode();
		this.parameters = be.getParameters();
	}

	public BaseException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public BaseException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String[] getParameters() {
		String[] parametersClone = null;
		if (this.parameters != null) {
			parametersClone = Arrays.copyOf(this.parameters, this.parameters.length);
		}
		return parametersClone;
	}

	public Message getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(Message userMessage) {
		this.userMessage = userMessage;
	}

}
