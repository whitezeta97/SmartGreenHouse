package clientdataservice;

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

public class SghClientImpl extends Thread implements SghClient {

	private static final int SLEEP_TIME = 1000;
	private static final int MAX_SIZE = 100;

	private String host;
	private int port;
	private final Vertx vertx;
	private HttpClient client;

	private volatile ServerData serverData;

	private volatile Semaphore mutex;

	private BlockingQueue<JsonArray> receivedDataFromServer = new ArrayBlockingQueue<>(SghClientImpl.MAX_SIZE);

	public SghClientImpl(final String host, final int port, final ServerData serverData, Semaphore mutex) {
		this.port = port;
		this.host = host;
		this.serverData = serverData;

		this.vertx = Vertx.vertx();
		this.client = vertx.createHttpClient();

		this.mutex = mutex;

		this.start();
	}

	@Override
	public void run() {

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

	}

	private void manageDataFromHttpResponses() {
		this.serverData.setLastUpdateFromServer(new Date().toString());

		for (JsonArray arr : this.receivedDataFromServer) {

			for (int i = 0; i < arr.size(); i++) {
				JsonObject receivedJSonObject = arr.getJsonObject(i);

				if (receivedJSonObject.containsKey(MessageTypes.FIRST_MESSAGE.toString())) {

				} else if (receivedJSonObject.containsKey(MessageTypes.IS_WATERING.toString())) {
					this.serverData.setWatering(receivedJSonObject.getBoolean(MessageTypes.IS_WATERING.toString()));

				} else if (receivedJSonObject.containsKey(MessageTypes.MANUALMODE.toString())) {
					this.serverData.setManualMode(receivedJSonObject.getBoolean(MessageTypes.MANUALMODE.toString()));

				} else if (receivedJSonObject.containsKey(MessageTypes.SGH_STATE.toString())) {
					this.serverData.setCurrentState(receivedJSonObject.getString(MessageTypes.SGH_STATE.toString()));

				} else if (receivedJSonObject.containsKey(MessageTypes.UMIDITY.toString())) {
					JsonObject innerJSonArray = receivedJSonObject.getJsonObject(MessageTypes.UMIDITY.toString());

					this.serverData.addUmidityValuesListElement(
							innerJSonArray.getFloat(MessageTypes.UMIDITY_VALUE.toString()),
							innerJSonArray.getString(MessageTypes.UMIDITY_DATE.toString()));

				} else if (receivedJSonObject.containsKey(MessageTypes.WARNING.toString())) {
					this.serverData
							.addWarningsListElement(receivedJSonObject.getString(MessageTypes.WARNING.toString()));

				} else if (receivedJSonObject.containsKey(MessageTypes.WATERING_LIST.toString())) {
					JsonObject innerJSonArray = receivedJSonObject.getJsonObject(MessageTypes.WATERING_LIST.toString());

					this.serverData.addWateringsListElement(
							innerJSonArray.getFloat(MessageTypes.WATERING_DURATION.toString()) / 1000,
							innerJSonArray.getString(MessageTypes.WATERING_DATE.toString()));
				}
			}
			this.receivedDataFromServer.remove(arr);
		}
	}

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
