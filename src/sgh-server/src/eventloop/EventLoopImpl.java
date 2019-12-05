package eventloop;

import afms.SghAfsm;
import afms.SmartGreenHouseAFSM;
import clientcommunication.ClientCommunication;
import clientcommunication.ClientCommunicationImpl;
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
		ObservableDataService edgeDataService = new ObservableDataService(80);
		edgeDataService.addObserver(this);
		vertx.deployVerticle(edgeDataService);

		ClientCommunication clientComm = new ClientCommunicationImpl(80, "601a1d31.ngrok.io");

		this.asincFiniteStateMachine = new SmartGreenHouseAFSM(timer, msgService, clientComm);

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
		else {
			if (this.eventQueue.remainingCapacity() == 0) {
				this.eventQueue.remove();
			}
			return this.eventQueue.offer(ev);
		}

	}

}
