package controller;

import clientdataservice.SghClient;
import clientdataservice.SghClientImpl;
import serverdata.ServerData;
import serverdata.ServerDataImpl;
import view.Gui;
import view.GuiImpl;

public class ControllerImpl implements Controller {
	private final SghClient sghClient;
	private final ServerData serverData;
	//private final Gui gui;

	public ControllerImpl() {
		this.serverData = new ServerDataImpl();
		this.sghClient = new SghClientImpl("cf7c2290.ngrok.io", 80, this.serverData);
		//this.gui = new GuiImpl(this);
	}

	@Override
	public ServerData getDataForView() {
		return this.serverData;
	}

}
