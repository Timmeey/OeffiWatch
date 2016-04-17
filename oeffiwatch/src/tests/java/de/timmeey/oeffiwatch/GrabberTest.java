package de.timmeey.oeffiwatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.timmeey.oeffiwatch.exception.AmbigiuousStationNameException;
import de.timmeey.oeffiwatch.exception.ParseException;
import de.timmeey.oeffiwatch.line.Line;
import de.timmeey.oeffiwatch.line.LineFactory;
import de.timmeey.oeffiwatch.station.StationFactory;
import de.timmeey.oeffiwatch.station.impl.StationImpl;
import de.timmeey.oeffiwatch.station.impl.StationImplTest;

public class GrabberTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	/**
	 * Test whether the auto detection and merging of queried station Works.
	 * Oftentimes a Station can be queried unambigiuously by the same Station name.
	 * In this case ("Björnson Str." && "Björnson Str. (Berlin)" && "Björnson Straße (Berlin)") are the same station
	 * and the same station will be returned. If that happens, we don't want to give the user
	 * the "new" station, but merely update the old stations lines to the most current data, and return that station instead.
	 * 
	 * In this testCase the StationNames "Straße1" and "Straße2" are referring to the same Station.
	 * So the second query should yield the SAME object as the first query.
	 * @throws IOException
	 * @throws AmbigiuousStationNameException
	 */
	public final void testAutoDetectionAndMergingOfSameStation() throws IOException, AmbigiuousStationNameException, ParseException {
		final String realStationName = "Straße1";
		final String unambigiuousDoubleStationName = "Straße2";
	
		StationFactory factory = new StationFactory() {
			
			@Override
			public StationImpl create(String stationName,ScheduledExecutorService updateExecutor) throws IOException, AmbigiuousStationNameException, ParseException {
				if(stationName.equals(realStationName) || stationName.equals(unambigiuousDoubleStationName)){
					return new StationImpl(realStationName,getLineFactory(),StationImplTest.generateParserfor(StationImplTest.generateParseResult(realStationName,new String[0][], null, null,null )), updateExecutor); 
				}else{
					return new StationImpl("ShouldBeDifferentSTreet",getLineFactory(),StationImplTest.generateParserfor(StationImplTest.generateParseResult("ShouldBeDifferentStreet",new String[0][], null, null,null )), updateExecutor); 

				}
			}

		};
		Grabber grabber = new Grabber(factory);
		StationImpl firstStation = (StationImpl)grabber.getStation(realStationName);
		StationImpl secondStation = (StationImpl)grabber.getStation(unambigiuousDoubleStationName);
		//Since the first two stations are mocked to be the same station, it should only have 1 station in its real station list
		assertEquals(1,grabber.countKnownStations());
		StationImpl differentStation = (StationImpl)grabber.getStation("ShouldBeDifferentSTreet");
		grabber.getStation(realStationName);
		
		assertEquals(2,grabber.countKnownStations());

		assertEquals(realStationName,firstStation.name());
		assertEquals(realStationName,secondStation.name());
		assertNotEquals(unambigiuousDoubleStationName,secondStation.name());
		assertTrue(String.format("Both stationobjects should referr to the same object in memory. FirstStation: %s, secondsStation: %s", ((Object)firstStation.toString()),((Object)secondStation.toString())), firstStation==secondStation);
		assertTrue(String.format("The station should now contain %s as an alternative name for itself", unambigiuousDoubleStationName),firstStation.getAlternativeStationNames().contains(unambigiuousDoubleStationName));
		assertEquals(String.format("The station should only contain 2 alternative names, But contained: %s",firstStation.getAlternativeStationNames()),2,firstStation.getAlternativeStationNames().size());
		assertEquals(String.format("The station should only contain 1 alternative name, But contained: %s",differentStation.getAlternativeStationNames()),1,differentStation.getAlternativeStationNames().size());


		
		
		assertNotEquals(realStationName,differentStation.name());
	}
	
	LineFactory getLineFactory(){
		return new LineFactory() {
			
			@Override
			public Line create(String departureTime, String lineName, String destination) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	

	

}
