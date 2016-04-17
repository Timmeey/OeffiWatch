package de.timmeey.oeffiwatch.util.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlStationParserImpl implements HtmlStationParser {
	private static final Logger	LOGGER	= LoggerFactory.getLogger(HtmlStationParserImpl.class);
	private static final String	BASE_URL	= "http://mobil.bvg.de/Fahrinfo/bin/stboard.bin/dox?ld=0.1&";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.timmeey.oeffiWatch.HtmlStationParser#stationLineInfo(java.lang.String)
	 */
	@Override
	public ParseResultImpl stationLineInfo(String stationName) throws IOException {
		LOGGER.debug("Starting to query and parse for {}",stationName);
		Map<String, String> data = new HashMap<>();
		data.put("input", stationName);
		data.put("start", "suchen");
		return parseStationLineInfo(getHTML(BASE_URL, data));
	}

	ParseResultImpl parseStationLineInfo(String html) throws IOException {
		Document doc = Jsoup.parse(html);
		return new ParseResultImpl(getStationName(doc), getErrorMessage(doc),
		      getAmbigiuousStationNames(doc), getLines(doc));

	}

	String getStationName(Document doc) {
		LOGGER.trace("Parsing stationName");
		Element queryStationName = doc.getElementById("ivu_overview_input");
		if (queryStationName != null && queryStationName.hasText()) {
			String result = queryStationName.getElementsByTag("strong").text();
			LOGGER.trace("Found station Name: {}",result);
			return result;
		}
		LOGGER.debug("Could not find stationName");
		return null;

	}

	String getErrorMessage(Document doc) {
		LOGGER.trace("Parsing errorMessage");
		Elements errors = doc.getElementsByClass("error");
		StringBuilder result = new StringBuilder();
		if (!errors.isEmpty()) {
			for (Element element : errors) {
				result.append(element.text());
			}
			LOGGER.trace("Error message(s) was/were: {}",result);
		}
		return result.toString();
	}

	String[][] getLines(Document doc) {
		LOGGER.trace("Parsing lines");

		Elements table = doc.getElementsByClass("ivu_table");
		String[][] list;
		if (table.isEmpty()) {
			// No departures from here
			list = new String[0][];
		} else {
			Element entries = table.first().child(1);
			// +1 for the station Name line
			list = new String[entries.getElementsByTag("tr").size()][];
			for (int i = 0; i < list.length; i++) {
				Element entry = entries.child(i);
				String[] line = { entry.child(0).text(), entry.child(1).text(), entry.child(2).text() };
				list[i] = line;
			}
		}
		LOGGER.trace("Found {} lines",list.length);
		
		return list;
	}

	String[] getAmbigiuousStationNames(Document doc) {
		LOGGER.trace("Parsing AmbigiuousStationNames");
		Elements form = doc.getElementsByTag("form");
		String[] suggestedNames = null;
		if (!form.isEmpty()) {
			Elements selectSpan = form.first().getElementsByClass("select");

			Elements suggestions = selectSpan.first().getElementsByTag("a");
			suggestedNames = new String[suggestions.size()];
			for (int i = 0; i < suggestions.size(); i++) {
				suggestedNames[i] = suggestions.get(i).text();

			}
			LOGGER.trace("Found {} ambigiuous stationNames",suggestedNames.length);
		}
		return suggestedNames;

	}

	private static String getHTML(String url, Map<String, String> data) throws IOException {
		String fetchURL = BASE_URL;
		
		try {
			LOGGER.trace("Fetching website at URL: {}, with data: {}",fetchURL,data);
			return Jsoup.connect(url).data(data).post().html();

		} catch (IOException e) {
			LOGGER.warn("Could not get {}", e, fetchURL);
			throw e;
		}

	}

}
