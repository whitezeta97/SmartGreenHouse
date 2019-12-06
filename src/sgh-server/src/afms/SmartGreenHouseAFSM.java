package afms;

import events.Event;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import events.ControllerMsgEvent;
import events.EdgeMsgEvent;
import events.TickEvent;

import observables.msgservice.ObservableMsgService;
import observables.timer.ObservableTimer;

import utilities.Pair;

public class SmartGreenHouseAFSM implements SghAfsm {

	private static float UMIN = 30;
	private static float U_MED = 20;
	private static float U_LOW = 10;
	private static float DELTAU = 5;
	private static int T_MAX = 5000;

	private static final int LIST_SIZE = 100;

	private boolean manualMode;

	private long wateringStartedTime;
	private long wateringStoppedTime;

	private enum State {
		PUMP_OFF, P_MIN, P_MED, P_MAX
	}

	private State currentState;

	private ObservableMsgService msgService;
	private ObservableTimer timer;

	private volatile List<Pair<Float, Date>> umidityValuesList = new LinkedList<>();

	private volatile List<Pair<Long, Date>> wateringsList = new LinkedList<>();

	private volatile List<Date> warningsList = new LinkedList<>();

	public SmartGreenHouseAFSM(ObservableTimer timer, ObservableMsgService msgServicm) {
		this.timer = timer;
		this.msgService = msgService;

		this.currentState = State.PUMP_OFF;

		// this.clientComm.sendSghState(this.currentState.toString());

		System.out.println("Server started at: " + new Date());
		System.out.println("Server state: " + this.currentState);

	}

	private void manageUmidity(float umidity) {

		if (this.currentState.equals(State.PUMP_OFF) && umidity < UMIN) {
			// this.clientComm.sendWateringOn();
			this.timer.start(T_MAX);
			this.wateringStartedTime = System.currentTimeMillis();

			System.out.println("Timer STARTED");

		}

		if (umidity >= U_MED && umidity <= UMIN) {
			this.currentState = State.P_MIN;

		} else if (umidity >= U_LOW && umidity < U_MED) {
			this.currentState = State.P_MED;

		} else if (umidity < U_LOW) {
			this.currentState = State.P_MAX;

		} else if (!this.currentState.equals(State.PUMP_OFF) && umidity >= UMIN + DELTAU) {
			this.timer.stop();
			System.out.println("Timer STOPPED");
			this.currentState = State.PUMP_OFF;

			this.wateringStoppedTime = System.currentTimeMillis();

			long wateringDuration = this.wateringStoppedTime - this.wateringStartedTime;
			this.wateringsList.add(new Pair<>(wateringDuration, new Date()));
			// this.clientComm.sendWateringOff(wateringDuration);

		}

		this.msgService.sendMsg(this.currentState.toString());

		// this.clientComm.sendSghState(this.currentState.toString());
		System.out.println("Server sent message: " + this.currentState);
		System.out.println("Server sent message to Client: " + this.currentState + " " + new Date());
	}

	@Override
	public void performAction(final Event ev) {
		try {
			if (ev instanceof ControllerMsgEvent) {

			} else if (ev instanceof EdgeMsgEvent && !this.manualMode) {
				float umidity = ((EdgeMsgEvent) ev).getMsg();
				System.out.println("Received: " + umidity);

				if (umidity >= 0 && umidity <= 100) {
					this.umidityValuesList.add(new Pair<>(umidity, new Date()));
					// this.clientComm.sendUmidity(umidity);
					this.manageUmidity(umidity);
				} else {
					System.out.println("Received wrong umidty value");
				}

			} else if (ev instanceof TickEvent && !this.manualMode) {
				this.timer.stop();
				this.currentState = State.PUMP_OFF;
				this.warningsList.add(new Date());

				this.msgService.sendMsg(this.currentState.toString());

				// this.clientComm.sendWarning("WARNING: Watering time exceeded!",
				// this.currentState.toString());

				System.out.println("WARNING: Watering time exceeded!");
				System.out.println("Server status changed in: " + this.currentState);
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

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public synchronized boolean isManualMode() {
		return this.manualMode;
	}

	@Override
	public void setManualMode(final String msg) {
		if (msg.equals("manualmodeon")) {
			this.manualMode = true;
			this.timer.stop();

			System.out.println("Manual Mode ON");

		} else if (msg.equals("manualmodeoff")) {
			this.manualMode = false;
			this.currentState = State.PUMP_OFF;
			this.msgService.sendMsg(this.currentState.toString());

			System.out.println("Manual Mode OFF");
			System.out.println("Server status changed in: " + this.currentState);

		}

	}

	@Override
	public void manageControllerMsgEvent(ControllerMsgEvent ev) {
		String msg = ((ControllerMsgEvent) ev).getMsg();
		System.out.println("Received: " + msg);

	}

	@Override
	public void manageTimerTickEvent(TickEvent ev) {
		// TODO Auto-generated method stub

	}
}
