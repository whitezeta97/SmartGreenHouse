package server.init;

import eventloop.EventLoopImpl;

/**
 * Allows to start the server.
 *
 */
public class ServerInit {

	public static void main(String[] args) {

		final String portName = "/dev/cu.isi00-DevB"; /* replace with the name of the serial port */

		new EventLoopImpl(portName, 9600).start();

	}
}
