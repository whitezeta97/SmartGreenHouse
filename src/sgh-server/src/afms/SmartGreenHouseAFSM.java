package afms;

import events.Event;
import events.MsgEvent;
import events.TickEvent;
import observables.msgservice.ObservableMsgService;
import observables.timer.ObservableTimer;

public class SmartGreenHouseAFSM implements SghAfsm {

	private static float UMIN = 30;
	private static float U_MED = 20;
	private static float U_LOW = 10;
	private static float DELTAU = 5;
	private static int T_MAX = 5000;

	private boolean manualMode;

	private enum State {
		PUMP_OFF, P_MIN, P_MED, P_MAX
	}

	private State currentState;

	private ObservableMsgService msgService;
	private ObservableTimer timer;

	public SmartGreenHouseAFSM(ObservableTimer timer, ObservableMsgService msgService) {
		this.timer = timer;
		this.msgService = msgService;

		this.currentState = State.PUMP_OFF;

		// TODO: messaggiare il Client con Stato serra
		System.out.println("Server started at: " + new java.util.Date());
		System.out.println("Server state: " + this.currentState);
	}

	private void manageUmidity(float umidity) {

		if (this.currentState.equals(State.PUMP_OFF) && umidity < UMIN) {
			this.timer.start(T_MAX);
			System.out.println("Timer STARTED");

			// TODO: messaggiare il Client
			System.out.println("Server sent message: " + this.currentState);
		}

		if (umidity >= U_MED && umidity <= U_LOW) {
			this.currentState = State.P_MIN;

		} else if (umidity >= UMIN && umidity < U_MED) {
			this.currentState = State.P_MED;

		} else if (umidity < U_LOW) {
			this.currentState = State.P_MAX;

		} else if (!this.currentState.equals(State.PUMP_OFF) && umidity >= UMIN + DELTAU) {
			this.timer.stop();
			System.out.println("Timer STOPPED");
			this.currentState = State.PUMP_OFF;

		}

		this.msgService.sendMsg(this.currentState.toString());

		// TODO: messaggiare il Client
		System.out.println("Server sent message: " + this.currentState);
		System.out.println("Server sent message to Client: " + this.currentState + " " + new java.util.Date());
	}

	@Override
	public boolean isManualMode() {
		return this.manualMode;
	}

	@Override
	public void performAction(final Event ev) {
		try {
			if (ev instanceof MsgEvent) {
				String msg = ((MsgEvent) ev).getMsg();
				System.out.println("Received: " + msg);

				if (msg.equals("manualmodeon")) {
					this.manualMode = true;
					this.timer.stop();
					// TODO: messaggiare il Client

					System.out.println("Manual Mode ON");

				} else if (msg.equals("manualmodeoff")) {
					this.manualMode = false;
					this.currentState = State.PUMP_OFF;
					this.msgService.sendMsg(this.currentState.toString());
					// TODO: messaggiare il Client

					System.out.println("Manual Mode OFF");
					System.out.println("Server status changed in: " + this.currentState);

				} else if (msg.matches("-?\\d+(\\.\\d+)?")) {
					this.manageUmidity(Integer.parseInt(msg));
				}

			} else if (ev instanceof TickEvent) {
				this.currentState = State.PUMP_OFF;
				this.msgService.sendMsg(this.currentState.toString());
				// TODO: messaggiare il Client

				System.out.println("WARNING: Watering time exceeded!");
				System.out.println("Server status changed in: " + this.currentState);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
