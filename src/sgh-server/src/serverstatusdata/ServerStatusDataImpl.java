package serverstatusdata;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import utilities.Pair;
import utilities.SghPumpStates;

/**
 * Implements the current SGH status and data.
 *
 */
public class ServerStatusDataImpl implements ServerStatusData {
	private static final int LIST_SIZE = 100;

	private volatile boolean isManualMode;
	private volatile boolean isWatering;
	private volatile SghPumpStates currentState;
	private BlockingQueue<Pair<Integer, Date>> humidityValuesList;
	private BlockingQueue<Pair<Long, Date>> wateringsList;
	private BlockingQueue<Date> warningsList;
	private int listSize = 100;

	public ServerStatusDataImpl() {
		this.humidityValuesList = new ArrayBlockingQueue<>(ServerStatusDataImpl.LIST_SIZE);
		this.wateringsList = new ArrayBlockingQueue<>(ServerStatusDataImpl.LIST_SIZE);
		this.warningsList = new ArrayBlockingQueue<>(ServerStatusDataImpl.LIST_SIZE);
	}

	/**
	 * 
	 * @param listSize
	 *            the size of humidity, watering and warnings lists.
	 */
	public ServerStatusDataImpl(final int listSize) {
		this.listSize = listSize;
		this.humidityValuesList = new ArrayBlockingQueue<>(this.listSize);
		this.wateringsList = new ArrayBlockingQueue<>(this.listSize);
		this.warningsList = new ArrayBlockingQueue<>(this.listSize);
	}

	/**
	 * Removes the first element from the list if it's full.
	 * 
	 * @param list
	 *            the list that must be managed.
	 */
	private <T> void manageListFull(final BlockingQueue<T> list) {
		if (list.size() >= ServerStatusDataImpl.LIST_SIZE) {
			list.poll();
		}
	}

	@Override
	public void setManualMode(final boolean manualMode) {
		this.isManualMode = manualMode;
	}

	@Override
	public void setWatering(final boolean isWatering) {
		this.isWatering = isWatering;
	}

	@Override
	public void setCurrentState(SghPumpStates currentState) {
		this.currentState = currentState;
	}

	@Override
	public void addHumidityValuesListElement(final int humidityValue, final Date receivingDate) {
		this.manageListFull(this.humidityValuesList);
		this.humidityValuesList.add(new Pair<Integer, Date>(humidityValue, receivingDate));

	}

	@Override
	public void addWateringsListElement(final long wateringDuration, final Date wateringDate) {
		this.manageListFull(this.wateringsList);
		this.wateringsList.add(new Pair<Long, Date>(wateringDuration, wateringDate));
	}

	@Override
	public void addWarningsListElement(final Date warningDate) {
		this.manageListFull(this.warningsList);
		this.warningsList.add(warningDate);

	}

	@Override
	public synchronized boolean isManualMode() {
		return this.isManualMode;
	}

	@Override
	public boolean isWatering() {
		return this.isWatering;
	}

	@Override
	public SghPumpStates getCurrentState() {
		return this.currentState;
	}

	@Override
	public BlockingQueue<Pair<Integer, Date>> getHumidityValuesList() {
		return this.humidityValuesList;
	}

	@Override
	public BlockingQueue<Pair<Long, Date>> getWateringsList() {
		return this.wateringsList;
	}

	@Override
	public BlockingQueue<Date> getWarningsList() {
		return this.warningsList;
	}

}
