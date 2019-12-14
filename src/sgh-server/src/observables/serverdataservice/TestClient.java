package observables.serverdataservice;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;

public class TestClient extends AbstractVerticle {

	public static void main(String[] args) {

		String host = "cf7c2290.ngrok.io";
		int port = 80;

		Vertx vertx = Vertx.vertx();
		HttpClient client = vertx.createHttpClient();

		JsonObject item = new JsonObject().put("value", 20.2);

		client.post(port, host, "/api/data", response -> {
			System.out.println("Received response with status code " + response.statusCode());
			response.bodyHandler(bodyHandler -> {
				System.out.println(bodyHandler.toString());
			});
		}).putHeader("content-type", "application/json").end(item.encodePrettily());
		
		
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		item = new JsonObject().put("value", 9);

		client.post(port, host, "/api/data", response -> {
			System.out.println("Received response with status code " + response.statusCode());
			response.bodyHandler(bodyHandler -> {
				System.out.println(bodyHandler.toString());
			});
		}).putHeader("content-type", "application/json").end(item.encodePrettily());
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		item = new JsonObject().put("value", 35);

		client.post(port, host, "/api/data", response -> {
			System.out.println("Received response with status code " + response.statusCode());
			response.bodyHandler(bodyHandler -> {
				System.out.println(bodyHandler.toString());
			});
		}).putHeader("content-type", "application/json").end(item.encodePrettily());
	}

}
