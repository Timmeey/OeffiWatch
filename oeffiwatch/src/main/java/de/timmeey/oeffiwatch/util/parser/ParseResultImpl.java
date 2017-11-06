package de.timmeey.oeffiwatch.util.parser;

public class ParseResultImpl implements ParseResult {


	private final String				stationName;
	private String				errorMessage;
	private final String[]			alternativeNames;
	private final String[][]		lines;
	private final OeffiParseError	error;

	ParseResultImpl(final String stationName, String errorMessage,
	      final String[] alternativeNames, final String[][] lines) {
		this.stationName = stationName;
		this.errorMessage = errorMessage==null ? "":errorMessage;
		this.alternativeNames = alternativeNames;
		this.lines = lines;
		if (stationName == null || stationName.isEmpty()) {
			if (alternativeNames == null || alternativeNames.length == 0) {
				this.error = OeffiParseError.PARSE_ERROR;
				this.errorMessage = this.errorMessage + "Station name was null/empty, but alternativeNames were also null/empty";
			} else {
				this.error = OeffiParseError.AMBIGIUOUS_STATION_NAME;
			}
		} else if (lines == null) {
			// We got a station name, not ambiguous, but we still got a NULL
			// line list.
			// THis is an error. If there were no departures, the lines array
			// would just be empty
			this.error = OeffiParseError.PARSE_ERROR;
			this.errorMessage = this.errorMessage + String.format("Station Name was %s not empty/null, but Lines was NULL",stationName);

		} else {

			this.error = null;
		}
	}

	/* (non-Javadoc)
	 * @see de.timmeey.oeffiwatch.util.parser.IParseResult#getStationName()
	 */
	@Override
	public String getStationName() {
		if (error != OeffiParseError.PARSE_ERROR) {
			return stationName;
		} else {
			throw new IllegalStateException(
			      String.format("Can't get Station name while ParseError is present. %s", error));
		}
	}

	/* (non-Javadoc)
	 * @see de.timmeey.oeffiwatch.util.parser.IParseResult#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		if (error != null) {
			return errorMessage;
		} else {
			throw new IllegalStateException(String.format("No error was given. %s", error));
		}
	}

	/* (non-Javadoc)
	 * @see de.timmeey.oeffiwatch.util.parser.IParseResult#getAlternativeNames()
	 */
	@Override
	public String[] getAlternativeNames() {
		if (error.equals(OeffiParseError.AMBIGIUOUS_STATION_NAME)) {
			return alternativeNames;
		} else {
			throw new IllegalStateException(
			      String.format("No ambigous Station name present. %s", error));
		}
	}

	/* (non-Javadoc)
	 * @see de.timmeey.oeffiwatch.util.parser.IParseResult#getLines()
	 */
	@Override
	public String[][] getLines() {
		if (error == null) {
			return lines;
		} else {
			throw new IllegalStateException(
			      String.format("No Lines were parsed due to parsing errors. %s", error));
		}
	}

	/* (non-Javadoc)
	 * @see de.timmeey.oeffiwatch.util.parser.IParseResult#getError()
	 */
	@Override
	public OeffiParseError getError() {
		return error;
	}

}