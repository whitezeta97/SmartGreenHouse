package afms;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import events.ControllerMsgEvent;
import events.EdgeMsgEvent;
import observable.serverdataservice.ObservableSghDataServiceImpl;
import observables.msgservice.ObservableMsgService;
import observables.timer.ObservableTimer;

import utilities.Pair;
import utilities.SghStates;

public class SmartGreenHouseAFSM implements SghAfsm {

	private static float UMIN = 30;
	private static float U_MED = 20;
	private static float U_LOW = 10;
	private static float DELTAU = 5;
	private static int T_MAX = 5000;

	private static final int LIST_SIZE = 100;

	private boolean manualMode;
	private boolean isWatering;
	private SghStates currentState;
	private List<Pair<Float, Date>> umidityValuesList;
	private List<Pair<Long, Date>> wateringsList;
	private List<Date> warningsList;

	private long wateringStartedTime;
	private long wateringStoppedTime;

	private ObservableMsgService msgService;
	private ObservableTimer timer;
	private ObservableSghDataServiceImpl serverDataService;

	public SmartGreenHouseAFSM(ObservableTimer timer, ObservableMsgService msgService,
			ObservableSghDataServiceImpl edgeDataService) {

		this.timer = timer;
		this.msgService = msgService;
		this.serverDataService = edgeDataService;

		this.umidityValuesList = new LinkedList<>();
		this.wateringsList = new LinkedList<>();
		this.warningsList = new LinkedList<>();

		this.currentState = SghStates.PUMP_OFF;

		System.out.println("Server started at: " + new Date());
		System.out.println("Server state: " + this.currentState);

		this.serverDataService.setCurrentState(this.currentState);
		this.serverDataService.setManualMode(this.manualMode);
		this.serverDataService.setWatering(this.isWatering);
		this.serverDataService.setUmidityValuesList(this.umidityValuesList);
		this.serverDataService.setWarningsList(this.warningsList);
		this.serverDataService.setWateringsList(this.wateringsList);

	}

	private void manageUmidity(float umidity) {

		if (this.currentState.equals(SghStates.PUMP_OFF) && umidity < UMIN) {
			this.timer.start(T_MAX);
			this.isWatering = true;
			this.wateringStartedTime = System.currentTimeMillis();

			this.serverDataService.setWatering(this.isWatering);

			System.out.println("SERVER: Timer STARTED");
		}

		if (umidity >= U_MED && umidity <= UMIN) {
			this.currentState = SghStates.P_MIN;

		} else if (umidity >= U_LOW && umidity < U_MED) {
			this.currentState = SghStates.P_MED;

		} else if (umidity < U_LOW) {
			this.currentState = SghStates.P_MAX;

		} else if (!this.currentState.equals(SghStates.PUMP_OFF) && umidity >= UMIN + DELTAU) {
			this.timer.stop();
			this.isWatering = false;
			System.out.println("SERVER: Timer STOPPED");
			this.currentState = SghStates.PUMP_OFF;

			this.wateringStoppedTime = System.currentTimeMillis();

			long wateringDuration = this.wateringStoppedTime - this.wateringStartedTime;
			this.wateringsList.add(new Pair<>(wateringDuration, new Date()));

			this.serverDataService.setWatering(this.isWatering);
			this.serverDataService.setWateringsList(this.wateringsList);
		}

		if (this.umidityValuesList.size() > SmartGreenHouseAFSM.LIST_SIZE) {
			this.umidityValuesList.remove(0);
		}
		if (this.wateringsList.size() > SmartGreenHouseAFSM.LIST_SIZE) {
			this.wateringsList.remove(0);
		}
		if (this.warningsList.size() > SmartGreenHouseAFSM.LIST_SIZE) {
			this.warningsList.remove(0);
		}

		this.msgService.sendMsg(this.currentState.toString());

		this.serverDataService.setCurrentState(this.currentState);
		this.serverDataService.setUmidityValuesList(this.umidityValuesList);

		System.out.println("Server sent message: " + this.currentState);
		System.out.println("Server sent message to Client: " + this.currentState + " " + new Date());
	}

	@Override
	public void manageControllerMsgEvent(final ControllerMsgEvent ev) {

		String msg = ((ControllerMsgEvent) ev).getMsg();
		System.out.println("SERVER: Received: " + msg);

		if (msg.equals("manualmodeon")) {
			this.manualMode = true;
			this.timer.stop();

			this.serverDataService.setManualMode(this.manualMode);

			System.out.println("SERVER: Manual Mode ON");

		} else if (msg.equals("manualmodeoff")) {
			this.manualMode = false;
			this.currentState = SghStates.PUMP_OFF;
			this.msgService.sendMsg(this.currentState.toString());

			this.serverDataService.setManualMode(this.manualMode);
			this.serverDataService.setCurrentState(this.currentState);

			System.out.println("SERVER: Manual Mode OFF");
			System.out.println("Server status changed in: " + this.currentState);
		}

	}

	@Override
	public void manageTimerTickEvent() {
		this.timer.stop();

		if (this.manualMode) {
			return;
		}

		this.isWatering = false;
		this.currentState = SghStates.PUMP_OFF;
		this.warningsList.add(new Date());
		this.wateringStoppedTime = System.currentTimeMillis();
		this.wateringsList.add(new Pair<Long, Date>(this.wateringStoppedTime - this.wateringStartedTime, new Date()));

		this.msgService.sendMsg(this.currentState.toString());

		this.serverDataService.setWarningsList(this.warningsList);
		this.serverDataService.setWatering(this.isWatering);
		this.serverDataService.setWateringsList(this.wateringsList);

		System.out.println("WARNING: Watering time exceeded!");
		System.out.println("Server status changed in: " + this.currentState);

	}

	@Override
	public void manageEdgeMsgEvent(EdgeMsgEvent ev) {
		if (this.manualMode) {
			return;
		}

		float umidity = ((EdgeMsgEvent) ev).getMsg();
		System.out.println("SERVER: Received: " + umidity);

		if (umidity >= 0 && umidity <= 100) {
			this.umidityValuesList.add(new Pair<>(umidity, new Date()));
			this.manageUmidity(umidity);
		} else {
			System.out.println("SERVER: Received wrong umidty value");
		}

	}

	@Override
	public synchronized boolean isManualMode() {
		return this.manualMode;
	}
}
