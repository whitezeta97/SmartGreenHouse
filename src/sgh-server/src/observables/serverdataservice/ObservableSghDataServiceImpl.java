package observables.serverdataservice;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import observer.event.EventObserver;
import serverdata.ServerData;
import utilities.MessageTypes;
import utilities.Pair;

import java.util.Date;
import java.util.LinkedList;

import events.EdgeMsgEvent;
import events.EdgeMsgEventImpl;
import events.Event;

/*
 * Data Service as a vertx event-loop 
 */
public class ObservableSghDataServiceImpl extends AbstractVerticle implements ObservableSghDataService {

	private int port;
	private ServerData serverData;

	private LinkedList<EventObserver> observers;

	public ObservableSghDataServiceImpl(final int port, final ServerData serverData) {
		this.observers = new LinkedList<>();
		this.port = port;
		this.serverData = serverData;
	}

	@Override
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.post("/api/data").handler(this::handleAddNewData);
		router.get("/api/data").handler(this::handleGetData);
		vertx.createHttpServer().requestHandler(router::accept).listen(port);
	}

	private void handleAddNewData(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();

		JsonObject res = routingContext.getBodyAsJson();

		if (res == null) {
			sendError(400, response);
		} else {
			float value = res.getFloat("value");
			response.setStatusCode(200).end();
			EdgeMsgEvent umidityEvent = new EdgeMsgEventImpl(value);
			this.notifyEvent(umidityEvent);
		}

	}

	private void handleGetData(RoutingContext routingContext) {
		JsonArray arr = new JsonArray();

		arr.add(new JsonObject().put(MessageTypes.MANUALMODE.toString(), this.serverData.isManualMode()));
		arr.add(new JsonObject().put(MessageTypes.IS_WATERING.toString(), this.serverData.isWatering()));
		arr.add(new JsonObject().put(MessageTypes.SGH_STATE.toString(), this.serverData.getCurrentState()));

		for (Pair<Float, Date> elem : this.serverData.getUmidityValuesList()) {
			arr.add(new JsonObject().put(MessageTypes.UMIDITY.toString(),
					new JsonObject().put(MessageTypes.UMIDITY_VALUE.toString(), elem.getX())
							.put(MessageTypes.UMIDITY_DATE.toString(), elem.getY().toString())));
		}

		for (Pair<Long, Date> elem : this.serverData.getWateringsList()) {
			final float durationInSeconds = elem.getX() / 1000;
			System.out.println("NEGRO NEGROOOO: " + durationInSeconds);
			arr.add(new JsonObject().put(MessageTypes.WATERING_LIST.toString(),
					new JsonObject().put(MessageTypes.WATERING_DURATION.toString(), durationInSeconds)
							.put(MessageTypes.WATERING_DATE.toString(), elem.getY().toString())));
		}

		for (Date elem : this.serverData.getWarningsList()) {
			arr.add(new JsonObject().put(MessageTypes.WARNING.toString(), elem.toString()));
		}

		routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
	}

	private void sendError(int statusCode, HttpServerResponse response) {
		response.setStatusCode(statusCode).end();
	}

	private void notifyEvent(final Event ev) {
		synchronized (this.observers) {
			for (EventObserver obs : this.observers) {
				obs.notifyEvent(ev);
			}
		}
	}

	@Override
	public void addObserver(EventObserver obs) {
		synchronized (this.observers) {
			this.observers.add(obs);
		}

	}

	@Override
	public void removeObserver(EventObserver obs) {
		synchronized (this.observers) {
			this.observers.remove(obs);
		}

	}

}