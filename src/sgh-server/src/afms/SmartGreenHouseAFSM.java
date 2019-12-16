package afms;

import java.util.Date;

import events.ControllerMsgEvent;
import events.EdgeMsgEvent;
import observables.msgservice.ObservableMsgService;
import observables.timer.ObservableTimer;
import serverstatusdata.ServerStatusData;
import utilities.SghPumpStates;

/**
 * 
 * Implements the SGH Server Asynchronous Finite State Machine.
 *
 */
public class SmartGreenHouseAFSM implements SghAfsm {
	private static final String MANUALMODE_OFF = "manualmodeoff";
	private static final String MANUALMODE_ON = "manualmodeon";
	private static final float UMIN = 30;
	private static final float U_MED = 20;
	private static final float U_LOW = 10;
	private static final float DELTAU = 5;
	private static final int T_MAX = 5000;

	private long wateringStartedTime;
	private long wateringStoppedTime;

	private ObservableMsgService msgService;
	private ObservableTimer timer;

	private ServerStatusData serverData;

	/**
	 * 
	 * @param timer
	 *            the Observable Timer that generates tick events.
	 * @param msgService
	 *            the Message Service to send/receive message events to/from SGH
	 *            Controller.
	 * @param serverData
	 *            the Server Status data.
	 */
	public SmartGreenHouseAFSM(final ObservableTimer timer, final ObservableMsgService msgService,
			final ServerStatusData serverData) {

		this.timer = timer;
		this.msgService = msgService;
		this.serverData = serverData;

		System.out.println("Server started at: " + new Date());

		this.serverData.setCurrentState(SghPumpStates.PUMP_OFF);

		System.out.println("Server state: " + this.serverData.getCurrentState());

	}

	/* Manages the received humidity. */
	private void manageHumidity(final float humidity) {

		if (this.serverData.getCurrentState().equals(SghPumpStates.PUMP_OFF) && humidity < UMIN) {
			this.timer.start(T_MAX);
			this.serverData.setWatering(true);
			this.wateringStartedTime = System.currentTimeMillis();
			System.out.println("SERVER: Watering STARTED");
		}

		if (humidity >= U_MED && humidity <= UMIN) {
			this.serverData.setCurrentState(SghPumpStates.P_MIN);

		} else if (humidity >= U_LOW && humidity < U_MED) {
			this.serverData.setCurrentState(SghPumpStates.P_MED);

		} else if (humidity < U_LOW) {
			this.serverData.setCurrentState(SghPumpStates.P_MAX);

		} else if (!this.serverData.getCurrentState().equals(SghPumpStates.PUMP_OFF) && humidity >= UMIN + DELTAU) {
			this.timer.stop();
			this.serverData.setWatering(false);

			System.out.println("SERVER: Watering STOPPED");

			this.wateringStoppedTime = System.currentTimeMillis();

			this.serverData.setCurrentState(SghPumpStates.PUMP_OFF);
			this.serverData.addWateringsListElement(this.wateringStoppedTime - this.wateringStartedTime, new Date());
		}

		this.msgService.sendMsg(this.serverData.getCurrentState().toString());

		System.out.println("Server sent message: " + this.serverData.getCurrentState());
		System.out.println("Server sent message to Client: " + this.serverData.getCurrentState() + " " + new Date());
	}

	@Override
	public void manageControllerMsgEvent(final ControllerMsgEvent ev) {

		final String msg = ((ControllerMsgEvent) ev).getMsg();
		System.out.println("SERVER: Received: " + msg);

		if (msg.equals(SmartGreenHouseAFSM.MANUALMODE_ON)) {
			this.serverData.setManualMode(true);
			this.timer.stop();

			System.out.println("SERVER: Manual Mode ON");

		} else if (msg.equals(SmartGreenHouseAFSM.MANUALMODE_OFF)) {
			this.serverData.setManualMode(false);
			this.serverData.setCurrentState(SghPumpStates.PUMP_OFF);

			this.msgService.sendMsg(this.serverData.getCurrentState().toString());

			System.out.println("SERVER: Manual Mode OFF");
			System.out.println("Server status changed in: " + this.serverData.getCurrentState());
		}

	}

	@Override
	public void manageTimerTickEvent() {
		this.timer.stop();
		this.wateringStoppedTime = System.currentTimeMillis();

		if (this.serverData.isManualMode()) {
			return;
		}

		this.serverData.setWatering(false);
		this.serverData.setCurrentState(SghPumpStates.PUMP_OFF);
		this.serverData.addWarningsListElement(new Date());
		this.serverData.addWateringsListElement(this.wateringStoppedTime - this.wateringStartedTime, new Date());

		this.msgService.sendMsg(this.serverData.getCurrentState().toString());

		System.out.println("WARNING: Watering time exceeded!");
		System.out.println("Server status changed in: " + this.serverData.getCurrentState());

	}

	@Override
	public void manageEdgeMsgEvent(final EdgeMsgEvent ev) {
		if (this.serverData.isManualMode()) {
			return;
		}

		final float humidity = ((EdgeMsgEvent) ev).getMsg();
		System.out.println("SERVER: Received: " + humidity);

		if (humidity >= 0 && humidity <= 100) {
			this.serverData.addHumidityValuesListElement(humidity, new Date());
			this.msgService.sendMsg(String.valueOf(humidity));
			this.manageHumidity(humidity);
		} else {
			System.out.println("SERVER: Received wrong humidty value");
		}

	}

}
