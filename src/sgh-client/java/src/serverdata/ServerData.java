package serverdata;

import java.util.List;

import utilities.Pair;

/**
 * 
 * Represents the SGH Server status data received from the sever.
 *
 */
public interface ServerData {

	/**
	 * Sets the current manual mode status.
	 * 
	 * @param manualMode
	 *            it's true if SGH is in manual mode, it's false otherwise.
	 */
	void setManualMode(boolean manualMode);

	/**
	 * Sets the current watering mode status.
	 * 
	 * @param isWatering
	 *            it's true if SGH is watering, it's false otherwise.
	 */
	void setWatering(boolean isWatering);

	/**
	 * Sets the current SGH status.
	 * 
	 * @param currentState
	 *            the current state of SGH.
	 */
	void setCurrentState(String currentState);

	/**
	 * Adds a humidity value to the list.
	 * 
	 * @param humidityValue
	 *            the humidity value.
	 * @param receivingDate
	 *            the receiving Date of the humidity value.
	 */
	void addUmidityValuesListElement(Float umidityValue, String receivingDate);

	/**
	 * Adds a watering to the waterings list.
	 * 
	 * @param wateringDuration
	 *            the watering duration.
	 * @param wateringDate
	 *            the watering Date.
	 */
	void addWateringsListElement(Float wateringDuration, String wateringDate);

	/**
	 * Add a warning to the warnings list.
	 * 
	 * @param warningDate
	 *            the warning Date.
	 */
	void addWarningsListElement(String warningDate);

	/**
	 * Sets the last update instant received from the server.
	 * 
	 * @param lastUpdateFromServer
	 *            the last update instant received from the server.
	 */
	void setLastUpdateFromServer(String lastUpdateFromServer);

	/**
	 * Returns true if SGHR is in manual mode, returns false otherwise.
	 * 
	 * @return the SGH manual mode status.
	 */
	boolean isManualMode();

	/**
	 * Returns true if SGHR is watering, returns false otherwise.
	 * 
	 * @return the SGH watering status.
	 */
	boolean isWatering();

	/**
	 * Gets the current SGH state.
	 * 
	 * @return the current SGH state.
	 */
	String getCurrentState();

	/**
	 * Returns the received humidity values and Date.
	 * 
	 * @return the humidity values and Date list.
	 */
	List<Pair<Float, String>> getHumidityValuesList();

	/**
	 * Returns the received waterings Dates.
	 * 
	 * @return the waterings Date list.
	 */
	List<Pair<Float, String>> getWateringsList();

	/**
	 * Returns the warnings list.
	 * 
	 * @return the warnings list with Dates.
	 */
	List<String> getWarningsList();

	/**
	 * Gets the instant of the last update received from the server.
	 * 
	 * @return the instant of the last update received from the server.
	 */
	String getLastUpdateFromServer();

}
