package clientdataservice;

import java.util.Date;
import java.util.List;

import utilities.Pair;

public interface SghClient {

	boolean isManualMode();

	boolean isWatering();

	String getCurrentState();

	List<Pair<Float, String>> getUmidityValuesList();

	List<Pair<Float, String>> getWateringsList();

	List<String> getWarningsList();

	Date getLastUpdateFromServer();
}
