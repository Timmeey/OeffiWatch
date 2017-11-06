package de.timmeey.oeffiwatch.util.parser;

public interface ParseResult {
	public enum OeffiParseError {
		AMBIGIUOUS_STATION_NAME, PARSE_ERROR;
	}

	/**
	 * @return the stationName
	 */
	public String getStationName();

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage();

	/**
	 * @return the alternativeNames
	 */
	public String[] getAlternativeNames();

	/**
	 * @return the lines
	 */
	public String[][] getLines();

	/**
	 * @return the error
	 */
	public OeffiParseError getError();

}