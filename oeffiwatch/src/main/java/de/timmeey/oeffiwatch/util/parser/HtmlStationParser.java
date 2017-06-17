package de.timmeey.oeffiwatch.util.parser;

import java.io.IOException;

public interface HtmlStationParser {

	/**
	 * Parses the Departure times for the given station. The official station
	 * name will be in [0][0] From [{@literal >}0][] will be the entries for the departure
	 * times per line
	 *
	 * @param stationName
	 *           The station Name to fetch the departureTImes for
	 * @return The Array containing the station name and the departure times
	 * @throws IOException
	 *            If something goes wrong while connecting to BVG-Server
	 */
	ParseResult stationLineInfo(String stationName) throws IOException;


}
