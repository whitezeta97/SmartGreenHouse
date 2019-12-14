package controller;

import java.util.concurrent.Semaphore;

import clientdataservice.SghClient;
import clientdataservice.SghClientImpl;
import serverdata.ServerData;
import serverdata.ServerDataImpl;
import view.Gui;
import view.GuiImpl;

public class ControllerImpl implements Controller {
	private static final int MUTEXT_PERMITS = 1;
	private Semaphore mutex;
	private final SghClient sghClient;
	private final ServerData serverData;
	private final Gui gui;

	public ControllerImpl() {
		this.mutex = new Semaphore(ControllerImpl.MUTEXT_PERMITS);
		this.serverData = new ServerDataImpl();
		this.sghClient = new SghClientImpl("3eb1f184.ngrok.io", 80, this.serverData, this.mutex);
		this.gui = new GuiImpl(this, this.mutex);
	}

	@Override
	public ServerData getDataForView() {
		return this.serverData;
	}

}
