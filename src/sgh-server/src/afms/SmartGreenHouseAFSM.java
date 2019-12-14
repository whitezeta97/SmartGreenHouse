package afms;

import java.util.Date;

import events.ControllerMsgEvent;
import events.EdgeMsgEvent;
import observables.msgservice.ObservableMsgService;
import observables.timer.ObservableTimer;
import serverdata.ServerData;
import utilities.SghStates;

public class SmartGreenHouseAFSM implements SghAfsm {

	private static float UMIN = 30;
	private static float U_MED = 20;
	private static float U_LOW = 10;
	private static float DELTAU = 5;
	private static int T_MAX = 5000;

	private long wateringStartedTime;
	private long wateringStoppedTime;

	private ObservableMsgService msgService;
	private ObservableTimer timer;

	private ServerData serverData;

	public SmartGreenHouseAFSM(final ObservableTimer timer, final ObservableMsgService msgService,
			final ServerData serverData) {

		this.timer = timer;
		this.msgService = msgService;
		this.serverData = serverData;

		System.out.println("Server started at: " + new Date());

		this.serverData.setCurrentState(SghStates.PUMP_OFF);

		System.out.println("Server state: " + this.serverData.getCurrentState());

	}

	private void manageUmidity(float umidity) {

		if (this.serverData.getCurrentState().equals(SghStates.PUMP_OFF) && umidity < UMIN) {
			this.timer.start(T_MAX);
			this.serverData.setWatering(true);
			this.wateringStartedTime = System.currentTimeMillis();
			System.out.println("SERVER: Watering STARTED");
		}

		if (umidity >= U_MED && umidity <= UMIN) {
			this.serverData.setCurrentState(SghStates.P_MIN);

		} else if (umidity >= U_LOW && umidity < U_MED) {
			this.serverData.setCurrentState(SghStates.P_MED);

		} else if (umidity < U_LOW) {
			this.serverData.setCurrentState(SghStates.P_MAX);

		} else if (!this.serverData.getCurrentState().equals(SghStates.PUMP_OFF) && umidity >= UMIN + DELTAU) {
			this.timer.stop();
			this.serverData.setWatering(false);

			System.out.println("SERVER: Watering STOPPED");

			this.wateringStoppedTime = System.currentTimeMillis();

			this.serverData.setCurrentState(SghStates.PUMP_OFF);
			this.serverData.addWateringsListElement(this.wateringStoppedTime - this.wateringStartedTime, new Date());
		}

		this.msgService.sendMsg(this.serverData.getCurrentState().toString());

		System.out.println("Server sent message: " + this.serverData.getCurrentState());
		System.out.println("Server sent message to Client: " + this.serverData.getCurrentState() + " " + new Date());
	}

	@Override
	public void manageControllerMsgEvent(final ControllerMsgEvent ev) {

		String msg = ((ControllerMsgEvent) ev).getMsg();
		System.out.println("SERVER: Received: " + msg);

		if (msg.equals("manualmodeon")) {
			this.serverData.setManualMode(true);
			this.timer.stop();

			System.out.println("SERVER: Manual Mode ON");

		} else if (msg.equals("manualmodeoff")) {
			this.serverData.setManualMode(false);
			this.serverData.setCurrentState(SghStates.PUMP_OFF);

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
		this.serverData.setCurrentState(SghStates.PUMP_OFF);
		this.serverData.addWarningsListElement(new Date());
		this.serverData.addWateringsListElement(this.wateringStoppedTime - this.wateringStartedTime, new Date());

		this.msgService.sendMsg(this.serverData.getCurrentState().toString());

		System.out.println("WARNING: Watering time exceeded!");
		System.out.println("Server status changed in: " + this.serverData.getCurrentState());

	}

	@Override
	public void manageEdgeMsgEvent(EdgeMsgEvent ev) {
		if (this.serverData.isManualMode()) {
			return;
		}

		float umidity = ((EdgeMsgEvent) ev).getMsg();
		System.out.println("SERVER: Received: " + umidity);

		if (umidity >= 0 && umidity <= 100) {
			this.serverData.addUmidityValuesListElement(umidity, new Date());
			this.manageUmidity(umidity);
		} else {
			System.out.println("SERVER: Received wrong umidty value");
		}

	}

}
