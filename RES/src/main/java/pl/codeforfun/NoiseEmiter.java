package pl.codeforfun;

/**
 * 
 * @author LS256
 * this class represent noise emision object like wind turbine etc..
 */
public class NoiseEmiter {
	private Coordinates coordinates;
	private double noiseEmission;
	
	NoiseEmiter(Coordinates coordinates, double noiseEmission) {
		this.coordinates = coordinates;
		this.noiseEmission = noiseEmission;
	}

	public Coordinates getCoordinates(){
		return coordinates;
	}
	
	public double getNoiseEmission() {
		return noiseEmission;
	}

	public void setNoiseEmission(double noiseEmission) {
		this.noiseEmission = noiseEmission;
	}
	

}
