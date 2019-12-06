package clientcommunication;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;

public class ClientCommunicationImpl implements ClientCommunication {
	private int port;
	private String host;
	private Vertx vertx;
	private HttpClient client;
	private JsonObject jsonObject;

	private enum MessageType {
		FIRST_MESSAGE, SERVER_STARTED, SGH_STATE, UMIDITY, WATERING_ON, WATERING_OFF, MANUALMODE, WARNING,
	}

	public ClientCommunicationImpl(final int port, final String host) {
		this.port = port;
		this.host = host;
		this.jsonObject = new JsonObject();
		this.vertx = Vertx.vertx();
		this.client = vertx.createHttpClient();
	}

	private void sendMsg() {
		this.jsonObject.put("time", System.currentTimeMillis());
		this.client.post(this.port, this.host, "/api/data", response -> {
			System.out.println("Received response with status code " + response.statusCode());
			response.bodyHandler(bodyHandler -> {
				System.out.println(bodyHandler.toString());
			});
		}).putHeader("content-type", "application/json").end(this.jsonObject.encodePrettily());
	}

	@Override
	public void sendFirstMessage(String state) {
		this.jsonObject.clear();
		this.jsonObject.put("msgtype", MessageType.FIRST_MESSAGE).put("state", state);
		this.sendMsg();

	}

	@Override
	public void sendSghState(String state) {
		this.jsonObject.clear();
		this.jsonObject.put("msgtype", MessageType.SGH_STATE).put("state", state);
		this.sendMsg();

	}

	@Override
	public void sendUmidity(final float umidity) {
		this.jsonObject.clear();
		this.jsonObject.put("msgtype", MessageType.UMIDITY).put("umidity", umidity);
		this.sendMsg();
	}

	@Override
	public void sendWateringOn() {
		this.jsonObject.clear();
		this.jsonObject.put("msgtype", MessageType.WATERING_ON);
		this.sendMsg();
	}

	@Override
	public void sendWateringOff(final long wateringDuration) {
		this.jsonObject.clear();
		this.jsonObject.put("msgtype", MessageType.WATERING_OFF).put("wateringduration", wateringDuration);
		this.sendMsg();
	}

	@Override
	public void sendManualMode(final boolean isManualMode) {
		this.jsonObject.clear();
		this.jsonObject.put("msgtype", MessageType.MANUALMODE).put("manualmode", isManualMode);
		this.sendMsg();
	}

	@Override
	public void sendWarning(final String warningMsg, final String state) {
		this.jsonObject.clear();
		this.jsonObject.put("msgtype", MessageType.WARNING).put("warning", warningMsg).put("state", state);
		this.sendMsg();
	}

}
