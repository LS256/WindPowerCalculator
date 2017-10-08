package pl.codeforfun;

/**
 * 
 * @author LS256
 * this class represent noise receiver - like house or certain poin desciribed by x,y coordinates
 */
public class NoiseReceiver {
	private Coordinates coordinates;
	
	NoiseReceiver(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public Coordinates getCoordinates(){
		return coordinates;
	}
	
}
