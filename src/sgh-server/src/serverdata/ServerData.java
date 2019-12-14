package serverdata;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

import utilities.Pair;
import utilities.SghStates;

public interface ServerData {

	void setManualMode(boolean manualMode);

	void setWatering(boolean isWatering);

	void setCurrentState(SghStates currentState);

	void addUmidityValuesListElement(Float umidityValue, Date receivingDate);

	void addWateringsListElement(long wateringDuration, Date wateringDate);

	void addWarningsListElement(Date warningDate);
	
	boolean isManualMode();

	boolean isWatering();

	SghStates getCurrentState();

	BlockingQueue<Pair<Float, Date>> getUmidityValuesList();

	BlockingQueue<Pair<Long, Date>> getWateringsList();

	BlockingQueue<Date> getWarningsList();

}
