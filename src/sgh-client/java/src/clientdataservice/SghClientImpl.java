package clientdataservice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import serverdata.ServerData;
import utilities.MessageTypes;

/**
 * 
 * Implements the SGH Client that sends get request to SGH Server to get data.
 *
 */
public class SghClientImpl implements SghClient {

	private static final int SLEEP_TIME = 200;
	private static final int MAX_SIZE = 100;

	private String host;
	private int port;
	private final Vertx vertx;
	private HttpClient client;

	private volatile ServerData serverData;

	private volatile Semaphore mutex;

	private BlockingQueue<JsonArray> receivedDataFromServer = new ArrayBlockingQueue<>(SghClientImpl.MAX_SIZE);

	/**
	 * 
	 * @param host
	 *            the host name to which the client has to connect.
	 * @param port
	 *            the server's port.
	 * @param serverData
	 *            stores the SGH Server data received from the server.
	 * @param mutex
	 *            the semaphore to access shared data.
	 */
	public SghClientImpl(final String host, final int port, final ServerData serverData, Semaphore mutex) {
		this.port = port;
		this.host = host;
		this.serverData = serverData;

		this.vertx = Vertx.vertx();
		this.client = vertx.createHttpClient();

		this.mutex = mutex;

		this.startGettingDataFromServer();
	}

	/* Starts getting data from server. */
	private void startGettingDataFromServer() {
		new Thread(() -> {
			while (true) {
				try {
					this.getDataFromServer();
					this.mutex.acquire();
					this.manageDataFromHttpResponses();
					this.mutex.release();
				} catch (InterruptedException e1) {
					this.mutex.release();
					e1.printStackTrace();
				}

				try {
					Thread.sleep(SghClientImpl.SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/* stores locally the SGH Server data received from SGH Server. */
	private void manageDataFromHttpResponses() {

		if (!this.receivedDataFromServer.isEmpty()) {
			this.serverData.setLastUpdateFromServer(new Date().toString());
		}

		for (final JsonArray arr : this.receivedDataFromServer) {

			for (int i = 0; i < arr.size(); i++) {
				final JsonObject receivedJSonObject = arr.getJsonObject(i);

				if (receivedJSonObject.containsKey(MessageTypes.FIRST_MESSAGE.toString())) {

				} else if (receivedJSonObject.containsKey(MessageTypes.IS_WATERING.toString())) {
					this.serverData.setWatering(receivedJSonObject.getBoolean(MessageTypes.IS_WATERING.toString()));

				} else if (receivedJSonObject.containsKey(MessageTypes.MANUALMODE.toString())) {
					this.serverData.setManualMode(receivedJSonObject.getBoolean(MessageTypes.MANUALMODE.toString()));

				} else if (receivedJSonObject.containsKey(MessageTypes.SGH_STATE.toString())) {
					this.serverData.setCurrentState(receivedJSonObject.getString(MessageTypes.SGH_STATE.toString()));

				} else if (receivedJSonObject.containsKey(MessageTypes.HUMIDITY.toString())) {
					final JsonObject innerJSonArray = receivedJSonObject
							.getJsonObject(MessageTypes.HUMIDITY.toString());

					this.serverData.addUmidityValuesListElement(
							innerJSonArray.getInteger(MessageTypes.HUMIDITY_VALUE.toString()),
							innerJSonArray.getString(MessageTypes.HUMIDITY_DATE.toString()));

				} else if (receivedJSonObject.containsKey(MessageTypes.WARNING.toString())) {
					this.serverData
							.addWarningsListElement(receivedJSonObject.getString(MessageTypes.WARNING.toString()));

				} else if (receivedJSonObject.containsKey(MessageTypes.WATERING_LIST.toString())) {
					final JsonObject innerJSonArray = receivedJSonObject
							.getJsonObject(MessageTypes.WATERING_LIST.toString());

					final BigDecimal wateringDuration = new BigDecimal(
							innerJSonArray.getFloat(MessageTypes.WATERING_DURATION.toString()) / 1000).setScale(2,
									RoundingMode.HALF_UP);

					this.serverData.addWateringsListElement(wateringDuration.floatValue(),
							innerJSonArray.getString(MessageTypes.WATERING_DATE.toString()));
				}
			}
			this.receivedDataFromServer.remove(arr);
		}
	}

	/* Gets SGH Server data via http get request. */
	private void getDataFromServer() {

		this.client.get(this.port, this.host, "/api/data", response -> {

			System.out.println("Received response with status code " + response.statusCode());

			response.bodyHandler(bodyHandler -> {
				JsonArray arr = bodyHandler.toJsonArray();

				if (this.receivedDataFromServer.size() > SghClientImpl.MAX_SIZE) {
					this.receivedDataFromServer.poll();
				}
				this.receivedDataFromServer.add(arr);

			});
		}).putHeader("content-type", "application/json").end();

	}

}
