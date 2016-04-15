package de.timmeey.oeffiwatch.station;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.timmeey.oeffiwatch.Grabber;
import de.timmeey.oeffiwatch.line.Line;
import de.timmeey.oeffiwatch.line.Line.Vehicle;
import de.timmeey.oeffiwatch.util.parser.IObservable;

/**
 * Station object that represents an actual station, including its soon departing lines.
 * 
 * This Class represents an actual station/busstop.... It's content is backed by what the Website
 * returned on its initialization or the last update.
 * 
 * A Station does have exactly one offical name, which is returned by the Website.
 * BUT a Station can have multiple alternativeNames, which all lead to the same station, but
 * are only discovered if such an alternativeName is queried.
 * 
 * A station can be observed, and will notify the observers if the list of soon departing lines change.
 * Be aware, such a change event can be fired at any point, due merging of Stations if a new alternativeName for 
 * a station is discovered. This "new" Station gets then merged with this station. If the "new" Station contains
 * more current data, the data is also merged, and a change Event can be fired.
 * 
 * Additionally this class is a Runnable, which is meant to be put into some periodic update mechanism
 * like TimerTask, to achieve something a auto updating Station.
 * Be aware, when used as a Runnable, the underlying data is only updated if there is at least 1 observer registered.
 * If you don't need advanced auto updating features, you can enableAutoUpdates by calling @method enableAutoUpdate.
 * This will add it to an executorService which then might start a thread for scheduled execution.
 * 
 * 
 * If you just want to update the data a single time, call the update() method.
 * 
 * This class is thread-safe
 * @author timmeey
 * 
 * @see Line
 * @see Grabber
 *
 */
public interface Station extends Runnable, IObservable{

	/**
	 * A List of all soon departing lines at this Station. Number of lines is
	 * limited by the API limit (enforced by the API)
	 * 
	 * @param vehicles
	 *           (optional)a List of Vehicles you want to query.
	 * @return All fetched lines (that meet the Vehicle.Type criteria) soon departing from this Station
	 */
	public List<Line> lines(Vehicle... vehicles);

	/**
	 * This stations name as returned by the Website
	 * 
	 * @return This stations name
	 */
	public String name();


	/**
	 * Polls the BVG Website for updated data
	 * 
	 * @return This station with updated values
	 * @throws IOException 
	 */
	public Station update() throws IOException;

	/**
	 * Time since the last successfull update from the website in ms
	 *
	 * @return Time since the last successfull update from the website
	 */
	public long timeSinceLastRefresh();

	/**
	 * Returns this Station including its soon departing lines as json
	 * @param prettyPrint Whether the returned json should be pretty printed (human readable) or not
	 * @return JSON representation of this station and it's lines
	 */
	public String toJson(boolean prettyPrint);

	
	
	/**
	 * Enables autoUpdating for this station.
	 * Updates are only run if @method countObservers >0
	 * 
	 * The initial delay is set to the periodic delay. (If you set period to 1 minute, the first update 
	 * is delayed by 1minute)
	 * 
	 * Calls to an already autoUpdating Station do nothing.
	 * 
	 * @param period the time between updates.
	 * @param unit the Timunit for the delay
	 * @return this station this station
	 */
	public Station enableAutoUpdate(int period, TimeUnit unit);
	
	/**
	 * Disables autoUpdate for this station. After this call, subsequent calls to @method isAutoUpdating
	 * will return false, unless @method enableAutoUpdate is called again.
	 * 
	 * Calls to an already disabled Station will do nothing
	 * @return this station
	 */
	public Station disableAutoUpdate();
	
	/**
	 * Return whether this Station is autoUpdating itself
	 * @return true when this station is auto updating itself
	 */
	public boolean isAutoUpdating();

	/**
	 * Returns the LocalTIme of the last update
	 * @return LocalDateTime of the last successful update
	 */
	public LocalDateTime lastUpdated();
	
	
	
	

}
