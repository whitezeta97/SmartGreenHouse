package observables.serverdataservice;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import observer.event.EventObserver;
import serverstatusdata.ServerStatusData;
import utilities.MessageTypes;
import utilities.Pair;

import java.util.Date;
import java.util.LinkedList;

import events.EdgeMsgEvent;
import events.EdgeMsgEventImpl;
import events.Event;

/**
 * 
 * Implements Data Service as a vertx event-loop.
 *
 */
public class ObservableSghDataServiceImpl extends AbstractVerticle implements ObservableSghDataService {

	private int port;
	private ServerStatusData serverData;

	private LinkedList<EventObserver> observers;

	/**
	 * 
	 * @param port
	 *            the port from which the server listens for http requests.
	 * @param serverData
	 *            the SGH Server status data.
	 */
	public ObservableSghDataServiceImpl(final int port, final ServerStatusData serverData) {
		this.observers = new LinkedList<>();
		this.port = port;
		this.serverData = serverData;
	}

	/* Manages post requests, it generates an event. */
	private void handleAddNewData(RoutingContext routingContext) {
		final HttpServerResponse response = routingContext.response();

		final JsonObject res = routingContext.getBodyAsJson();

		if (res == null) {
			sendError(400, response);
		} else {
			final int value = res.getInteger("value");
			response.setStatusCode(200).end();
			final EdgeMsgEvent humidityEvent = new EdgeMsgEventImpl(value);
			this.notifyEvent(humidityEvent);
		}

	}

	/* Manages get requests, it generates an event. */
	private void handleGetData(RoutingContext routingContext) {
		final JsonArray arr = new JsonArray();

		arr.add(new JsonObject().put(MessageTypes.MANUALMODE.toString(), this.serverData.isManualMode()));
		arr.add(new JsonObject().put(MessageTypes.IS_WATERING.toString(), this.serverData.isWatering()));
		arr.add(new JsonObject().put(MessageTypes.SGH_STATE.toString(), this.serverData.getCurrentState()));

		for (final Pair<Integer, Date> elem : this.serverData.getHumidityValuesList()) {
			arr.add(new JsonObject().put(MessageTypes.HUMIDITY.toString(),
					new JsonObject().put(MessageTypes.HUMIDITY_VALUE.toString(), elem.getX())
							.put(MessageTypes.HUMIDITY_DATE.toString(), elem.getY().toString())));
		}

		for (final Pair<Long, Date> elem : this.serverData.getWateringsList()) {
			arr.add(new JsonObject().put(MessageTypes.WATERING_LIST.toString(),
					new JsonObject().put(MessageTypes.WATERING_DURATION.toString(), elem.getX())
							.put(MessageTypes.WATERING_DATE.toString(), elem.getY().toString())));
		}

		for (final Date elem : this.serverData.getWarningsList()) {
			arr.add(new JsonObject().put(MessageTypes.WARNING.toString(), elem.toString()));
		}

		routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
	}

	/* Sends an error as http server response. */
	private void sendError(final int statusCode, final HttpServerResponse response) {
		response.setStatusCode(statusCode).end();
	}

	/* Notifies the event to every observer. */
	private void notifyEvent(final Event ev) {
		synchronized (this.observers) {
			for (final EventObserver obs : this.observers) {
				obs.notifyEvent(ev);
			}
		}
	}

	@Override
	public void start() {
		final Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.post("/api/data").handler(this::handleAddNewData);
		router.get("/api/data").handler(this::handleGetData);
		this.vertx.createHttpServer().requestHandler(router::accept).listen(port);
	}

	@Override
	public void addObserver(final EventObserver obs) {
		synchronized (this.observers) {
			this.observers.add(obs);
		}

	}

	@Override
	public void removeObserver(final EventObserver obs) {
		synchronized (this.observers) {
			this.observers.remove(obs);
		}

	}

}