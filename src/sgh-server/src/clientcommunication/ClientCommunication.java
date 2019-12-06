package clientcommunication;

public interface ClientCommunication {

	void sendFirstMessage(String state);

	void sendSghState(String state);

	void sendUmidity(float umidity);

	void sendWateringOn();

	void sendWateringOff(long wateringDuration);

	void sendManualMode(boolean isManualMode);

	void sendWarning(String warningMsg, String state);
}
