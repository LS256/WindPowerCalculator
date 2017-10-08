package pl.codeforfun;

/**
 * class to represent certain wind turbine power curve
 * @author LS256
 *
 */
public class DBpowerCurve {
	double windSpeed;
	int generatedPower;
	
	public DBpowerCurve() {
		
	}
	
	public DBpowerCurve(double windSpeed, int generatedPower) {
		this.windSpeed = windSpeed;
		this.generatedPower = generatedPower;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public int getGeneratedPower() {
		return generatedPower;
	}

	public void setGeneratedPower(int generatedPower) {
		this.generatedPower = generatedPower;
	}
	
	

}
