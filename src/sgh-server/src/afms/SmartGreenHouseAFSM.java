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
	private List<Pair<Float, Date>> umidityValuesList = new LinkedList<>();
	private List<Pair<Long, Date>> wateringsList = new LinkedList<>();
	private List<Date> warningsList = new LinkedList<>();

	private long wateringStartedTime;
	private long wateringStoppedTime;

	private ObservableMsgService msgService;
	private ObservableTimer timer;
	private ObservableSghDataServiceImpl edgeDataService;

	public SmartGreenHouseAFSM(ObservableTimer timer, ObservableMsgService msgService,
			ObservableSghDataServiceImpl edgeDataService) {

		this.timer = timer;
		this.msgService = msgService;
		this.edgeDataService = edgeDataService;

		this.currentState = SghStates.PUMP_OFF;

		System.out.println("Server started at: " + new Date());
		System.out.println("Server state: " + this.currentState);

		this.edgeDataService.setCurrentState(this.currentState);
		this.edgeDataService.setManualMode(this.manualMode);
		this.edgeDataService.setWatering(this.isWatering);
		this.edgeDataService.setUmidityValuesList(this.umidityValuesList);
		this.edgeDataService.setWarningsList(this.warningsList);
		this.edgeDataService.setWateringsList(this.wateringsList);

	}

	private void manageUmidity(float umidity) {

		if (this.currentState.equals(SghStates.PUMP_OFF) && umidity < UMIN) {
			this.timer.start(T_MAX);
			this.wateringStartedTime = System.currentTimeMillis();

			this.edgeDataService.setWatering(this.manualMode);

			System.out.println("Timer STARTED");
		}

		if (umidity >= U_MED && umidity <= UMIN) {
			this.currentState = SghStates.P_MIN;

		} else if (umidity >= U_LOW && umidity < U_MED) {
			this.currentState = SghStates.P_MED;

		} else if (umidity < U_LOW) {
			this.currentState = SghStates.P_MAX;

		} else if (!this.currentState.equals(SghStates.PUMP_OFF) && umidity >= UMIN + DELTAU) {
			this.timer.stop();
			System.out.println("Timer STOPPED");
			this.currentState = SghStates.PUMP_OFF;

			this.wateringStoppedTime = System.currentTimeMillis();

			long wateringDuration = this.wateringStoppedTime - this.wateringStartedTime;
			this.wateringsList.add(new Pair<>(wateringDuration, new Date()));

			this.edgeDataService.setWatering(this.manualMode);
			this.edgeDataService.setWateringsList(this.wateringsList);
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

		this.edgeDataService.setCurrentState(this.currentState);
		this.edgeDataService.setUmidityValuesList(this.umidityValuesList);

		System.out.println("Server sent message: " + this.currentState);
		System.out.println("Server sent message to Client: " + this.currentState + " " + new Date());
	}

	@Override
	public synchronized boolean isManualMode() {
		return this.manualMode;
	}

	@Override
	public void manageControllerMsgEvent(final ControllerMsgEvent ev) {
		if (this.manualMode) {
			return;
		}

		String msg = ((ControllerMsgEvent) ev).getMsg();
		System.out.println("Received: " + msg);

		if (msg.equals("manualmodeon")) {
			this.manualMode = true;
			this.timer.stop();

			System.out.println("Manual Mode ON");

		} else if (msg.equals("manualmodeoff")) {
			this.manualMode = false;
			this.currentState = SghStates.PUMP_OFF;
			this.msgService.sendMsg(this.currentState.toString());

			System.out.println("Manual Mode OFF");
			System.out.println("Server status changed in: " + this.currentState);
		}

		this.edgeDataService.setManualMode(this.manualMode);

	}

	@Override
	public void manageTimerTickEvent() {
		if (this.manualMode) {
			return;
		}

		this.timer.stop();
		this.currentState = SghStates.PUMP_OFF;
		this.warningsList.add(new Date());

		this.msgService.sendMsg(this.currentState.toString());

		this.edgeDataService.setWarningsList(this.warningsList);

		System.out.println("WARNING: Watering time exceeded!");
		System.out.println("Server status changed in: " + this.currentState);

	}

	@Override
	public void manageEdgeMsgEvent(EdgeMsgEvent ev) {
		if (this.manualMode) {
			return;
		}

		float umidity = ((EdgeMsgEvent) ev).getMsg();
		System.out.println("Received: " + umidity);

		if (umidity >= 0 && umidity <= 100) {
			this.umidityValuesList.add(new Pair<>(umidity, new Date()));
			this.manageUmidity(umidity);
		} else {
			System.out.println("Received wrong umidty value");
		}

	}
}
