package eventloop;

import afms.SghAfsm;
import afms.SmartGreenHouseAFSM;
import events.Event;
import io.vertx.core.Vertx;
import observable.serverdataservice.ObservableSghDataService;
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
		ObservableSghDataService edgeDataService = new ObservableSghDataService(80);
		edgeDataService.addObserver(this);
		vertx.deployVerticle(edgeDataService);

		this.asincFiniteStateMachine = new SmartGreenHouseAFSM(timer, msgService);

	}

	@Override
	protected void processEvent(Event ev) {
		this.asincFiniteStateMachine.performAction(ev);
	}

	@Override
	public boolean notifyEvent(final Event ev) {

		if (this.eventQueue.remainingCapacity() == 0) {
			this.eventQueue.remove();
		}

		return this.eventQueue.offer(ev);

	}

}
