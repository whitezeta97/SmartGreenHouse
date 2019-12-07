package controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import utilities.MessageTypes;
import utilities.Pair;
import utilities.SghStates;

public class SghClientImpl extends Thread implements SghClient {
	private String host;
	private int port;
	private static final int SLEEP_TIME = 3000;

	private volatile boolean manualMode;
	private volatile boolean isWatering;
	private volatile String currentState;

	private volatile List<Pair<Float, Date>> umidityValuesList = new LinkedList<>();
	private volatile List<Pair<Long, Date>> wateringsList = new LinkedList<>();
	private volatile List<Date> warningsList = new LinkedList<>();

	public SghClientImpl(final String host, final int port) {
		this.host = host;
		this.port = port;

		this.start();
	}

	public void run() {

		while (true) {

			this.getDataFromServer();

			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private synchronized void getDataFromServer() {

		Vertx vertx = Vertx.vertx();
		HttpClient client = vertx.createHttpClient();

		client.get(this.port, this.host, "/api/data", response -> {
			System.out.println("Received response with status code " + response.statusCode());
			response.bodyHandler(bodyHandler -> {

				JsonArray arr = bodyHandler.toJsonArray();

				for (int i = 0; i < arr.size(); i++) {
					JsonObject cacca = arr.getJsonObject(i);

					if (cacca.containsKey(MessageTypes.FIRST_MESSAGE.toString())) {

					} else if (cacca.containsKey(MessageTypes.IS_WATERING.toString())) {
						this.isWatering = cacca.getBoolean(MessageTypes.IS_WATERING.toString());

					} else if (cacca.containsKey(MessageTypes.MANUALMODE.toString())) {
						this.manualMode = cacca.getBoolean(MessageTypes.MANUALMODE.toString());

					} else if (cacca.containsKey(MessageTypes.SGH_STATE.toString())) {
						this.currentState = (String) cacca.getValue(MessageTypes.SGH_STATE.toString());

					} else if (cacca.containsKey(MessageTypes.UMIDITY.toString())) {
						this.umidityValuesList.clear();
						//JsonObject niente = cacca.get;

						this.umidityValuesList.add((Pair<Float, Date>) cacca.getValue(MessageTypes.UMIDITY.toString()));

					} else if (cacca.containsKey(MessageTypes.WARNING.toString())) {
						this.warningsList.clear();
						this.warningsList.add((Date) cacca.getValue(MessageTypes.WARNING.toString()));

					} else if (cacca.containsKey(MessageTypes.WATERING_LIST.toString())) {
						this.wateringsList.clear();
						this.wateringsList
								.add((Pair<Long, Date>) cacca.getValue(MessageTypes.WATERING_LIST.toString()));

					}
				}

			});
		}).putHeader("content-type", "application/json").end();
	}

	@Override
	public boolean isManualMode() {
		return manualMode;
	}

	@Override
	public boolean isWatering() {
		return isWatering;
	}

	@Override
	public String getCurrentState() {
		return currentState;
	}

	@Override
	public List<Pair<Float, Date>> getUmidityValuesList() {
		return umidityValuesList;
	}

	@Override
	public List<Pair<Long, Date>> getWateringsList() {
		return wateringsList;
	}

	@Override
	public List<Date> getWarningsList() {
		return warningsList;
	}
}
