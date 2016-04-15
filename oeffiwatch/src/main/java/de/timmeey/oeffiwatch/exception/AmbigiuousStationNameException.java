package de.timmeey.oeffiwatch.exception;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class AmbigiuousStationNameException extends Exception {
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
	@Expose
	private final String[]	alternativeNames;
	@Expose
	private final String		errorMsg;

	public AmbigiuousStationNameException(String errorMessage,
	      String... alternativeStationNames) {
		this.alternativeNames = alternativeStationNames;
		this.errorMsg = errorMessage;
	}

	/**
	 * @return the alternativeNames
	 */
	public String[] getAlternativeNames() {
		return alternativeNames;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
	
	public String toJson(){
		return gson.toJson(this);
	}

}
