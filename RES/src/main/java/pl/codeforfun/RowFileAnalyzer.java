package pl.codeforfun;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * class where we can analyze wind data from file
 * @author LS256
 */
public class RowFileAnalyzer {
	
	static final Logger logger = LogManager.getLogger(RES.class.getName());
//	List<MeasuredWindData> measuredWindData = new ArrayList<MeasuredWindData>();
	Map<String, List<MeasuredWindData>> measuredWindDataMap = new TreeMap<String, List<MeasuredWindData>>();
	Map<String, Map<Double, Integer>> finalGeneratedPower = new TreeMap<String, Map<Double, Integer>>();
	
	// eventually remove
	Map<String, Map<Double, Double>> finalGeneratedPowerD = new TreeMap<String, Map<Double, Double>>();
	
	public RowFileAnalyzer(){
	
	}
	
	/**
	 * Method responsible for reading file with wind data. This method isolate only some parameters needed for further analysis and put it
	 * into a map where key is a name of file with wind Data and value of this map is a list  with MeasuredWindData objects
	 * remember that averaged wind data still need to be multiplied by scale factor = 0.01
	 * @param fileWithWindData - file name with data
	 */
	public void fileReader(String fileWithWindData){
		List<MeasuredWindData> measuredWindDataList = new ArrayList<MeasuredWindData>();
		
		try {
			// 2011_01_80m.row
			String fileContent = "";
			String previousHour = "00";
			String  dateStamp = "";
			int hourlySum80m = 0;
			int hourlySum60m = 0;
			int hourlySum30m = 0;
			int cnt = 0;
	
			
			FileInputStream fileInputStream = new FileInputStream(fileWithWindData);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
//			long timeStart = System.currentTimeMillis();
	
			while((fileContent = bufferedReader.readLine() ) != null){
				String[] analyzedRow = fileContent.split("\\t");
				if(analyzedRow.length>24) {
					dateStamp = analyzedRow[0];
					String[] tempHourTab = analyzedRow[1].split(":");	
					
					if(tempHourTab[0].equals(previousHour)) {
						hourlySum80m = hourlySum80m + Integer.valueOf(analyzedRow[2]);
						hourlySum60m = hourlySum60m + Integer.valueOf(analyzedRow[5]);
						hourlySum30m = hourlySum30m + Integer.valueOf(analyzedRow[8]);
						cnt++;	
					} else{ 
//						logger.info("{}\t{}\t{}\t{}\t{}", dateStamp, previousHour, hourlySum80m, hourlySum60m, hourlySum30m);
						//	mean wind speed multiply by 10 to keep precision of mean wind speed to two numbers after comma. Final mean wind speed is received by additional dividing by 100
						measuredWindDataList.add(new MeasuredWindData(dateStamp, previousHour, (10*hourlySum80m)/cnt, (10*hourlySum60m)/cnt, (10*hourlySum30m)/cnt));	
						hourlySum80m = Integer.valueOf(analyzedRow[2]);
						hourlySum60m = Integer.valueOf(analyzedRow[5]);
						hourlySum30m = Integer.valueOf(analyzedRow[8]);
						cnt = 1;
					}
					previousHour =  tempHourTab[0];
				}	
			}
//			long totalTime = System.currentTimeMillis() - timeStart;
//			System.out.println("czasOperacji " + totalTime);
//			logger.info("{}\t{}\t{}\t{}\t{}", dateStamp, previousHour, hourlySum80m, hourlySum60m, hourlySum30m);
			measuredWindDataList.add(new MeasuredWindData(dateStamp, previousHour, (10*hourlySum80m)/cnt, (10*hourlySum60m)/cnt, (10*hourlySum30m)/cnt));	
			
			bufferedReader.close();
		} catch(Exception e) {
			logger.error("nie znaleziono pliku {}", e);
		}
		
		measuredWindDataMap.put(fileWithWindData, measuredWindDataList);
	}
	
	/**
	 * Show content of measuredWindDataMap
	 * Method created for checking how data was placed into a map
	 */
	public void showData(){

		long startTime =System.currentTimeMillis();
		measuredWindDataMap.forEach((k,v) -> { 	
												double r = v.stream().collect(Collectors.averagingInt(d -> d.getvMean80m()));
												
												System.out.println(k + " œrednia 80m: " + r);
												//v.stream().collect(Collectors.averagingInt(coll)	
												});
		measuredWindDataMap.forEach((k, v) -> { String keyMap = k;
												v.forEach(p -> logger.info("{} : {}\t{}\t{}\t{}\t{}", keyMap, p.getDateStamp(), p.getHour(), p.getvMean30m(), p.getvMean60m(), p.getvMean80m()));
												logger.info("plik: {}", k);
												} );
		long total = System.currentTimeMillis() - startTime;
	}
	
	/**
	 * TODO finih forecasting Vmean to 100m. Had a problem because of wrong values of parameters
	 * y had value -97433. Check if formula was correctly transformed. 
	 */
	public double find100mSpeed(double x80, double y80, double x60, double y60, double x30, double y30){
		double a, b, c;
		double z, p;
		z = (y30+y80*(x30+x60-x80)-y60*(x30-x80+x60));
		p = (x30*(x60-x80)+x60*x60*(x80-x30)+x80*x80*(x30-x60));
		a = z / p;
//		a = (y30+y80*(x30+x60-x80)-y60*(x30-x80+x60))/(x30*(x60-x80)+x60*x60*(x80-x30)+x80*x80*(x30-x60));
		b = (y60-y80-a*(x60*x60-x80*x80))/(x60-x80);
		c = y80-a*x80*x80-b*x80;
//		logger.info("Wsp. a={}, b={}, c={}, z={}, p={}",a, b, c, z, p);	
		
		double y=0;
		while(y<100){
			x80+=0.05;
			y=a*x80*x80 + b*x80 + c;
			System.out.println(y);
		}
//		logger.info("na 100m Vmean = {}", x80);
		return x80;
	}
	
	
	/**
	 * Method for getting generated power base on measurement and selected powerCurve
	 * @param temmpPowerCurve - power curve for WTG chosen by user
	 * @param measuredWindData - this parameter is coming from object and keeps measured wind data
	 * @return finalGeneratedPower - where key is file name with windData, and to this map is connected map with generated electricity for each wind speed
	 */
	public Map<String, Map<Double, Integer>> getGeneratedPower(Map<Double, Integer> tempPowerCurveMap, double shearFactor){
		measuredWindDataMap.forEach((k,v) -> { 								
			finalGeneratedPower.put(k, calculateProduction(v, tempPowerCurveMap, shearFactor));								
			});
	return finalGeneratedPower;
	}
	
	/*
	 * Method to calculate generated electricity in certain time period
	 * To get real mean wind speed with correct precision mean wind speed has to be divided by 100 - like using scale factor 0.01
	 * but to get more precise results tempPower curve has windSpeed precision precision of 0.1 m/s and therefore double dividing by 10 was used
	 * @param measuredData - measured data in certain time period
	 * @param temmpPowerCurve - power curve for WTG chosen by user
	 * @return generatedPower - map with result of generated electricity 
	 */
	public Map<Double, Integer> calculateProduction(List<MeasuredWindData> measuredData, Map<Double, Integer> tempPowerCurveMap, double shearFactor){
		Map<Double, Integer> generatedPower = new TreeMap<Double, Integer>();
		measuredData.forEach(p -> {
		
			int vMean = (int) ((p.getvMean80m()*shearFactor)/10);

			double vMeanSimple =vMean / 10;	//	k /100;
			
			double vMeanExtended = vMean / 10.0;
	
//			System.out.println("simple=" + vMeanSimple+ ", extended=" + vMeanExtended);
			
			if(vMeanSimple>25){
				vMeanSimple = 0;
				vMeanExtended = 0;
			}
			if(generatedPower.get(vMeanSimple) == null) {
				generatedPower.put(vMeanSimple, tempPowerCurveMap.get(vMeanExtended));
			} else {
				generatedPower.put(vMeanSimple, generatedPower.get(vMeanSimple)+tempPowerCurveMap.get(vMeanExtended));
			}
		}); 
		
//		generatedPower.forEach((k,v) -> System.out.println(k + " - " + v));
		
		return generatedPower;		
	}
	
	
	/**
	 * Method to predict mean wind speed at certain hub height
	 * To predict mean wind speed at certain hub height there are some possible solutions but from complexity and accuracy 
	 * point of view this one is the easiest.
	 * @param hubHeight - height where mean wind speed should be predicted
	 * @return shearFactor - parameter which after multiply by mean wind speed at 80m will give predicted wind speed at certain height
	 */
	
	public double calculateWindShareFactor(double hubHeight){
	
		List<Double> vMean60mList = new ArrayList<Double>();
		List<Double> vMean80mList = new ArrayList<Double>();
		
		measuredWindDataMap.forEach((k,v) -> {
			vMean60mList.add(v.stream().collect(Collectors.averagingDouble(t -> t.getvMean60m())));
			vMean80mList.add(v.stream().collect(Collectors.averagingDouble(t -> t.getvMean80m())));	
		});
		
		double vMean60m = vMean60mList.stream().collect(Collectors.averagingDouble(Double::doubleValue))/100;
		double vMean80m = vMean80mList.stream().collect(Collectors.averagingDouble(Double::doubleValue))/100;
		
		double shearExponent = Math.log10(vMean80m/vMean60m) / Math.log10(80.0/60.0);
		double shearFactor = Math.pow((hubHeight / 80.0), shearExponent);

		double searchedWindSpeed = vMean80m * Math.pow((hubHeight / 80.0), shearExponent);
		System.out.println("wind speed at " + hubHeight + " = " + searchedWindSpeed + " [m/s]  shearFactor=" + shearFactor );
		
		return shearFactor;
	}
	

}
