package eventloop;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import events.Event;
import observables.Observable;

/**
 * 
 * Implements an abstract Event Loop.
 *
 */
public abstract class AbstractEventLoop extends Thread implements EventLoopObserver {

	private static final int defaultEventQueueSize = 50;
	protected BlockingQueue<Event> eventQueue;

	/**
	 * 
	 * @param size
	 *            the event queue size.
	 */
	protected AbstractEventLoop(final int size) {
		this.eventQueue = new ArrayBlockingQueue<Event>(size);
	}

	protected AbstractEventLoop() {
		this(defaultEventQueueSize);
	}

	/**
	 * Processes the received event.
	 * 
	 * @param ev
	 *            the received event.
	 */
	abstract protected void processEvent(final Event ev);

	/**
	 * Start to observe the observable.
	 * 
	 * @param object
	 *            the observable that has to be observed.
	 */
	protected void startObserving(final Observable object) {
		object.addObserver(this);
	}

	/**
	 * Stop to observe the observable.
	 * 
	 * @param object
	 *            the observable that has no longer to be observed.
	 */
	protected void stopObserving(final Observable object) {
		object.removeObserver(this);
	}

	/**
	 * Wait for the next event.
	 * 
	 * @return the event received.
	 * @throws InterruptedException
	 */
	protected Event waitForNextEvent() throws InterruptedException {
		return this.eventQueue.take();
	}

	/**
	 * Get the next event from the event queue if available.
	 * 
	 * @return the next available event from the event queue.
	 * @throws InterruptedException
	 */
	protected Event pickNextEventIfAvail() throws InterruptedException {
		return this.eventQueue.poll();
	}

	@Override
	public boolean notifyEvent(final Event ev) {
		return this.eventQueue.offer(ev);
	}

	@Override
	public void run() {

		while (true) {
			try {
				final Event ev = this.waitForNextEvent();
				this.processEvent(ev);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

}
