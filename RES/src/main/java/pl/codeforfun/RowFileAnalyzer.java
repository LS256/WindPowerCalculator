package pl.codeforfun;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

/**
 * class where we can analyze wind data from file
 * @author LS256
 */
public class RowFileAnalyzer {
	
//	static final Logger logger = LogManager.getLogger(RES.class.getName());
//	List<MeasuredWindData> measuredWindData = new ArrayList<MeasuredWindData>();
	Map<String, List<MeasuredWindData>> measuredWindDataMap = new TreeMap<String, List<MeasuredWindData>>();
	Map<String, Map<Double, Integer>> finalGeneratedPower = new TreeMap<String, Map<Double, Integer>>();
	
	// eventually remove
	Map<String, Map<Double, Integer>> finalGeneratedPowerD = new TreeMap<String, Map<Double, Integer>>();

	
	
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
			String fileContent = "";
			String previousHour = "00";
			String  dateStamp = "";
			int hourlySum80m = 0;
			int hourlySum60m = 0;
			int hourlySum30m = 0;
			int cnt = 0;
	
			
			FileInputStream fileInputStream = new FileInputStream(fileWithWindData);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
	
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
						measuredWindDataList.add(new MeasuredWindData(dateStamp, previousHour, (10*hourlySum80m)/cnt, (10*hourlySum60m)/cnt, (10*hourlySum30m)/cnt));	
						hourlySum80m = Integer.valueOf(analyzedRow[2]);
						hourlySum60m = Integer.valueOf(analyzedRow[5]);
						hourlySum30m = Integer.valueOf(analyzedRow[8]);
						cnt = 1;
					}
					previousHour =  tempHourTab[0];
				}	
			}
			measuredWindDataList.add(new MeasuredWindData(dateStamp, previousHour, (10*hourlySum80m)/cnt, (10*hourlySum60m)/cnt, (10*hourlySum30m)/cnt));	
			
			bufferedReader.close();
		} catch(Exception e) {
//			logger.error("nie znaleziono pliku {}", e);
		}
		
		measuredWindDataMap.put(fileWithWindData, measuredWindDataList);
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
	
		return generatedPower;		
	}
	
	/**
	 * Method to convert generated energy from kWh to general unit of MWh
	 * @param kWh - amount of generated kWh
	 * @param precision - how many digits after comma should be included in result
	 * @return doubleMWh - converted energy in MWh  
	 */
	public double convertkWhToMWh(int kWh, int precision){
		double doubleMWh = (double) kWh;
		doubleMWh = precision*(doubleMWh/1000);
		int intMWh = (int) doubleMWh/1;
		doubleMWh = (double) intMWh / precision;	

		return doubleMWh;
	}
	
	/**
	 * Method to convert generated energy from kWh to general unit of MWh. 
	 * Method overloaded for double input parameter
	 * @param kWh - amount of generated kWh
	 * @param precision - how many digits after comma should be included in result
	 * @return doubleMWh - converted energy in MWh  
	 */
	
	public double convertkWhToMWh(double kWh, int precision){
		double doubleMWh = kWh;
		doubleMWh = precision*(doubleMWh/1000);
		int intMWh = (int) doubleMWh/1;
		doubleMWh = (double) intMWh / precision;	

		return doubleMWh;
	}
		
	/**
	 * Method to predict shear exponent at certain hub height
	 * @param hubHeight - height where mean wind speed should be predicted
	 * @return searchedWubdSpeed - mean wind speed calculated at required hub height
	 */
	
	public double getMeanWindSpeed(double hubHeight){
	
		List<Double> vMean60mList = new ArrayList<Double>();
		List<Double> vMean80mList = new ArrayList<Double>();
		
		measuredWindDataMap.forEach((k,v) -> {
			vMean60mList.add(v.stream().collect(Collectors.averagingDouble(t -> t.getvMean60m())));
			vMean80mList.add(v.stream().collect(Collectors.averagingDouble(t -> t.getvMean80m())));	
		});
		
		double vMean60m = vMean60mList.stream().collect(Collectors.averagingDouble(Double::doubleValue))/100;
		double vMean80m = vMean80mList.stream().collect(Collectors.averagingDouble(Double::doubleValue))/100;
		
		double shearExponent = Math.log10(vMean80m/vMean60m) / Math.log10(80.0/60.0);

		double searchedWindSpeed = vMean80m * Math.pow((hubHeight / 80.0), shearExponent);
		
		return searchedWindSpeed;
	}
	
	
	/**
	 * Method to predict mean wind speed at certain hub height
	 * To predict mean wind speed at certain hub height there are some possible solutions but from complexity and accuracy 
	 * point of view this one is the easiest.
	 * @param hubHeight - height where mean wind speed should be predicted
	 * @return shearFactor - parameter which after multiply by mean wind speed at 80m will give predicted wind speed at certain height
	 */
	
	public double calculateWindSpeed(double hubHeight){
	
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
		
		return shearFactor;
	}
	
	/**
	 * Method to calculate hours of loaded measurement
	 * @return totalMeasuredHours - hours of measured wind speed loaded into the program
	 */
	
	public int getMeasuredHours() {
		 List<Integer> monthlyMeasuredHours = new ArrayList<Integer>();

		 measuredWindDataMap.forEach((k, v) -> {
		 	monthlyMeasuredHours.add(v.size());
		 });
		 
		 int totalMeasuredHours = monthlyMeasuredHours.stream().collect(Collectors.summingInt(Integer::intValue));
		 return totalMeasuredHours;
	}
	
}
