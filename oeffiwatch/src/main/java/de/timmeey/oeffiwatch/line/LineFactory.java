package de.timmeey.oeffiwatch.line;

import com.google.inject.assistedinject.Assisted;

@FunctionalInterface
public interface LineFactory {
	public Line create(@Assisted("departureTime") String departureTime,
	      @Assisted("lineName") String lineName, @Assisted("destination") String destination, @Assisted("routeUrl") String routeUrl );

}
