package observable.serverdataservice;

class DataPointImpl implements ServerDataPoint {
	private double value;

	public DataPointImpl(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}
	
}
