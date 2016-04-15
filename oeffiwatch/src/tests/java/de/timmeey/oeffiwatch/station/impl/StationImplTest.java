package de.timmeey.oeffiwatch.station.impl;

import static org.junit.Assert.*;


import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.timmeey.oeffiwatch.exception.AmbigiuousStationNameException;
import de.timmeey.oeffiwatch.exception.ParseException;
import de.timmeey.oeffiwatch.line.LineImplTest;
import de.timmeey.oeffiwatch.station.impl.StationImpl;
import de.timmeey.oeffiwatch.util.parser.HtmlStationParser;
import de.timmeey.oeffiwatch.util.parser.HtmlStationParser.ParseResult;

public class StationImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStationGenerationAndParsing()
	      throws IOException, AmbigiuousStationNameException, ParseException {
		try{
		String[][] tmp = { { "15:30", "Tra M13", "Warschau" } };
		ParseResult result = new ParseResult("Björnson", null, null, tmp);
		StationImpl station = new StationImpl("Björnson", LineImplTest.generateDefaultFactory(),
		      generateParserfor(result), null);
		assertEquals("Björnson", station.name());
		assertTrue(!station.lines().isEmpty());
		}catch(Exception e){
			fail("There should have been no exception");
		}
		
	}

	@Test(expected = AmbigiuousStationNameException.class)
	public void testStationAmbigiuousName()
	      throws IOException, AmbigiuousStationNameException, ParseException {
		String[][] tmp = { { "15:30", "Tra M13", "Warschau" } };
		String[] alternatives = { "muh", "Foo" };
		ParseResult result = new ParseResult(null, "Ambigiuous name", alternatives, null);
		StationImpl station = new StationImpl("Björnson", LineImplTest.generateDefaultFactory(),
		      generateParserfor(result), null);
	}

	@Test(expected = ParseException.class)
	public void testStationParseException()
	      throws IOException, AmbigiuousStationNameException, ParseException {
		String[][] tmp = { { "15:30", "Tra M13", "Warschau" } };
		String[] alternatives = { "muh", "Foo" };
		ParseResult result = new ParseResult(null, "Error", null, null);
		StationImpl station = new StationImpl("Björnson", LineImplTest.generateDefaultFactory(),
		      generateParserfor(result), null);
	}

	@Test
	public void testMergeWorks() throws IOException, AmbigiuousStationNameException,
	      ParseException, InterruptedException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		final String street1 = "Björnson";
		final String street2 = "Björnson (Berlin)";
		String[][] tmp = { { "15:30", "Tra M13", "Warschau" } };
		ParseResult result = new ParseResult(street1, null, null, tmp);
		StationImpl station1 = new StationImpl(street1, LineImplTest.generateDefaultFactory(),
		      generateParserfor(result), null);

		String[][] tmp1 = { { "15:31", "Tra M13", "Warschau" } };
		
		StationImpl stationToMerge = new StationImpl(street1, LineImplTest.generateDefaultFactory(),
		      generateParserfor(new ParseResult(street1, null, null, tmp1)), null);
		
		Class ftClass = stationToMerge.getClass();

		Field f1 = ftClass.getDeclaredField("lastUpdated");
		f1.setAccessible(true);
		f1.set(stationToMerge, station1.lastUpdated().plus(10,ChronoUnit.SECONDS));

		station1.merge(stationToMerge, street2);

		assertEquals(
		      String.format(
		            "Timestamp of station1 should equals the timestamp of station2 after the merge. Station1: %s, station2: %s",
		            station1.timeSinceLastRefresh(), stationToMerge.timeSinceLastRefresh()),
		      stationToMerge.timeSinceLastRefresh(), station1.timeSinceLastRefresh());

		assertEquals(
		      String.format(
		            "The Line object in station1 should point to the same Line list from station2 after the merge"),
		      stationToMerge.lines(), station1.lines());

		assertEquals("Alternative station names, should now contain 2 elements after the merge", 2,
		      station1.getAlternativeStationNames().size());
		assertTrue(String.format("Alternative station name should now contain %s", street2),
		      station1.getAlternativeStationNames().contains(street2));

	}

	/**
	 * If a station with a wrong station name is merged, there should be an
	 * exception
	 * 
	 * @throws IOException
	 * @throws AmbigiuousStationNameException
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMergeRejectsName() throws IOException, AmbigiuousStationNameException,
	      ParseException, InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final String street1 = "Björnson";
		final String street2 = "Björnson (Berlin)";
		String[][] tmp = { { "15:30", "Tra M13", "Warschau" } };
		ParseResult result = new ParseResult(street1, null, null, tmp);
		StationImpl station1 = new StationImpl(street1, LineImplTest.generateDefaultFactory(),
		      generateParserfor(result), null);
		

		StationImpl stationToMerge = new StationImpl(street1, LineImplTest.generateDefaultFactory(),
		      generateParserfor(new ParseResult(street2, null, null, tmp)), null);
		
		Class ftClass = stationToMerge.getClass();

		Field f1 = ftClass.getDeclaredField("lastUpdated");
		f1.setAccessible(true);
		f1.set(stationToMerge, station1.lastUpdated().plus(10,ChronoUnit.SECONDS));
		station1.merge(stationToMerge, street2);
	}

	/**
	 * Merging a station with a timestamp older than the actual stations
	 * timestamp should do nothing, but adding the line name to the list of
	 * alternative names
	 * @throws ParseException 
	 * @throws AmbigiuousStationNameException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@Test
	public void testMergeDiscardsTimestamp() throws IOException, AmbigiuousStationNameException, ParseException, InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final String street1 = "Björnson";
		final String street2 = "Björnson (Berlin)";
		
		                   // i'm too lazy to mock System.currentTimeMillis()

		String[][] tmp1 = { { "15:29", "Tra M13", "Warschau" } };
		StationImpl stationToMerge = new StationImpl(street1, LineImplTest.generateDefaultFactory(),
		      generateParserfor(new ParseResult(street1, null, null, tmp1)), null);


		String[][] tmp = { { "15:30", "Tra M13", "Warschau" } };
		ParseResult result = new ParseResult(street1, null, null, tmp);
		StationImpl station1 = new StationImpl(street1, LineImplTest.generateDefaultFactory(),
		      generateParserfor(result), null);
		
		Class ftClass = stationToMerge.getClass();

		Field f1 = ftClass.getDeclaredField("lastUpdated");
		f1.setAccessible(true);
		f1.set(station1,stationToMerge.lastUpdated().plus(10,ChronoUnit.SECONDS));
		station1.merge(stationToMerge, street2);
		
		assertNotEquals(
		      String.format(
		            "Timestamp of station1 should NOT equals the timestamp of station2 after the merge. Station1: %s, station2: %s",
		            station1.timeSinceLastRefresh(), stationToMerge.timeSinceLastRefresh()),
		      stationToMerge.timeSinceLastRefresh(), station1.timeSinceLastRefresh());

		assertNotEquals(
		      String.format(
		            "The Line object in station1 should NOT point to the same Line list from station2 after the merge"),
		      stationToMerge.lines(), station1.lines());

		assertEquals("Alternative station names, should now contain 2 elements after the merge", 2,
		      station1.getAlternativeStationNames().size());
		assertTrue(String.format("Alternative station name should now contain %s", street2),
		      station1.getAlternativeStationNames().contains(street2));

	}

	@Test
	public void testGetsNewTimestampOnUpdate() throws IOException, AmbigiuousStationNameException, ParseException, InterruptedException {
		final String street1 = "Björnson";
		String[][] tmp = { { "15:29", "Tra M13", "Warschau" } };
		StationImpl station = new StationImpl(street1, LineImplTest.generateDefaultFactory(),
		      generateParserfor(new ParseResult(street1, null, null, tmp)), null);
		LocalDateTime oldTimestamp = station.lastUpdated(); // NOSONAR
		tmp[0] = new String[3];
		tmp[0][0] = "15:30";
		tmp[0][1] = "Tra M13";
		tmp[0][2] = "Warschau";
		Thread.sleep(10); // NOSONAR
		station.update();
		assertTrue("Timestamp should have changed",oldTimestamp.isBefore(station.lastUpdated()) );

	}


	public static HtmlStationParser generateParserfor(ParseResult result) {
		return new HtmlStationParser() {

			@Override
			public ParseResult stationLineInfo(String stationName) throws IOException {
				return result;
			}
		};
	}

}
