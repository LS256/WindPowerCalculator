package pl.codeforfun;

/**
 * 
 * @author LS256
 * this class represent x and y coordinate
 */
public class Coordinates {
	private int xCoordinate;
	private int yCoordinate;
	
	Coordinates(int xCoordinate, int yCoordiante){
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordiante;
	}

	public int getXCoordinate() {
		return xCoordinate;
	}

	public void setXCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public int getYCoordinate() {
		return yCoordinate;
	}

	public void setYCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}
	
}
