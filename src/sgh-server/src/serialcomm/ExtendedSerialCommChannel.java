package serialcomm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Comm channel implementation based on serial port.
 * 
 */
public class ExtendedSerialCommChannel implements CommChannel, SerialPortEventListener {

	private SerialPort serialPort;
	private BlockingQueue<String> queue;
	private StringBuffer currentMsg = new StringBuffer("");
	private static final int SERVER_PORT = 8080;

	private AndroidEmulatorCommChannel androidEmulatorChannel;
	private static final char BT_MESSAGES_ID = '$';

	public ExtendedSerialCommChannel(final String port, final int rate) throws Exception {
		this(port, rate, SERVER_PORT);
	}

	public ExtendedSerialCommChannel(final String port, final int rate, final int localIPportEmu) throws Exception {

		this.queue = new ArrayBlockingQueue<String>(100);

		this.serialPort = new SerialPort(port);

		try {

			this.serialPort.openPort();

			this.serialPort.setParams(rate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			this.serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

			// serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
			this.serialPort.addEventListener(this);

			System.out.println("serial port ok");
			// emulator server

			this.androidEmulatorChannel = new AndroidEmulatorCommChannel(localIPportEmu);
			this.androidEmulatorChannel.start();

		} catch (SerialPortException ex) {
			System.err.println("Error on writing string to port: " + port);
			ex.printStackTrace();
		} catch (IOException ex) {
			System.err.println("Error on installing emu|IP server");
			ex.printStackTrace();
		}

	}

	@Override
	public void sendMsg(final String msg) {
		char[] array = (msg + "\n").toCharArray();
		byte[] bytes = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			bytes[i] = (byte) array[i];
		}
		try {
			synchronized (this.serialPort) {
				this.serialPort.writeBytes(bytes);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public String receiveMsg() throws InterruptedException {
		return this.queue.take();
	}

	@Override
	public boolean isMsgAvailable() {
		return !this.queue.isEmpty();
	}

	/**
	 * This should be called when you stop using the port. This will prevent port
	 * locking on platforms like Linux.
	 */
	public void close() {
		try {
			if (this.serialPort != null) {
				this.serialPort.removeEventListener();
				this.serialPort.closePort();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	@Override
	public void serialEvent(final SerialPortEvent event) {
		/* if there are bytes received in the input buffer */
		if (event.isRXCHAR()) {
			try {
				String msg = this.serialPort.readString(event.getEventValue());

				msg = msg.replaceAll("\r", "");

				this.currentMsg.append(msg);

				boolean goAhead = true;

				while (goAhead) {
					String msg2 = this.currentMsg.toString();
					int index = msg2.indexOf("\n");
					if (index >= 0) {

						if (msg2.getBytes().length > 1 && msg2.getBytes()[index - 1] == BT_MESSAGES_ID) {
							this.androidEmulatorChannel.sendMsgToAndroidEmulator(msg2.substring(0, index - 1));
						} else {
							this.queue.put(msg2.substring(0, index));
						}

						this.currentMsg = new StringBuffer("");
						if (index + 1 < msg2.length()) {
							this.currentMsg.append(msg2.substring(index + 1));
						}
					} else {
						goAhead = false;
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Error in receiving string from COM-port: " + ex);
			}
		}
	}

	class AndroidEmulatorCommChannel extends Thread {

		private ServerSocket server;
		private Socket socket;

		public AndroidEmulatorCommChannel(int port) throws IOException {
			this.server = new ServerSocket(port);
		}

		@Override
		public void run() {
			try {
				this.socket = this.server.accept();
				while (true) {
					BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
					String message = in.readLine();
					sendMsg(message + BT_MESSAGES_ID);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void sendMsgToAndroidEmulator(final String msg) {
			if (this.socket != null) {
				PrintWriter out;
				try {
					out = new PrintWriter(this.socket.getOutputStream(), true);
					out.println(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
