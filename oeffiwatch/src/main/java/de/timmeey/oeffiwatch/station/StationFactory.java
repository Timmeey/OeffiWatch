package de.timmeey.oeffiwatch.station;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.assistedinject.Assisted;

import de.timmeey.oeffiwatch.Grabber;
import de.timmeey.oeffiwatch.exception.AmbigiuousStationNameException;
import de.timmeey.oeffiwatch.exception.ParseException;
import de.timmeey.oeffiwatch.station.impl.StationImpl;

@FunctionalInterface
public interface StationFactory {
	public StationImpl create(@Assisted("stationName") String stationName, @Assisted("updateExecutor") ScheduledExecutorService updateExecutor)
	      throws IOException, AmbigiuousStationNameException, ParseException; // NOSONAR

}
