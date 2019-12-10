package observable.serverdataservice;

import java.util.Date;
import java.util.List;

import utilities.Pair;
import utilities.SghStates;

public interface ObservableSghDataService {

	void setManualMode(boolean manualMode);

	void setWatering(boolean isWatering);

	void setCurrentState(SghStates currentState);

	void setUmidityValuesList(List<Pair<Float, Date>> umidityValuesList);

	void setWateringsList(List<Pair<Float, Date>> wateringsList);

	void setWarningsList(List<Date> warningsList);

}
