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
		SGH_STATE, UMIDITY, WATERING, MANUALMODE, WARNING,
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
	public void sendWatering(final boolean isStarted) {
		this.jsonObject.clear();
		this.jsonObject.put("msgtype", MessageType.WATERING).put("watering", isStarted);
		this.sendMsg();
	}
	
	@Override
	public void sendManualMode(final boolean isManualMode) {
		this.jsonObject.clear();
		this.jsonObject.put("msgtype", MessageType.MANUALMODE).put("watering", isManualMode);
		this.sendMsg();
	}

	@Override
	public void sendWarning(final String warningMsg) {
		this.jsonObject.clear();
		this.jsonObject.put("msgtype", MessageType.WARNING).put("warning", warningMsg);
		this.sendMsg();
	}

}
