package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import view.Gui;

public class Init extends AbstractVerticle {
	static JsonArray cacca;

	public static void main(String[] args) {

		new Gui();

		String host = "c5dc9fc2.ngrok.io";
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
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		client.get(port, host, "/api/data", response -> {
			System.out.println("Received response with status code " + response.statusCode());
			response.bodyHandler(bodyHandler -> {
				// System.out.println(bodyHandler.toJsonArray());
				// cacca = bodyHandler.toJsonArray().getJsonArray(0);
				//JsonArray arr = new JsonArray();
				//arr = bodyHandler.toJsonArray();
				//JsonObject asdasd = arr.getJsonObject(0);

				//System.out.println(arr.getJsonArray(0).getJsonObject(0));

				/*for (int i = 0; i < arr.size(); i++) {
					arr.getJsonObject(i);
				}

				/*
				 * for (Map.Entry<String, Object> entry : arr.toj) { String key =
				 * entry.getKey(); ArrayList<String> value = entry.getValue(); // now work with
				 * key and value... }
				 */

				//System.out.println(arr.getJsonObject(0).getInteger("time"));
				// System.out.println(bodyHandler.toJsonObject().getLong("value"));

				// System.out.println(jesion.get(0).toString());

			});
		}).putHeader("content-type", "application/json").end(item.encodePrettily());

	}

}
