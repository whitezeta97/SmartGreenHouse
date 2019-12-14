package serverdata;

import java.util.List;

import utilities.Pair;

public interface ServerData {

	void setManualMode(boolean manualMode);

	void setWatering(boolean isWatering);

	void setCurrentState(String currentState);

	void addUmidityValuesListElement(Float umidityValue, String receivingDate);

	void addWateringsListElement(Float wateringDuration, String wateringDate);

	void addWarningsListElement(String warningDate);
	
	void setLastUpdateFromServer(String lastUpdateFromServer);

	boolean isManualMode();

	boolean isWatering();

	String getCurrentState();

	List<Pair<Float, String>> getUmidityValuesList();

	List<Pair<Float, String>> getWateringsList();

	List<String> getWarningsList();
	
	String getLastUpdateFromServer();

}
