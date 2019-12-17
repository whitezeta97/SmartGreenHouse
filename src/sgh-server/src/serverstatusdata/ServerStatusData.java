package serverstatusdata;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

import utilities.Pair;
import utilities.SghPumpStates;

/**
 * Represents the current SGH status and data.
 *
 */
public interface ServerStatusData {

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
	void setCurrentState(SghPumpStates currentState);

	/**
	 * Adds a humidity value to the list. If the list is full the first element of
	 * the list is removed and the new element is added.
	 * 
	 * @param humidityValue
	 *            the humidity value.
	 * @param receivingDate
	 *            the receiving Date of the humidity value.
	 */
	void addHumidityValuesListElement(float humidityValue, Date receivingDate);

	/**
	 * Adds a watering to the waterings list. If the list is full the first element
	 * of the list is removed and the new element is added.
	 * 
	 * @param wateringDuration
	 *            the watering duration.
	 * @param wateringDate
	 *            the watering Date.
	 */
	void addWateringsListElement(long wateringDuration, Date wateringDate);

	/**
	 * Add a warning to the warnings list. If the list is full the first element of
	 * the list is removed and the new element is added.
	 * 
	 * @param warningDate
	 *            the warning Date.
	 */
	void addWarningsListElement(Date warningDate);

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
	SghPumpStates getCurrentState();

	/**
	 * Returns the received humidity values and Date.
	 * 
	 * @return the humidity values and Date list.
	 */
	BlockingQueue<Pair<Float, Date>> getHumidityValuesList();

	/**
	 * Returns the received waterings Dates.
	 * 
	 * @return the waterings Date list.
	 */
	BlockingQueue<Pair<Long, Date>> getWateringsList();

	/**
	 * Returns the warnings list.
	 * 
	 * @return the warnings list with Dates.
	 */
	BlockingQueue<Date> getWarningsList();

}
