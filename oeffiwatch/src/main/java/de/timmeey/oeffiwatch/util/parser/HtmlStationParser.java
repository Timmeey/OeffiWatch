package de.timmeey.oeffiwatch.util.parser;

import java.io.IOException;

import de.timmeey.oeffiwatch.exception.AmbigiuousStationNameException;

public interface HtmlStationParser {

	/**
	 * Parses the Departure times for the given station. The official station
	 * name will be in [0][0] From [>0][] will be the entries for the departure
	 * times per line
	 * 
	 * @param stationName
	 *           The station Name to fetch the departureTImes for
	 * @return The Array containing the staiton name and the departure times
	 * @throws IOException
	 *            If something oes wrong while connecting to BVG-Server
	 * @throws AmbigiuousStationNameException
	 */
	ParseResult stationLineInfo(String stationName) throws IOException;

	
}