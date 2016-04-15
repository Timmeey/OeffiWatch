package de.timmeey.oeffiwatch.line;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * This class represents an actual scheduled/estimated Departure (Bus/Train...) from a Station.
 * 
 * A line represents exactly one soon departing Bus/Train... from a Station, including its estimated time of departure.
 * (Only estimated departures are implemented, scheduled departures are planned for a future release)
 * 
 * Instances of this Class are immutable and are not updated by updating the Station. (If an estimated departure time
 * changes, the change will not be visible to an instance of this class)
 * @author timmeey
 *
 */
public interface Line {
	/**A Vehicle represents the Type of this line, whether it is a Tram/Straßenbahn or UBahn/Subway and so on.
	 * @author timmeey
	 *
	 */
	public enum Vehicle {
		Tram("Tra","Tram"), UBahn("U","U-Bahn"), SBahn("S","S-Bahn"), Faehre("F","Fähre"), Bus("Bus","Bus"), RegionalExpress(// NOSONAR
		      "RE","Regional Express"), RegionalBahn("RB","Regional Bahn"), InterCityExpress("ICE","Intercity Express"), InterCity("IC","Intercity"), Unkown("unkown","Unbekannt"); // NOSONAR

		private String shortCode;
		private String name;

		private Vehicle(String shortCode,String name) {
			this.shortCode = shortCode;
			this.name = name;
		}

		public String getShortcode() {
			return this.shortCode;
		}
		
		public String getName(){
			return this.name;
		}

		public static Vehicle getVehicleByShortcode(String shortCode) {
			for (Vehicle vehicle : Vehicle.values()) {
				if (vehicle.shortCode.equals(shortCode)) {
					return vehicle;
				}
			}
			return Unkown;

		}
		
		public static Vehicle getVehicleByName(String name) {
			for (Vehicle vehicle : Vehicle.values()) {
				if (vehicle.shortCode.equals(name)) {
					return vehicle;
				}
			}
			return Unkown;

		}
	}

	/**
	 * Gets the Name of the Line (e.g. U2 or M13)
	 * 
	 * @return The name of the Line
	 */
	public String lineName();

	/**
	 * The end station of the line
	 * 
	 * @return The name of the last station for the Line
	 */
	public String lineEnd();

	/**
	 * Gives the estimated actual departureTime for this line at the
	 * queryStation, in MINUTES Can be negative to indicate that the Line has
	 * already departed.
	 * 
	 * @return The estimated remaining time until departure for this line at the
	 *         queryStation
	 */
	public long estimatedDepartureTimefromNow();

	/**
	 * Gives the estimated Date of Departure of this line at the queried Station
	 * 
	 * @return The estimated Departure time as Date
	 */
	public LocalDateTime estimatedDepartureTime();

	/**
	 * The Type of this Line Tram,Bus,Sbahn....
	 * 
	 * @return The type of this line
	 */
	public Vehicle vehicleType();

	/**
	 * Returns the Platformnumber this Train is leaving from
	 * 
	 * @return THe Platformnumber this train is leaving from, or null, of no
	 *         platform was given
	 */
	public Optional<Integer> getPlatform();

}
