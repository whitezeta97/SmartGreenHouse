package eventloop;

import afms.SghAfsm;
import afms.SmartGreenHouseAFSM;
import events.ControllerMsgEvent;
import events.EdgeMsgEvent;
import events.Event;
import events.TickEvent;
import io.vertx.core.Vertx;
import observables.msgservice.ObservableMsgService;
import observables.msgservice.ObservableMsgServiceImpl;
import observables.serverdataservice.ObservableSghDataServiceImpl;
import observables.timer.ObservableTimer;
import observables.timer.ObservableTimerImpl;
import serverstatusdata.ServerStatusData;
import serverstatusdata.ServerStatusDataImpl;

/**
 * 
 * Implements an Event Loop Observer.
 *
 */
public class EventLoopImpl extends AbstractEventLoop {

	private SghAfsm asincFiniteStateMachine;

	/**
	 * 
	 * @param port
	 *            the name of the serial port to communicate with SGH Controller.
	 * @param rate
	 *            the serial rate to communicate with SGH Controller.
	 */
	public EventLoopImpl(final String port, final int rate) {

		final ServerStatusData serverData = new ServerStatusDataImpl();

		final ObservableMsgService msgService = new ObservableMsgServiceImpl(port, rate);
		msgService.addObserver(this);
		msgService.init();

		ObservableTimer timer = new ObservableTimerImpl();
		timer.addObserver(this);

		final Vertx vertx = Vertx.vertx();

		final ObservableSghDataServiceImpl serverDataService = new ObservableSghDataServiceImpl(80, serverData);
		serverDataService.addObserver(this);
		vertx.deployVerticle(serverDataService);

		this.asincFiniteStateMachine = new SmartGreenHouseAFSM(timer, msgService, serverData);

	}

	@Override
	protected void processEvent(final Event ev) {
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
			this.eventQueue.poll();
		}

		return this.eventQueue.offer(ev);

	}

}
