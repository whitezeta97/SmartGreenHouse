package serverdata;

import java.util.ArrayList;
import java.util.List;

import utilities.Pair;

/**
 * 
 * Implements the SGH Server status data received from the sever.
 *
 */
public class ServerDataImpl implements ServerData {
	private static final int LIST_SIZE = 100;

	private volatile boolean isManualMode;
	private volatile boolean isWatering;
	private volatile String currentState;
	private List<Pair<Integer, String>> umidityValuesList;
	private List<Pair<Float, String>> wateringsList;
	private List<String> warningsList;
	private String lastUpdateFromServer;
	private int listSize;

	public ServerDataImpl() {
		this.umidityValuesList = new ArrayList<>(ServerDataImpl.LIST_SIZE);
		this.wateringsList = new ArrayList<>(ServerDataImpl.LIST_SIZE);
		this.warningsList = new ArrayList<>(ServerDataImpl.LIST_SIZE);
	}

	/**
	 * 
	 * @param listSize
	 *            the size of humidity, watering and warnings lists.
	 */
	public ServerDataImpl(final int listSize) {
		this.listSize = listSize;
		this.umidityValuesList = new ArrayList<>(this.listSize);
		this.wateringsList = new ArrayList<>(this.listSize);
		this.warningsList = new ArrayList<>(this.listSize);
	}

	/* Remove the first element of the list if it's full. */
	private <T> void manageListFull(final List<T> list) {
		if (list.size() >= ServerDataImpl.LIST_SIZE) {
			list.remove(0);
		}
	}

	@Override
	public void setManualMode(boolean manualMode) {
		this.isManualMode = manualMode;
	}

	@Override
	public void setWatering(boolean isWatering) {
		this.isWatering = isWatering;
	}

	@Override
	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}

	@Override
	public void addUmidityValuesListElement(final int umidityValue, String receivingDate) {
		this.manageListFull(this.umidityValuesList);
		this.umidityValuesList.add(new Pair<Integer, String>(umidityValue, receivingDate));

	}

	@Override
	public void addWateringsListElement(final float wateringDuration, String wateringDate) {
		this.manageListFull(this.wateringsList);
		this.wateringsList.add(new Pair<Float, String>(wateringDuration, wateringDate));
	}

	@Override
	public void addWarningsListElement(String warningDate) {
		this.manageListFull(this.warningsList);
		this.warningsList.add(warningDate);

	}

	@Override
	public void setLastUpdateFromServer(final String lastUpdateFromServer) {
		this.lastUpdateFromServer = lastUpdateFromServer;

	}

	@Override
	public boolean isManualMode() {
		return this.isManualMode;
	}

	@Override
	public boolean isWatering() {
		return this.isWatering;
	}

	@Override
	public String getCurrentState() {
		return this.currentState;
	}

	@Override
	public List<Pair<Integer, String>> getHumidityValuesList() {
		return this.umidityValuesList;
	}

	@Override
	public List<Pair<Float, String>> getWateringsList() {
		return this.wateringsList;
	}

	@Override
	public List<String> getWarningsList() {
		return this.warningsList;
	}

	@Override
	public String getLastUpdateFromServer() {
		return this.lastUpdateFromServer;
	}

}
