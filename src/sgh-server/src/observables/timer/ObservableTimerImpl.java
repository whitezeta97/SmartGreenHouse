package observables.timer;

import java.util.concurrent.*;

import events.TickEventImpl;
import observables.AbstractObservable;

public class ObservableTimerImpl extends AbstractObservable implements ObservableTimer {

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> tickHandle;
	private Runnable tickTask;

	public ObservableTimerImpl() {

		this.tickTask = () -> {
			TickEventImpl ev = new TickEventImpl(System.currentTimeMillis());
			notifyEvent(ev);
		};
	}

	public synchronized void start(final long period) {
		this.tickHandle = this.scheduler.scheduleAtFixedRate(this.tickTask, 0, period, TimeUnit.MILLISECONDS);
	}

	public synchronized void stop() {

		if (this.tickHandle != null) {
			this.tickHandle.cancel(false);
			this.tickHandle = null;
		}

	}

	public synchronized void scheduleTick(final long deltat) {
		this.scheduler.schedule(this.tickTask, deltat, TimeUnit.MILLISECONDS);
	}
}
