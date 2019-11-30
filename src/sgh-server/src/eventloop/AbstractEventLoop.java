package eventloop;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import events.Event;
import observables.Observable;

public abstract class AbstractEventLoop extends Thread implements EventLoopObserver {

	private static final int defaultEventQueueSize = 50;
	protected BlockingQueue<Event> eventQueue;

	protected AbstractEventLoop(final int size) {
		this.eventQueue = new ArrayBlockingQueue<Event>(size);
	}

	protected AbstractEventLoop() {
		this(defaultEventQueueSize);
	}

	abstract protected void processEvent(final Event ev);

	public void run() {

		while (true) {
			try {
				Event ev = this.waitForNextEvent();
				this.processEvent(ev);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public boolean notifyEvent(final Event ev) {
		return this.eventQueue.offer(ev);
	}

	protected void startObserving(final Observable object) {
		object.addObserver(this);
	}

	protected void stopObserving(final Observable object) {
		object.removeObserver(this);
	}

	protected Event waitForNextEvent() throws InterruptedException {
		return this.eventQueue.take();
	}

	protected Event pickNextEventIfAvail() throws InterruptedException {
		return this.eventQueue.poll();
	}

}
