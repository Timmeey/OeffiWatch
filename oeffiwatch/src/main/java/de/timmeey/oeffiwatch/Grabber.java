package de.timmeey.oeffiwatch;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import de.timmeey.oeffiwatch.exception.AmbigiuousStationNameException;
import de.timmeey.oeffiwatch.exception.ParseException;
import de.timmeey.oeffiwatch.line.Line;
import de.timmeey.oeffiwatch.line.LineFactory;
import de.timmeey.oeffiwatch.line.LineImpl;
import de.timmeey.oeffiwatch.station.Station;
import de.timmeey.oeffiwatch.station.StationFactory;
import de.timmeey.oeffiwatch.station.impl.StationImpl;
import de.timmeey.oeffiwatch.util.parser.HtmlStationParser;
import de.timmeey.oeffiwatch.util.parser.HtmlStationParserImpl;

public class Grabber {
	private static final Logger			LOGGER				= LoggerFactory.getLogger(Grabber.class);
	private static final Injector			INJECTOR				= configureInjector();
	private final StationFactory			stationFactory;

	private final List<StationImpl>		queriedStations	= new LinkedList<>();

	private ScheduledThreadPoolExecutor	updateExecutor;

	Grabber(StationFactory stationFactory) {
		//DO NOT set the CorePoolSize  to 0, it will trigger a Bug in JVM resulting in busy waiting in
		// the ScheduledThreadPoolExecutor and 100%CPU time
		//https://stackoverflow.com/questions/30972531/netty-running-at-100-cpu
		this.updateExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = Executors.defaultThreadFactory().newThread(r);
				t.setDaemon(true);
				return t;
			}
		});
		this.updateExecutor.setRemoveOnCancelPolicy(true);
		this.stationFactory = stationFactory;
	}

	
	public static Grabber getVBBInstance() {
		return new Grabber(INJECTOR.getInstance(StationFactory.class));
	}

	private static Injector configureInjector() {
		return Guice.createInjector(new AbstractModule() { // NOSONAR

			@Override
			protected void configure() {
				bind(HtmlStationParser.class).to(HtmlStationParserImpl.class);

				install(new FactoryModuleBuilder().implement(Station.class, StationImpl.class)
		            .build(StationFactory.class));

				install(new FactoryModuleBuilder().implement(Line.class, LineImpl.class)
		            .build(LineFactory.class));

			}
		});
	}

	/**
	 * Gets the Station associated with this StationName. This can return an
	 * earlier queried Station, if the StationName is already known to be an
	 * alternativeName for a Station or this exact Station has been queried
	 * before.
	 * 
	 * @param stationName
	 *           The Name of the Station
	 * @return The Station
	 * @throws IOException
	 * @throws AmbigiuousStationNameException
	 *            In case no Station could be found for the given StationName
	 *            This will contain a list of possible Stations that could be
	 *            meant by the given StationName
	 * @throws ParseException
	 */
	public Station getStation(String stationName)
	      throws IOException, AmbigiuousStationNameException, ParseException {

		StationImpl result;
		Optional<StationImpl> alreadyPresentStation = checkStationAlreadyExists(stationName);
		LOGGER.debug("Looking up {}",stationName);

		if (alreadyPresentStation.isPresent()) {
			// This StationName was already queried earlier
			LOGGER.debug("This exact StationName {} was already queried earlier",
			      alreadyPresentStation.get().name());
			result = alreadyPresentStation.get();
		} else {
			LOGGER.debug("This exact StationName {} has not been queried earlier", stationName);
			Optional<StationImpl> alternativeStation = checkUnambigiuousStation(stationName);
			if (alternativeStation.isPresent()) {
				// The stationName is already known to be an alternativeName for an
				// already queried Station
				LOGGER.debug("The stationName {} is already known to be an alternativeName for Station: {}",stationName,
				      alternativeStation.get().name());
				result = alternativeStation.get();
			} else {
				// This station name is not yet known
				LOGGER.debug("The Station for {} is not yet known", stationName);
				LOGGER.info("Querying the website for Station {}",stationName);
				result = this.stationFactory.create(stationName, updateExecutor);
				Optional<StationImpl> duplicateStation = checkStationIsDuplicate(result);
				if (duplicateStation.isPresent()) {
					// The queried Station is a duplicate of an already known
					// Station. Merging Stations
					LOGGER.debug("The queried Station {}(realName: %s) is a duplicate of an already known Station {}. Merging Stations",
					      stationName, result.name(), duplicateStation.get().name());
					duplicateStation.get().merge(result, stationName);
					result = duplicateStation.get();
				} else {
					LOGGER.debug("Station {}(realName:{}) was not known, and no duplicate",stationName,result.name());
					LOGGER.info("Adding the new Station {} to the List of known Stations",result.name());
					queriedStations.add(result);
					LOGGER.trace("Known stationList contains now {} distinct stations",queriedStations.size());

				}
			}
		}
		return result;

	}

	/**
	 * Returns the number of real Stations that have already been queried
	 * 
	 * @return The count of known station Objects
	 */
	public int countKnownStations() {
		return queriedStations.size();

	}

	/**
	 * Checks whether the queried Station name is already known to be a
	 * alternative Name for an already queried Station
	 * 
	 * @param stationName
	 *           The station name to look for
	 * @return The station with the queried name as an alternative Name
	 */
	Optional<StationImpl> checkUnambigiuousStation(String stationName) {
		return queriedStations.stream()
		      .filter(s -> s.getAlternativeStationNames().contains(stationName)).findAny();
	}

	/**
	 * Checks whether there already is a station with this exact name in the
	 * queried Station list
	 * 
	 * @param stationName
	 *           The station name to look for
	 * @return The station with this exact name
	 */
	Optional<StationImpl> checkStationAlreadyExists(String stationName) {
		return queriedStations.stream().filter(s -> s.name().equals(stationName)).findAny();
	}

	Optional<StationImpl> checkStationIsDuplicate(StationImpl station) {
		return queriedStations.stream().filter(s -> s.name().equals(station.name())).findAny();

	}

	
	
	public static JsonSerializer<LocalDateTime> getDateTimeSerializer() {
		return new JsonSerializer<LocalDateTime>() { // NOSONAR
			@Override
			public JsonElement serialize(LocalDateTime src, Type typeOfSrc,
		         JsonSerializationContext context) {
				return src == null ? null : new JsonPrimitive(src.toString());
			}
		};
	}

}
