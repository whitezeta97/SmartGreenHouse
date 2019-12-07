package controller;

import io.vertx.core.AbstractVerticle;

public class Init extends AbstractVerticle {

	public static void main(String[] args) {

		final Controller controller = new ControllerImpl();

	}

}
