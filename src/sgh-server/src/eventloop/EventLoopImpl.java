package eventloop;

import afms.SghAfsm;
import afms.SmartGreenHouseAFSM;
import events.Event;
import io.vertx.core.Vertx;
import observable.serverdataservice.ObservableDataService;
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

		Vertx vertx = Vertx.vertx();
		ObservableDataService service = new ObservableDataService(80);
		service.addObserver(this);
		vertx.deployVerticle(service);

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
