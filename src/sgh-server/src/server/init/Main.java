package server.init;

import eventloop.EventLoopImpl;

public class Main {
	public static void main(String[] args) {

		String portName = "/dev/cu.isi00-DevB"; /* replace with the name of the serial port */

		EventLoopImpl smartGreenHouseServer = new EventLoopImpl(portName, 9600);

		smartGreenHouseServer.start();

	}
}
