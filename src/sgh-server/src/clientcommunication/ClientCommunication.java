package clientcommunication;

public interface ClientCommunication {

	void sendSghState(String state);

	void sendUmidity(float umidity);

	void sendWatering(boolean isStarted);

	void sendManualMode(boolean isManualMode);

	void sendWarning(String warningMsg);
}
