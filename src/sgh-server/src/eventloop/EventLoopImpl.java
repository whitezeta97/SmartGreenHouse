package eventloop;

import afms.SghAfsm;
import afms.SmartGreenHouseAFSM;
import events.ControllerMsgEvent;
import events.EdgeMsgEvent;
import events.Event;
import events.TickEvent;
import io.vertx.core.Vertx;
import observable.serverdataservice.ObservableSghDataServiceImpl;
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
		ObservableSghDataServiceImpl serverDataService = new ObservableSghDataServiceImpl(80);
		serverDataService.addObserver(this);
		vertx.deployVerticle(serverDataService);

		this.asincFiniteStateMachine = new SmartGreenHouseAFSM(timer, msgService, serverDataService);

	}

	@Override
	protected void processEvent(Event ev) {
		try {
			if (ev instanceof ControllerMsgEvent) {
				this.asincFiniteStateMachine.manageControllerMsgEvent((ControllerMsgEvent) ev);
			} else if (ev instanceof EdgeMsgEvent) {
				this.asincFiniteStateMachine.manageEdgeMsgEvent((EdgeMsgEvent) ev);
			} else if (ev instanceof TickEvent) {
				this.asincFiniteStateMachine.manageTimerTickEvent();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public boolean notifyEvent(final Event ev) {

		if (this.eventQueue.remainingCapacity() == 0) {
			this.eventQueue.remove();
		}

		return this.eventQueue.offer(ev);

	}

}
