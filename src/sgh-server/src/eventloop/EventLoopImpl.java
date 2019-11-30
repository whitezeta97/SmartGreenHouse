package eventloop;

import events.Event;
import events.MsgEvent;
import events.TickEvent;
import observables.msgservice.ObservableMsgService;
import observables.msgservice.ObservableMsgServiceImpl;
import observables.timer.ObservableTimer;
import observables.timer.ObservableTimerImpl;

public class EventLoopImpl extends AbstractEventLoop {

	private ObservableMsgService msgService;
	private ObservableTimer timer;

	private enum State {
		POMPE_OFF, POMPE_ON, PMIN, PMED, PMAX
	};

	private State currentState;

	public EventLoopImpl(String port, int rate) {

		this.msgService = new ObservableMsgServiceImpl(port, rate);
		this.msgService.init();
		this.timer = new ObservableTimerImpl();
		this.timer.addObserver(this);
		this.msgService.addObserver(this);
		this.currentState = State.POMPE_OFF;
		this.msgService.sendMsg("ping");

	}

	@Override
	protected void processEvent(Event ev) {
		switch (this.currentState) {
		case POMPE_OFF:
			try {
				if (ev instanceof MsgEvent) {
					String msg = ((MsgEvent) ev).getMsg();
					System.out.println("Received: " + msg);
					timer.start(500);
					currentState = State.POMPE_OFF;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			break;
		case POMPE_ON:
			if (ev instanceof TickEvent) {
				timer.stop();
				msgService.sendMsg("ping");
				currentState = State.POMPE_OFF;
			}
			break;
		default:
			break;
		}

	}

}
