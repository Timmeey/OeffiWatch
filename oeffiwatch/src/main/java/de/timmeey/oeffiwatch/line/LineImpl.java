package de.timmeey.oeffiwatch.line;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.timmeey.oeffiwatch.Grabber;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal Class representing an actual line
 *
 * @author timmeey
 *
 */
public class LineImpl implements Line {
	private static final Gson		gson		= new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
	      .registerTypeAdapter(LocalDateTime.class, Grabber.getDateTimeSerializer()).create();

	private static final Logger	LOGGER	= LoggerFactory.getLogger(LineImpl.class);
	@Expose
	private final LocalDateTime	departureTime;
	@Expose
	private final String				lineName;
	@Expose
	private final String				destination;
	@Expose
	private final Vehicle			type;
	@Expose
	private final Integer			platform;

	/**
	 * Internal method used by the injector/factory to create Lines
	 *
	 * @param departureTime
	 *           THe departure string as returned by the website
	 * @param lineName
	 *           The full identifier as returned by the website
	 * @param destination
	 *           the destination string as returned by the website
	 */
	@Inject
	LineImpl(@Assisted("departureTime") String departureTime, @Assisted("lineName") String lineName,
	      @Assisted("destination") String destination) {
		LOGGER.trace("Creating Line for time: {} name: {} destination: {}", departureTime, lineName,
		      destination);

		this.destination = Preconditions.checkNotNull(destination);
		this.departureTime = getDepartureDateTime(Preconditions.checkNotNull(departureTime),
		      LocalDateTime.now());
		this.type = getVehicleType(Preconditions.checkNotNull(lineName));
		this.lineName = lineName(lineName, type);
		this.platform = getPlatform(lineName);
		LOGGER.trace(
		      "Line created. Destination: {}, departureTim: {}, type: {}, lineName: {}, platform: {}",
		      this.destination, this.departureTime, this.type, this.lineName, this.platform);
	}

	/**
	 * Extracts the actual Name of the Line from its identifier
	 *
	 * @param name
	 *           THe full identifier as returned by the website
	 * @param type
	 *           the already extracted type of this line
	 * @return the name of this line
	 */
	static String lineName(String name, Vehicle type) {
		String workingName;
		workingName = name.replace("Bus", "");
		workingName = workingName.replace("Tra", "");
		workingName = workingName.trim();
		if (workingName.contains(" ")) {
			workingName = workingName.split(" ")[0];
		}
		String regex = "[0-9]+";
		if (workingName.matches(regex)) {
			// Name only contains digits, appending Vehicle type to name
			workingName = type.name().concat(" ").concat(workingName);
		}
		return workingName;

	}

	/**
	 * Extracts the type of this line, from its identifier String
	 *
	 * @param name
	 *           the identifier String as returned by the website
	 * @return the Vehicle type
	 */
	static Vehicle getVehicleType(String name) {
		// try simple
		Vehicle tmpType = Vehicle.getVehicleByShortcode(name.split(" ")[0]);
		if (!tmpType.equals(Vehicle.Unkown)) {
			return tmpType;
		}
		tmpType = Vehicle.getVehicleByShortcode(name.substring(0, 0));
		if (!tmpType.equals(Vehicle.Unkown)) {
			return tmpType;
		}

		tmpType = Vehicle.getVehicleByShortcode(name.substring(0, 1));
		if (!tmpType.equals(Vehicle.Unkown)) {
			return tmpType;
		}
		// Fallthrough
		return Vehicle.Unkown;
	}

	/**
	 * Extracts the Platform number, this line is leaving from (if present), from
	 * its identifier String as returned by the website
	 *
	 * @param name
	 *           The identifier string, as returned by the website
	 * @return the Platform number, if present or NULL otherwise
	 */
	static Integer getPlatform(String name) {
		try {
			String workingName;
			int index = name.indexOf("(Gl. ");
			if (index > -1) {
				// Contains "(Gl. "

				workingName = name.substring(index + 5);
				workingName = workingName.substring(0, workingName.length() - 1);
				return Integer.parseInt(workingName.trim());
			} else {
				return null;
			}
		} catch (Exception e) {
			LOGGER.warn(
			      "Expected to find a Platform number in the form of :\"(Gl. [0-9]+\". "
			            + "But something({}) went wrong with the name {}. {}",
			      e.getMessage(), name, e);
			return null;
		}

	}

	/**
	 * Extracts/Expands the departure time, as returned by the website, into a
	 * full DateTime object
	 *
	 * @param departure
	 *           the departure ime as retunred by the website (time string)
	 * @return Datetime of departure
	 */
	static LocalDateTime getDepartureDateTime(String departure, LocalDateTime nowDateTime) {
		LocalTime internalDepartureTime = LocalTime.parse(departure.replace("*",""));
		LocalTime nowTime = LocalTime.now();
		LocalDateTime departureDateTime;
		// Determines if the departure time means (probably) the next day.
		// Departure time 00:19, now 23:42. In that case an extra day is added
		// during the conversion
		// to a DateTime for the departure time, because it is probably the next
		// day.
		if (Math.abs(internalDepartureTime.until(nowTime, ChronoUnit.HOURS)) > 12) {
			departureDateTime = internalDepartureTime.atDate(nowDateTime.toLocalDate());
			departureDateTime = departureDateTime.plus(1, ChronoUnit.DAYS);

		} else {
			departureDateTime = internalDepartureTime.atDate(nowDateTime.toLocalDate());
		}

		return departureDateTime;
	}

	@Override
	public String lineName() {
		return this.lineName;
	}

	@Override
	public String lineEnd() {
		return this.destination;
	}

	@Override
	public long estimatedDepartureTimefromNow() {
		return LocalDateTime.now().until(this.departureTime, ChronoUnit.MINUTES);
	}

	@Override
	public LocalDateTime estimatedDepartureTime() {
		return this.departureTime;
	}

	@Override
	public Vehicle vehicleType() {
		return this.type;
	}

	@Override
	public Optional<Integer> getPlatform() {
		return Optional.ofNullable(this.platform);
	}

	/**
	 * Internal method to get a serializer for this class, to be used by Gson
	 *
	 * @return a Gson conform serializer for this class
	 */
	public static JsonSerializer<LineImpl> getSerializer() {
		return new JsonSerializer<LineImpl>() { // NOSONAR
			@Override
			public JsonElement serialize(LineImpl src, Type typeOfSrc,
		         JsonSerializationContext context) {
				JsonObject tree = (JsonObject) gson.toJsonTree(src);
				tree.addProperty("departureIn", src.estimatedDepartureTimefromNow());
				return tree;
			}
		};

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((departureTime == null) ? 0 : departureTime.hashCode());
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((lineName == null) ? 0 : lineName.hashCode());
		result = prime * result + ((platform == null) ? 0 : platform.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LineImpl)) {
			return false;
		}
		LineImpl other = (LineImpl) obj;
		if (departureTime == null) {
			if (other.departureTime != null) {
				return false;
			}
		} else if (!departureTime.equals(other.departureTime)) {
			return false;
		}
		if (destination == null) {
			if (other.destination != null) {
				return false;
			}
		} else if (!destination.equals(other.destination)) {
			return false;
		}
		if (lineName == null) {
			if (other.lineName != null) {
				return false;
			}
		} else if (!lineName.equals(other.lineName)) {
			return false;
		}
		if (platform == null) {
			if (other.platform != null) {
				return false;
			}
		} else if (!platform.equals(other.platform)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

}
