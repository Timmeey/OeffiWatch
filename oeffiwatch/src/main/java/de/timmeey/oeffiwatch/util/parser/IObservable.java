package de.timmeey.oeffiwatch.util.parser;

import java.util.Observer;

public interface IObservable {

	/* (non-Javadoc)
	 * @see java.util.Observable#addObserver(java.util.Observer)
	 */
	void addObserver(Observer o);

	/* (non-Javadoc)
	 * @see java.util.Observable#deleteObserver(java.util.Observer)
	 */
	void deleteObserver(Observer o);

	/* (non-Javadoc)
	 * @see java.util.Observable#notifyObservers()
	 */
	void notifyObservers();

	/* (non-Javadoc)
	 * @see java.util.Observable#notifyObservers(java.lang.Object)
	 */
	void notifyObservers(Object arg);

	/* (non-Javadoc)
	 * @see java.util.Observable#deleteObservers()
	 */
	void deleteObservers();

	/* (non-Javadoc)
	 * @see java.util.Observable#hasChanged()
	 */
	boolean hasChanged();

	/* (non-Javadoc)
	 * @see java.util.Observable#countObservers()
	 */
	int countObservers();

}