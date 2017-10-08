package pl.codeforfun;

/**
 * Class which represent wind data measurement
 * @author LS
 * @param vMvean80m - is 1 hour average mean wind speed measured at 80m
 * @param vMvean60m - is 1 hour average mean wind speed measured at 60m
 * @param vMvean30m - is 1 hour average mean wind speed measured at 30m
 */
public class MeasuredWindData {
	
	private String dateStamp;
	private String hour;
	double vMean100m;
	private int vMean80m;
	private int vMean60m;
	private int vMean30m;
	
	public MeasuredWindData(String dateStamp, String hour, int vMean80m, int vMean60m, int vMean30m){
		this.dateStamp = dateStamp;
		this.hour = hour;
		this.vMean80m = vMean80m;
		this.vMean60m = vMean60m;
		this.vMean30m = vMean30m;
	}
	
	public String getDateStamp() {
		return dateStamp;
	}
	
	public String getHour() {
		return hour;
	}
	
	public int getvMean80m() {
		return vMean80m;
	}
	
	public int getvMean60m() {
		return vMean60m;
	}
	
	public int getvMean30m() {
		return vMean30m;
	}
}
