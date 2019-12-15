package controller;

import serverdata.ServerData;

/**
 * 
 * Represents the application controller. It allows the application and every
 * its components to start.
 *
 */
public interface Controller {
	/**
	 * Gets the data that must be displayed from the GUI.
	 * 
	 * @return the data that must be displayed from the GUI.
	 */
	ServerData getDataForView();
}
