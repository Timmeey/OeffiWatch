package de.timmeey.oeffiwatch.exception;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ParseException extends Exception {
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


	public ParseException() {
		
		super();
		// TODO Auto-generated constructor stub
	}

	public ParseException(String message, Throwable cause, boolean enableSuppression,
	      boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ParseException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ParseException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ParseException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
