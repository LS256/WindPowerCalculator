package pl.codeforfun;

public class DBwtg {
	private int id;
	private String wtgType;
	private int wtgPower;
	private double rotorDiameter;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWtgType() {
		return wtgType;
	}

	public void setWtgType(String wtgType) {
		this.wtgType = wtgType;
	}

	public int getWtgPower() {
		return wtgPower;
	}

	public void setWtgPower(int wtgPower) {
		this.wtgPower = wtgPower;
	}

	public double getRotorDiameter() {
		return rotorDiameter;
	}

	public void setRotorDiameter(double rotorDiameter) {
		this.rotorDiameter = rotorDiameter;
	}

	public DBwtg(){
		
	}
	
	public DBwtg(int id, String wtgType, int wtgPower, double rotorDiameter){
		this.id = id;
		this.wtgType = wtgType;
		this.wtgPower = wtgPower;
		this.rotorDiameter = rotorDiameter;
	}
	
	@Override
	public String toString() {
		return wtgType + " with nominal power " + wtgPower +" kW, " + " and rotor diameter " + rotorDiameter;
		
	}
	
	
	

}
