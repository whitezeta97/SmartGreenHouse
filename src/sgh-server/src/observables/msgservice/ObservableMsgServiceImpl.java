package observables.msgservice;

import events.ControllerMsgEventImpl;
import observables.AbstractObservable;
import serialcomm.CommChannel;
import serialcomm.ExtendedSerialCommChannel;

/**
 * 
 * Implements an observable message service, generating an event whenever it
 * gets a message from serial port.
 *
 */
public class ObservableMsgServiceImpl extends AbstractObservable implements ObservableMsgService {

	private CommChannel channel;
	private String port;
	private int rate;

	/**
	 * 
	 * @param port
	 *            the serial port name.
	 * @param rate
	 *            the serial rate.
	 */
	public ObservableMsgServiceImpl(final String port, final int rate) {
		this.port = port;
		this.rate = rate;
	}

	@Override
	public void init() {
		try {
			this.channel = new ExtendedSerialCommChannel(this.port, this.rate);
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
					final String msg = channel.receiveMsg();
					//System.out.println("received " + msg);
					this.notifyEvent(new ControllerMsgEventImpl(msg));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void sendMsg(final String msg) {
		this.channel.sendMsg(msg);
	}

}
