package observable.dataservice.copy;

class DataPointImpl implements DataPoint {
	private double value;
	private long time;
	private String place;

	public DataPointImpl(double value, long time, String place) {
		this.value = value;
		this.time = time;
		this.place = place;
	}

	public double getValue() {
		return value;
	}

	public long getTime() {
		return time;
	}

	public String getPlace() {
		return place;
	}
}
