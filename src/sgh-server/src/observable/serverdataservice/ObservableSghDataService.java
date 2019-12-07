package observable.serverdataservice;

import java.util.Date;
import java.util.List;

import utilities.Pair;
import utilities.SghStates;

public interface ObservableSghDataService {

	public void setManualMode(boolean manualMode);

	public void setWatering(boolean isWatering);

	public void setCurrentState(SghStates currentState);

	public void setUmidityValuesList(List<Pair<Float, Date>> umidityValuesList);

	public void setWateringsList(List<Pair<Long, Date>> wateringsList);

	public void setWarningsList(List<Date> warningsList);

}
