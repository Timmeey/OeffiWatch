package de.timmeey.oeffiwatch.station.impl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.timmeey.oeffiwatch.Grabber;
import de.timmeey.oeffiwatch.exception.AmbigiuousStationNameException;
import de.timmeey.oeffiwatch.exception.ParseException;
import de.timmeey.oeffiwatch.line.Line;
import de.timmeey.oeffiwatch.line.LineFactory;
import de.timmeey.oeffiwatch.line.LineImpl;
import de.timmeey.oeffiwatch.line.Line.Vehicle;
import de.timmeey.oeffiwatch.station.Station;
import de.timmeey.oeffiwatch.util.parser.HtmlStationParser;
import de.timmeey.oeffiwatch.util.parser.HtmlStationParser.ParseResult;
import de.timmeey.oeffiwatch.util.parser.HtmlStationParser.ParseResult.OeffiParseError;

/**
 * Internal class that represents a Station
 * @author timmeey
 *
 */
public class StationImpl extends Observable implements Station {
	private static final GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
	      .registerTypeAdapter(LineImpl.class, LineImpl.getSerializer()).registerTypeAdapter(LocalDateTime.class, Grabber.getDateTimeSerializer());
	private static final Gson gsonPretty = builder.setPrettyPrinting().create();
	private static final Gson gsonCompact = builder.create();
	
	private ScheduledFuture<?> scheduledFuture;
	
	
	private static final Logger		LOGGER							= LoggerFactory
	      .getLogger(StationImpl.class);
	//Do not set yourself. use the private Setter, to ensure correct timstamps and stuff
	@Expose
	private List<Line>					lines;

	// The exact Name of the Station, returned by the API (Can differ from the
	// searched Station name)
	@Expose
	private final String					name;

	private final HtmlStationParser	parser;
	private final LineFactory			lineFactory;

	/**
	 * Station names are often not exact but still unambigous. Therefor if a User
	 * looks for "Ernst-Reuter Platz" he will get "U-Ernst-Reuter-Platz". If he
	 * afterwards searches for "Ernst-Reuter-Platz" he would again get
	 * "U-Ernst-Reuter-Platz" (see the difference in the last '-'?) For that
	 * reason we also store a list of all already KNOWN names for a station in
	 * alternativeNamesForStation list so next time the User searches for an
	 * already known alias, we don't have to fetch it from again.
	 **/
	@Expose
	private Set<String>			alternativeNamesForStation	= new HashSet<>();

	@Expose
	private LocalDateTime							lastUpdated;
	private final ScheduledExecutorService updateExecutor;

	/**
	 * Internal constructor used by the injector/factory to create Stations
	 * @param stationName The queried StationName
	 * @param lineFactory The LineFactory
	 * @param parser The WebsiteParser
	 * @param updateExecutor The Updateexecutor (needed for autoUpdate feature)
	 * @throws IOException If an IOError occures during fetching the website
	 * @throws AmbigiuousStationNameException If the queriedStationName could not be resolved to a single station
	 * @throws ParseException If something goes wrong while parsing (bad data/server errors, change in html format)
	 */
	@Inject
	public StationImpl(@Assisted("stationName") String stationName, LineFactory lineFactory,
	      HtmlStationParser parser, @Assisted("updateExecutor") ScheduledExecutorService updateExecutor)
	            throws IOException, AmbigiuousStationNameException, ParseException {
		LOGGER.debug("Creating station for {}",stationName);
		this.updateExecutor = updateExecutor;
		this.parser = Preconditions.checkNotNull(parser);
		ParseResult initalFetchedData = Preconditions
		      .checkNotNull(this.parser.stationLineInfo(stationName));
		this.lineFactory = Preconditions.checkNotNull(lineFactory);
		if (initalFetchedData.getError() == null) {
			LOGGER.debug("Parser does not contain errors, initializing station fields.");
			this.name = Preconditions.checkNotNull(initalFetchedData.getStationName());
			this.lines = Preconditions.checkNotNull(parseLines(initalFetchedData.getLines()));
			this.lastUpdated = LocalDateTime.now();
			this.alternativeNamesForStation.add(stationName);
			LOGGER.debug("Station:{} created, has {} lines and {} alternativeName",this.name(),this.lines().size(),this.alternativeNamesForStation.size());
		} else if (initalFetchedData.getError().equals(OeffiParseError.AMBIGIUOUS_STATION_NAME)) {
			LOGGER.debug("Could not initialize Station for {}, because it was ambigiuous",stationName);
			throw new AmbigiuousStationNameException(initalFetchedData.getErrorMessage(),
			      initalFetchedData.getAlternativeNames());
		} else {
			LOGGER.warn("Something went wrong while creating Station for {}",stationName);
			throw new ParseException(initalFetchedData.getErrorMessage());
		}

	}
	


	private List<Line> parseLines(String[][] lines) {
		LOGGER.trace("Parsing lines for station: {}",this.name());
		ImmutableList.Builder<Line> builder = ImmutableList.builder();
		// Starting at i=1 because first entry is the station name
		for (int i = 0; i < lines.length; i++) {
			Line line = lineFactory.create(lines[i][0], lines[i][1], lines[i][2]);
			builder.add(line);

		}
		return builder.build();

	}


	

	@Override
	public List<Line> lines(Vehicle... vehicles) {
		List<Line> result;
		if (vehicles != null && vehicles.length > 0) {
			List<Vehicle> tmpVehicles = Arrays.asList(vehicles);
			result = this.lines.stream().filter(l -> tmpVehicles.contains(l.vehicleType()))
			      .collect(Collectors.toList());
		}else{
			result = this.lines;
		}
		return Collections.unmodifiableList(result);

	}

	@Override
	public String name() {
		return this.name;
	}
	
	
	@Override
	public StationImpl update() throws IOException {
		LOGGER.debug("Updating station: {}",this.name());
		// just synchronize this to make sure there is no update happening while
		// merging and the other way round
		synchronized (this) {
				List<Line> tmp = parseLines(parser.stationLineInfo(this.name).getLines());
				boolean changed = !this.lines.equals(tmp);
				if(changed){
					LOGGER.trace("Station: {} was updated, and got new Line data. Informing observers",this.name());
					this.lines = tmp;
					this.setChanged();
					this.notifyObservers();
				}else{
					LOGGER.trace("Update for {} did not yield new data. Setting lastUpdated, but NOT informaing observers",this.name());
				}
				this.lastUpdated = LocalDateTime.now();

			
			return this;
		}

	}
	
	
	@Override
	public LocalDateTime lastUpdated(){
		return this.lastUpdated;
	}

	public long timeSinceLastRefresh() {
		return lastUpdated.until(LocalDateTime.now(), ChronoUnit.MILLIS);
	}

	/**
	 * Internally used method to update an already existing station with
	 * accidentally queried Data from the same Station but a different object
	 * 
	 * Station names are unique. But BVG website tries to figure out the correct
	 * station if someone queries it with a partial or unambigiuous (but not
	 * "correct") station name. The real name will then be returned by the
	 * Website. This name is then used to find out whether there already is a
	 * station object for that station. If so, this method will be called, to
	 * merge the freshly queried departure times of the duplicate station object,
	 * into the already existing object, so the queried data does not go to
	 * waste.
	 * 
	 * @param stationToMerge
	 *           the station which equals this station and was accidently
	 *           queried. Meant for the initial query on stations to retrieve
	 *           their REAL name
	 * @param alternativeStationName 
	 * 
	 * @return This station with updated list
	 **/
	public StationImpl merge(StationImpl stationToMerge, String alternativeStationName) {
		LOGGER.debug("Merging station {}, with alternativeName {} into this station ({})",stationToMerge.name(),alternativeStationName,this.name());
		if (!this.equals(stationToMerge)) {
			throw new IllegalArgumentException(String.format(
			      "Station %s does not equals this station (%s)", stationToMerge.name, this.name));
		}
		// just synchronize this to make sure there is no update happening while
		// merging and the other way round
		synchronized (this) {
			// Adds the user searched Name from the stationToMerge to the List of
			// alternatives for this,so next time someone looks for this station,
			// THIS station will be found instead of a new query
			ImmutableSet.Builder<String> builder = ImmutableSet.builder();
			builder.addAll(alternativeNamesForStation);
			builder.add(alternativeStationName);
			this.alternativeNamesForStation = builder.build();
			// Check whether the toMerge station has more recent data
			if (this.lastUpdated.isBefore(stationToMerge.lastUpdated) ) {
				LOGGER.debug("The merging station has more recent data, replacing this stations lines with the new ones");
				this.lines = stationToMerge.lines;
				this.setChanged();
				this.notifyObservers();
				this.lastUpdated = stationToMerge.lastUpdated;
			}
			LOGGER.debug("Stations merged. AlternativeNames for {}, are now: {}",this.name(),this.alternativeNamesForStation);
			return this;
		}
	}
	
	/**
	 * Internal Method used to associate a station with it's alternative names (which also lead to the same station)
	 * @return the list of already discovered alternative names for this station
	 */
	public Set<String> getAlternativeStationNames(){
		return this.alternativeNamesForStation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof StationImpl)) {
			return false;
		}
		StationImpl other = (StationImpl) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}


	@Override
	public String toJson(boolean prettyPrint) {
		if(prettyPrint){
			return gsonPretty.toJson(this);
		}else{
			return gsonCompact.toJson(this);
		}
	}



	@Override
	public void run() {
		LOGGER.trace("Run method was triggered for station: {}", this.name());
		if(super.countObservers()>0){
			try {
				this.update();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			LOGGER.trace("No observers present ({}), not running update",this.name());
		}
		
	}



	@Override
	public synchronized Station enableAutoUpdate(int period, TimeUnit unit) {
		LOGGER.debug("Enabling autoUpdate for station {}",this.name());
		if(this.scheduledFuture==null){
			this.scheduledFuture = updateExecutor.scheduleWithFixedDelay(this, period, period, unit);
		}else{
			LOGGER.debug("autoUpdating was already enabled for {}, doing nothing",this.name());
		}
		return this;
	}



	@Override
	public synchronized Station disableAutoUpdate() {
		LOGGER.debug("Disbaling autoUpdate for Station: {}",this.name());
		if(this.scheduledFuture!=null){
			this.scheduledFuture.cancel(false);
			this.scheduledFuture = null;
		}else{
			LOGGER.debug("autoUpdate was not enabled for station: {}. Doing nothing",this.name());
		}
		return this;
	}
	
	
	
	@Override
	public boolean isAutoUpdating(){
		return this.scheduledFuture!=null && !this.scheduledFuture.isCancelled();
	}
	
	
	

}
