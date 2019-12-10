package view;

import java.util.Date;
import java.util.List;

import utilities.Pair;

public interface Gui {

	void viewUpdate();

	void setManualMode(boolean manualMode);

	void setWatering(boolean isWatering);

	void setCurrentState(String currentState);

	void setUmidityValuesList(List<Pair<Float, String>> umidityValuesList);

	void setWateringsList(List<Pair<Float, String>> wateringsList);

	void setWarningsList(List<String> warningsList);

	void setLastUpdateFromServer(Date lastUpdateFromServer);
}
