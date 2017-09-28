/**
 *
 */
package de.timmeey.oeffiwatch.line;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.timmeey.oeffiwatch.line.Line;
import de.timmeey.oeffiwatch.line.LineFactory;
import de.timmeey.oeffiwatch.line.LineImpl;
import de.timmeey.oeffiwatch.line.Line.Vehicle;

/**
 * @author timmeey
 *
 */
public class LineImplTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link de.timmeey.oeffiwatch.line.LineImplTest#lineName()}.
	 */
	@Test
	public final void testLineName() {
		assertEquals("S8", new LineImpl("14:36", "S8 (Gl. 5 )", "Hennigsdorf","").lineName());
		assertEquals("S8", new LineImpl("14:36", "S8", "Hennigsdorf","").lineName());
		assertEquals("U9", new LineImpl("14:36", "U9", "Hennigsdorf","").lineName());
		assertEquals("M13", new LineImpl("14:36", "Tram M13", "Hennigsdorf","").lineName());
		assertEquals("Tram 50", new LineImpl("14:36", "Tram 50", "Hennigsdorf","").lineName());

	}

	/**
	 * Test method for
	 * {@link de.timmeey.oeffiwatch.line.LineImplTest#estimatedDepartureTimefromNow()}
	 * .
	 */
	@Test
	// I know, this test might break if in between line 91 and 93 the minute
	// changes (clock jumps from 14:34 to 14:35 or something)

	public final void testEstimatedDepartureTimefromNow() {
		LocalTime now = LocalTime.now();
		LocalTime departure = now.plus(5, ChronoUnit.MINUTES);
		// Departure is in 4 Minutes, since we set the departure time to
		// NOW+5Minutes (so it is 4:59:99999 minutes:seconds:ms)
		assertEquals(4, new LineImpl(departure.toString(), "S8 (Gl. 5 )", "Hennigsdorf","")
		      .estimatedDepartureTimefromNow());
	}

	/**
	 * Test method for
	 * {@link de.timmeey.oeffiwatch.line.LineImplTest#estimatedDepartureTime()}.
	 */
	@Test
	public final void testEstimatedDepartureTime() {
		LocalTime now = LocalTime.now();
		LocalTime departure = now.plus(5, ChronoUnit.MINUTES);
		LocalDateTime departureDateTime = LocalDateTime.of(LocalDate.now(), departure);
		// Departure is in 4 Minutes, since we set the departure time to
		// NOW+5Minutes (so it is 4:59:99999 minutes:seconds:ms)
		assertEquals(departureDateTime,
		      new LineImpl(departure.toString(), "S8 (Gl. 5 )", "Hennigsdorf","")
		            .estimatedDepartureTime());
	}

	/**
	 * Test method for {@link de.timmeey.oeffiwatch.line.LineImplTest#vehicleType()}.
	 */
	@Test
	public final void testType() {
		assertEquals(Vehicle.SBahn, new LineImpl("14:36", "S8 (Gl. 5 )", "Hennigsdorf","").vehicleType());
		assertEquals(Vehicle.Tram, new LineImpl("14:36", "Tram 50", "Hennigsdorf","").vehicleType());
		assertEquals(Vehicle.Tram, new LineImpl("14:36", "Tram M13", "Hennigsdorf","").vehicleType());
		assertEquals(Vehicle.UBahn, new LineImpl("14:36", "U8", "Hennigsdorf","").vehicleType());
		assertEquals(Vehicle.Bus, new LineImpl("14:36", "Bus X96", "Hennigsdorf","").vehicleType());
		assertEquals(Vehicle.Faehre, new LineImpl("14:36", "F X96", "Hennigsdorf","").vehicleType());

	}

	@Test
	public final void testGetPlatform() {
		assertEquals(String.format("Should return 5 as Platform number"), new Integer(5),
		      LineImpl.getPlatform("S8 (Gl. 5 )"));
	}

	@Test
	public final void testDepartureTimeCalculation(){
//		fail();
	}

	@Test
	public final void testDepartureTimeOverDateline(){
//		fail();
	}

	public static LineFactory generateDefaultFactory() {
		return new LineFactory() {

			@Override
			public Line create(String departureTime, String lineName, String destination, String routeUrl) {
				// TODO Auto-generated method stub
				return new LineImpl(departureTime, lineName, destination,routeUrl);
			}
		};
	}


}
