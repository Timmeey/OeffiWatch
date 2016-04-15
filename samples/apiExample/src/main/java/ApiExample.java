

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import de.timmeey.oeffiwatch.Grabber;
import de.timmeey.oeffiwatch.exception.AmbigiuousStationNameException;
import de.timmeey.oeffiwatch.exception.ParseException;
import de.timmeey.oeffiwatch.line.Line;
import de.timmeey.oeffiwatch.station.Station;

public class ApiExample {


	// Name of the Station we want to watch
	private final static String	stationName			= "S Tiergarten (Berlin)";

	// Refresh the Data every 30 Seconds
	private final static int		refreshPeriod		= 30;
	private final static TimeUnit	refreshPeriodUnit	= TimeUnit.SECONDS;

	public static void main(String[] args) throws InterruptedException {
	   System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");

		// Getting the appropriate Grabber for Berlin (VBB)
		Grabber vbbGrabber = Grabber.getVBBInstance();

		try {
			// Getting the Station data from the website
			Station station = vbbGrabber.getStation(stationName);

			System.out.println(String.format("There are %s departures scheduled, at %s",
			      station.lines().size(), station.name()));
			System.out.println(String.format(
			      "The next departures will be:"));
			for(int i = 0; i<station.lines().size() && i<4; i++){
				Line nextDeparture = station.lines().get(i);
				System.out.println(String.format("%s which is a %s going to %s in %s minutes",
			      nextDeparture.lineName(), nextDeparture.vehicleType().getName(),
			      nextDeparture.lineEnd(), nextDeparture.estimatedDepartureTimefromNow()));
			}
			
			//Enabling autoUpdate every 30 seconds. If something changes during update
			// the observers will get notified.
			//AutoUpdates only happen, if there are observers registered.
			station.enableAutoUpdate(refreshPeriod, refreshPeriodUnit);
			
			//check if station is really autoUpdating now
			System.out.println(String.format("Station is autoupdateing: %s", station.isAutoUpdating()));
			
			System.out.println(String.format("Last update happened at: %s", station.lastUpdated()));
			//Need to register observers, so updates are triggered
			station.addObserver(new Observer() {
				
				@Override
				public void update(Observable o, Object arg) {
					System.out.println(String.format("Now there are %s departures available", station.lines().size()));
					System.out.println(station.lastUpdated());
					//disable autoupdate after the first change
					station.disableAutoUpdate();
					System.out.println(String.format("Station is autoupdateing: %s", station.isAutoUpdating()));
					
					//Stop everything. DON'T do this. THis is jsut for practical purposes in this example
					System.exit(0);
					
				}
			});
			System.out.println("Observer added, waiting for update to happen");
			
			//Since the updating threads are daemonTHreads we need to wait here for something to happen
			Thread.sleep(60*1000*60L);

			
		} catch (IOException e) {
			System.out.println(
			      "Something went wrong while fetching the website. Check your network connection");
			System.out.println(e.getMessage());
		} catch (AmbigiuousStationNameException e) {
			// the entered station name was not recognized, there are some "Did you mean" recommendations
			System.out.println(
			      String.format("The entered StationName %s, was not unambiguous.", stationName));
			System.out.println("Did you mean: ");
			for (String station : e.getAlternativeNames()) {
				System.out.println("    " + station);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Something went wrong while parsing the website. Sorry");
			System.out.println(e.getMessage());
		}

	}


}
