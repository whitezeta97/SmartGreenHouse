package serverdata;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import utilities.Pair;
import utilities.SghStates;

public class ServerDataImpl implements ServerData {
	private volatile boolean isManualMode;
	private volatile boolean isWatering;
	private volatile SghStates currentState;
	private BlockingQueue<Pair<Float, Date>> umidityValuesList;
	private BlockingQueue<Pair<Long, Date>> wateringsList;
	private BlockingQueue<Date> warningsList;

	private static final int LIST_SIZE = 100;

	public ServerDataImpl() {
		this.umidityValuesList = new ArrayBlockingQueue<>(ServerDataImpl.LIST_SIZE);
		this.wateringsList = new ArrayBlockingQueue<>(ServerDataImpl.LIST_SIZE);
		this.warningsList = new ArrayBlockingQueue<>(ServerDataImpl.LIST_SIZE);
	}

	private <T> void manageListFull(final BlockingQueue<T> list) {
		if (list.size() >= ServerDataImpl.LIST_SIZE) {
			list.poll();
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
	public void setCurrentState(SghStates currentState) {
		this.currentState = currentState;
	}

	@Override
	public void addUmidityValuesListElement(Float umidityValue, Date receivingDate) {
		this.manageListFull(this.umidityValuesList);
		this.umidityValuesList.add(new Pair<Float, Date>(umidityValue, receivingDate));

	}

	@Override
	public void addWateringsListElement(long wateringDuration, Date wateringDate) {
		this.manageListFull(this.wateringsList);
		this.wateringsList.add(new Pair<Long, Date>(wateringDuration, wateringDate));
	}

	@Override
	public void addWarningsListElement(Date warningDate) {
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
	public SghStates getCurrentState() {
		return this.currentState;
	}

	@Override
	public BlockingQueue<Pair<Float, Date>> getUmidityValuesList() {
		return this.umidityValuesList;
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
