package observables;

import observer.event.EventObserver;

public interface Observable {

	void addObserver(final EventObserver obs);

	void removeObserver(final EventObserver obs);
}
