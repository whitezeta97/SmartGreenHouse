package eventloop;

import events.Event;
import events.MsgEvent;
import events.TickEvent;
import observables.msgservice.ObservableMsgService;
import observables.msgservice.ObservableMsgServiceImpl;
import observables.timer.ObservableTimer;
import observables.timer.ObservableTimerImpl;

public class EventLoopImpl extends AbstractEventLoop {

	private static float UMIN = 30;
	private static float U_MED = 20;
	private static float U_LOW = 10;
	private static float DELTAU = 5;
	private static int T_MAX = 5000;

	private boolean manualMode;
	private float umidity;

	private ObservableMsgService msgService;
	private ObservableTimer timer;

	private enum State {
		POMPE_OFF, PMIN, PMED, PMAX
	};

	private State currentState;

	public EventLoopImpl(String port, int rate) {

		this.msgService = new ObservableMsgServiceImpl(port, rate);
		this.msgService.init();
		this.msgService.addObserver(this);

		this.timer = new ObservableTimerImpl();
		this.timer.addObserver(this);

		this.currentState = State.POMPE_OFF;
		System.out.println("Server started at: " + new java.util.Date());

	}

	@Override
	protected void processEvent(Event ev) {

		if (manualMode)
			return;

		switch (this.currentState) {
		case POMPE_OFF:
			this.managePompeOffState(ev);
			break;

		case PMIN:
		case PMED:
		case PMAX:
			this.managePompeOnState(ev);
			break;

		default:

			break;
		}

	}

	@Override
	public boolean notifyEvent(final Event ev) {
		if (this.manualMode)
			return false;
		else
			return this.eventQueue.offer(ev);
	}

	private void changeFlow() {
		if (this.umidity >= U_MED && this.umidity <= U_LOW) {
			this.msgService.sendMsg("pmin");
			this.currentState = State.PMIN;

		} else if (this.umidity >= UMIN && this.umidity < U_MED) {
			this.msgService.sendMsg("pmed");
			this.currentState = State.PMED;

		} else if (this.umidity < UMIN) {
			this.msgService.sendMsg("pmax");
			this.currentState = State.PMAX;
		}

		//TODO: messaggiare il Client
		System.out.println("Server sent message: " + this.currentState);
		System.out.println("Server sent message to Client: " + this.currentState + " " + new java.util.Date());
	}

	private void managePompeOffState(Event ev) {
		try {
			if (ev instanceof MsgEvent) {
				String msg = ((MsgEvent) ev).getMsg();
				System.out.println("Received: " + msg);

				if (msg.equals("manualmodeon")) {
					this.manualMode = true;
					this.currentState = State.POMPE_OFF;
					//TODO: messaggiare il Client
					System.out.println("Manual Mode ON");
					System.out.println("Server status changed in: " + this.currentState);

				} else if (msg.equals("manualmodeoff")) {
					this.manualMode = false;
					//TODO: messaggiare il Client
					System.out.println("Manual Mode OFF");

				} else if (msg.matches("-?\\d+(\\.\\d+)?")) {
					this.umidity = Integer.parseInt(msg);
					this.changeFlow();
					this.timer.start(T_MAX);

					System.out.println("Received Umidity Value = " + msg);
					System.out.println("Server status changed in: " + this.currentState);
					System.out.println("Timer Started");
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void managePompeOnState(Event ev) {
		if (ev instanceof TickEvent) {
			this.timer.stop();
			this.msgService.sendMsg(State.POMPE_OFF.toString());
			this.currentState = State.POMPE_OFF;
			//TODO: messaggiare il Client
			System.out.println("WARNING: Watering time exceeded!");

		} else if (ev instanceof MsgEvent) {
			String msg = ((MsgEvent) ev).getMsg();
			System.out.println("Received: " + msg);

			if (msg.matches("-?\\d+(\\.\\d+)?")) {
				this.umidity = Integer.parseInt(msg);

				if (this.umidity >= UMIN + DELTAU) {
					this.timer.stop();
					this.msgService.sendMsg(State.POMPE_OFF.toString());
					this.currentState = State.POMPE_OFF;
					//TODO: messaggiare il Client

					System.out.println("Received Umidity Value = " + msg);
					System.out.println("Server status changed in: " + this.currentState);
					System.out.println("Server sent message: " + this.currentState);
					System.out.println(
							"Server sent message to Client: " + this.currentState + " " + new java.util.Date());
					System.out.println("Timer Stopped");
				} else {
					this.changeFlow();
				}
			}
		}
	}

}
