package observables.msgservice;

import events.ControllerMsgEventImpl;
import observables.AbstractObservable;
import serialcomm.CommChannel;
import serialcomm.ExtendedSerialCommChannel;

public class ObservableMsgServiceImpl extends AbstractObservable implements ObservableMsgService {

	private CommChannel channel;
	private String port;
	private int rate;

	public ObservableMsgServiceImpl(final String port, final int rate) {
		this.port = port;
		this.rate = rate;
	}

	public void init() {
		try {
			channel = new ExtendedSerialCommChannel(port, rate);
			// channel = new SerialCommChannel(port, rate);
			System.out.println("Waiting Arduino for rebooting...");
			Thread.sleep(4000);
			System.out.println("Ready.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		new Thread(() -> {
			while (true) {
				try {
					String msg = channel.receiveMsg();
					System.out.println("received " + msg);
					this.notifyEvent(new ControllerMsgEventImpl(msg));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	public void sendMsg(String msg) {
		this.channel.sendMsg(msg);
	}

}
