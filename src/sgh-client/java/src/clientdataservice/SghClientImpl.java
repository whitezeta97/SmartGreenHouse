package clientdataservice;

import java.util.Date;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import serverdata.ServerData;
import utilities.MessageTypes;

public class SghClientImpl extends Thread implements SghClient {
	private String host;
	private int port;
	private static final int SLEEP_TIME = 50;

	private volatile ServerData serverData ;

	public SghClientImpl(final String host, final int port, final ServerData serverData) {
		this.port = port;
		this.host = host;
		this.serverData = serverData;

		this.start();
	}
	
	@Override
	public void run() {

		while (true) {

			this.getDataFromServer();

			try {
				Thread.sleep(SghClientImpl.SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void getDataFromServer() {

		Vertx vertx = Vertx.vertx();
		HttpClient client = vertx.createHttpClient();

		client.get(this.port, this.host, "/api/data", response -> {
			System.out.println("Received response with status code " + response.statusCode());
			response.bodyHandler(bodyHandler -> {

				JsonArray arr = bodyHandler.toJsonArray();

				this.serverData.setLastUpdateFromServer(new Date().toString());

				for (int i = 0; i < arr.size(); i++) {
					JsonObject receivedJSonObject = arr.getJsonObject(i);

					if (receivedJSonObject.containsKey(MessageTypes.FIRST_MESSAGE.toString())) {

					} else if (receivedJSonObject.containsKey(MessageTypes.IS_WATERING.toString())) {
						this.serverData.setWatering(receivedJSonObject.getBoolean(MessageTypes.IS_WATERING.toString()));

					} else if (receivedJSonObject.containsKey(MessageTypes.MANUALMODE.toString())) {
						this.serverData
								.setManualMode(receivedJSonObject.getBoolean(MessageTypes.MANUALMODE.toString()));

					} else if (receivedJSonObject.containsKey(MessageTypes.SGH_STATE.toString())) {
						this.serverData
								.setCurrentState(receivedJSonObject.getString(MessageTypes.SGH_STATE.toString()));

					} else if (receivedJSonObject.containsKey(MessageTypes.UMIDITY.toString())) {
						JsonObject innerJSonArray = receivedJSonObject.getJsonObject(MessageTypes.UMIDITY.toString());

						this.serverData.addUmidityValuesListElement(
								innerJSonArray.getFloat(MessageTypes.UMIDITY_VALUE.toString()),
								innerJSonArray.getString(MessageTypes.UMIDITY_DATE.toString()));

					} else if (receivedJSonObject.containsKey(MessageTypes.WARNING.toString())) {
						this.serverData
								.addWarningsListElement(receivedJSonObject.getString(MessageTypes.WARNING.toString()));

					} else if (receivedJSonObject.containsKey(MessageTypes.WATERING_LIST.toString())) {
						JsonObject innerJSonArray = receivedJSonObject
								.getJsonObject(MessageTypes.WATERING_LIST.toString());

						this.serverData.addWateringsListElement(
								innerJSonArray.getFloat(MessageTypes.WATERING_DURATION.toString()),
								innerJSonArray.getString(MessageTypes.WATERING_DATE.toString()));
					}
				}

			});
		}).putHeader("content-type", "application/json").end();

	}

}
