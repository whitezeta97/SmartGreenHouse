package clientdataservice;

import java.util.List;

import utilities.Pair;

public interface SghClient {

	public boolean isManualMode();

	public boolean isWatering();

	public String getCurrentState();

	public List<Pair<Float, String>> getUmidityValuesList();

	public List<Pair<Long, String>> getWateringsList();

	public List<String> getWarningsList();
}
