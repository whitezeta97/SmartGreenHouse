package eventloop;

import afms.SghAfsm;
import afms.SmartGreenHouseAFSM;
import events.Event;
import observables.msgservice.ObservableMsgService;
import observables.msgservice.ObservableMsgServiceImpl;
import observables.timer.ObservableTimer;
import observables.timer.ObservableTimerImpl;

public class EventLoopImpl extends AbstractEventLoop {

	private SghAfsm asincFiniteStateMachine;

	public EventLoopImpl(String port, int rate) {

		ObservableMsgService msgService = new ObservableMsgServiceImpl(port, rate);
		msgService.addObserver(this);
		msgService.init();

		ObservableTimer timer = new ObservableTimerImpl();
		timer.addObserver(this);

		this.asincFiniteStateMachine = new SmartGreenHouseAFSM(timer, msgService);

	}

	@Override
	protected void processEvent(Event ev) {
		if (this.asincFiniteStateMachine.isManualMode())
			return;

		this.asincFiniteStateMachine.performAction(ev);
	}

	@Override
	public boolean notifyEvent(final Event ev) {
		if (this.asincFiniteStateMachine.isManualMode())
			return false;
		else
			return this.eventQueue.offer(ev);
	}

}
