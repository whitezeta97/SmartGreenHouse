package model;

public class ModelImpl implements Model {

	private static enum MessageType {
		FIRST_MESSAGE, SERVER_STARTED, SGH_STATE, UMIDITY, WATERING_ON, WATERING_OFF, MANUALMODE, WARNING,
	}

	public ModelImpl() {

	}

}
