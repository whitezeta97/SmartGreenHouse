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
import utilities.MessageTypes;
import utilities.Pair;
import utilities.SghStates;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import events.EdgeMsgEvent;
import events.EdgeMsgEventImpl;
import events.Event;

/*
 * Data Service as a vertx event-loop 
 */
public class ObservableSghDataServiceImpl extends AbstractVerticle implements Observable, ObservableSghDataService {

	private int port;

	private volatile boolean manualMode;
	private volatile boolean isWatering;
	private volatile SghStates currentState;
	private volatile List<Pair<Float, Date>> umidityValuesList = new LinkedList<>();
	private volatile List<Pair<Long, Date>> wateringsList = new LinkedList<>();
	private volatile List<Date> warningsList = new LinkedList<>();

	private LinkedList<EventObserver> observers;

	public ObservableSghDataServiceImpl(int port) {
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

		JsonObject res = routingContext.getBodyAsJson();

		if (res == null) {
			sendError(400, response);
		} else {
			float value = res.getFloat("value");
			log(value + "");
			response.setStatusCode(200).end();
			EdgeMsgEvent umidityEvent = new EdgeMsgEventImpl(value);
			this.notifyEvent(umidityEvent);
		}

	}

	private synchronized void handleGetData(RoutingContext routingContext) {
		JsonArray arr = new JsonArray();

		arr.add(new JsonObject().put(MessageTypes.MANUALMODE.toString(), this.manualMode));
		arr.add(new JsonObject().put(MessageTypes.IS_WATERING.toString(), this.isWatering));
		arr.add(new JsonObject().put(MessageTypes.SGH_STATE.toString(), this.currentState));

		for (Pair<Float, Date> elem : this.umidityValuesList) {
			arr.add(new JsonObject()
					.put(MessageTypes.UMIDITY.toString(),
							new JsonObject().put(MessageTypes.UMIDITY_VALUE.toString(), elem.getX()))
					.put(MessageTypes.UMIDITY_DATE.toString(), elem.getY().toString()));
		}

		for (Pair<Long, Date> elem : this.wateringsList) {
			arr.add(new JsonObject()
					.put(MessageTypes.WATERING_LIST.toString(),
							new JsonObject().put(MessageTypes.WATERING_DURATION.toString(), elem.getX()))
					.put(MessageTypes.WATERING_DATE.toString(), elem.getY().toString()));
		}

		for (Date elem : this.warningsList) {
			arr.add(new JsonObject().put(MessageTypes.WARNING.toString(), elem));
		}

		routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
	}

	private void sendError(int statusCode, HttpServerResponse response) {
		response.setStatusCode(statusCode).end();
	}

	private void log(String msg) {
		System.out.println("[DATA SERVICE] " + msg);
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

	@Override
	public synchronized void setManualMode(boolean manualMode) {
		this.manualMode = manualMode;
	}

	@Override
	public synchronized void setWatering(boolean isWatering) {
		this.isWatering = isWatering;
	}

	@Override
	public synchronized void setCurrentState(SghStates currentState) {
		this.currentState = currentState;
	}

	@Override
	public synchronized void setUmidityValuesList(List<Pair<Float, Date>> umidityValuesList) {
		this.umidityValuesList = umidityValuesList;
	}

	@Override
	public synchronized void setWateringsList(List<Pair<Long, Date>> wateringsList) {
		this.wateringsList = wateringsList;
	}

	@Override
	public synchronized void setWarningsList(List<Date> warningsList) {
		this.warningsList = warningsList;
	}

}