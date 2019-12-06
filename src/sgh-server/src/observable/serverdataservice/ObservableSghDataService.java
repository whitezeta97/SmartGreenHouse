package observable.serverdataservice;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import observables.Observable;
import observer.event.EventObserver;

import java.util.LinkedList;

import events.EdgeMsgEvent;
import events.EdgeMsgEventImpl;
import events.Event;

/*
 * Data Service as a vertx event-loop 
 */
public class ObservableSghDataService extends AbstractVerticle implements Observable {

	private int port;
	private static final int MAX_SIZE = 10;
	private LinkedList<ServerDataPoint> values;

	private LinkedList<EventObserver> observers;

	public ObservableSghDataService(int port) {
		this.values = new LinkedList<>();
		this.observers = new LinkedList<>();
		this.port = port;
	}

	@Override
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.post("/api/data").handler(this::handleAddNewData);
		router.get("/api/data").handler(this::handleGetData);
		vertx.createHttpServer().requestHandler(router::accept).listen(port);

		log("Service ready.");
	}

	private void handleAddNewData(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		// log("new msg "+routingContext.getBodyAsString());
		JsonObject res = routingContext.getBodyAsJson();
		if (res == null) {
			sendError(400, response);
		} else {
			float value = res.getFloat("value");

			this.values.addFirst(new DataPointImpl(value));
			if (this.values.size() > MAX_SIZE) {
				this.values.removeLast();
			}

			log(value + "");
			response.setStatusCode(200).end();
			EdgeMsgEvent umidityEvent = new EdgeMsgEventImpl(value);
			this.notifyEvent(umidityEvent);

		}
	}

	private void handleGetData(RoutingContext routingContext) {
		JsonArray arr = new JsonArray();
		for (ServerDataPoint p : this.values) {
			JsonObject data = new JsonObject();
			data.put("value", p.getValue());
			arr.add(data);
		}
		routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
	}

	private void sendError(int statusCode, HttpServerResponse response) {
		response.setStatusCode(statusCode).end();
	}

	private void log(String msg) {
		System.out.println("[DATA SERVICE] " + msg);
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

	private void notifyEvent(final Event ev) {
		synchronized (this.observers) {
			for (EventObserver obs : this.observers) {
				obs.notifyEvent(ev);
			}
		}
	}

}