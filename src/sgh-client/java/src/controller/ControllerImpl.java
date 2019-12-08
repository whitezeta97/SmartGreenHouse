package controller;

import clientdataservice.SghClient;
import clientdataservice.SghClientImpl;
import view.Gui;
import view.GuiImpl;

public class ControllerImpl extends Thread implements Controller {
	private final Gui gui;
	private final SghClient sghClient;

	private static final int SLEEP_TIME = 3000;

	public ControllerImpl() {
		this.gui = new GuiImpl();
		this.sghClient = new SghClientImpl("f3972e00.ngrok.io", 80);
		this.start();
	}

	private void getDataForView() {
		this.gui.setCurrentState(this.sghClient.getCurrentState());
		this.gui.setManualMode(this.sghClient.isManualMode());
		this.gui.setUmidityValuesList(this.sghClient.getUmidityValuesList());
		this.gui.setWarningsList(this.sghClient.getWarningsList());
		this.gui.setWatering(this.sghClient.isWatering());
		this.gui.setWateringsList(this.sghClient.getWateringsList());
		this.gui.viewUpdate();
	}

	public void run() {

		while (true) {

			this.getDataForView();

			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
