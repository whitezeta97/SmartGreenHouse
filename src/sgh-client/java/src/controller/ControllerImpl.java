package controller;

import view.Gui;
import view.GuiImpl;

public class ControllerImpl implements Controller {
	private final Gui gui;
	private final SghClient sghClient;

	private static final int SLEEP_TIME = 3000;

	public ControllerImpl() {
		this.gui = new GuiImpl();
		this.sghClient = new SghClientImpl("ce891104.ngrok.io", 80);
	}

	private void boh() {
		try {
			Thread.sleep(ControllerImpl.SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
