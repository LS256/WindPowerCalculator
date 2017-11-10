package pl.codeforfun;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @author LS256
 * Class responsible for connection with data base - getting and putting data into data base
 */

public class DBaccess {
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

//	Data base access paramenters for database located on localhost
	private static final String DB_ADDRESS = "jdbc:mysql://localhost:3306/res_wind?autoReconnect=true&useSSL=false";
	private static final String USER = "LS256";
	private static final String PASSWORD = "12345";
	
	

	

	public void readDataBase(String query) throws Exception {
		try{
			 Class.forName("com.mysql.jdbc.Driver");
			 connection =  DriverManager.getConnection(DB_ADDRESS, USER, PASSWORD);
	
			 statement = connection.createStatement();
			 resultSet = statement.executeQuery(query);		 
		} catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * method to get Wind Turbines Generators list from data base
	 * @return wtgList - list with turbines available in data base in table wtg
	 * @throws Exception
	 */
	public List<DBwtg> selectWTG() throws Exception {
		List<DBwtg> wtgList = new LinkedList<DBwtg>();
		try {
			readDataBase("SELECT id, wtg_type, wtg_power, rotor_diameter from wtg");
			int id, wtgPower;
			String wtgType;
			Double rotorDiameter;
			
				 while(resultSet.next()){
					 id = resultSet.getInt(1);
					 wtgType = resultSet.getString(2);
					 wtgPower = resultSet.getInt(3);
					 rotorDiameter = resultSet.getDouble(4);
					 wtgList.add(new DBwtg(id, wtgType, wtgPower, rotorDiameter));				 
				 }
		} catch(Exception e){
			throw e;
		} finally {
			connection.close();
		}
		return wtgList;
	} 
	
	/** 
	 * Method to get list with available WTGs powerMode
	 * @param wtgID - wtg id in dataBase
	 * @return  tempWtgPowerModeList -list with description of power mode and it's noise emision level
	 * @throws Exception 
	 */
	public List<String> getPowerMode(int ewID) throws Exception {

		List<String> tempWtgPowerModeList = new ArrayList<String>();
		try {
			readDataBase("SELECT DISTINCT mode_description, ew_noise FROM power_curve WHERE ew_id="+ewID);
			
			String powerModeAndNoise="";
		
				 while(resultSet.next()){
					 powerModeAndNoise = resultSet.getString(1)+" "+resultSet.getString(2)+" dB";
					 tempWtgPowerModeList.add(powerModeAndNoise); 			 
				 }
		} catch(Exception e){
			throw e;
		} finally {
			connection.close();
		}
		
		return tempWtgPowerModeList;
	}
	
	/**
	 * method to get nominal power of wind turbine chosen in wtgList ComboBox
	 * @param ew_id - id of turbine in data base
	 * @return nominalPower - nominal power of wind turbine in kW stored in DB
	 */
	public int getNominalWtgPower(int ew_id) throws Exception{
		int nominalPower=0;
		try {
			readDataBase("SELECT wtg_power FROM wtg WHERE id="+ew_id);
			
				 while(resultSet.next()){
					 nominalPower = resultSet.getInt(1);	 
				 }
		} catch(Exception e){
			throw e;
		} finally {
			connection.close();
		}
		return nominalPower;
	}
	
	/**
	 * method to get rotor diameter of chosen wind turbine
	 * @param 
	 * @return 
	 */
	public String getRotorDiameter(String wtgDescription) throws Exception{
		String rotorDiameter ="";
		try {
			readDataBase("SELECT rotor_diameter FROM wtg WHERE wtg_type='"+wtgDescription+"'");
			
				 while(resultSet.next()){
					 rotorDiameter = resultSet.getString(1);	 
				 }
		} catch(Exception e){
			throw e;
		} finally {
			connection.close();
		}
		return rotorDiameter;
	}
	
	
	
	/** 
	 * Method to get parameters of power curve described by windTurbine ID and it's mode description
	 * @param wtgID - wtg id in dataBase
	 * @param modeDesription - wtg power mode in data base
	 * @return powerCurveMap - map with power curve for asked wind turbine
	 * @throws Exception 
	 */
	
	public Map<Double, Integer> selectPowerCurve(int ewID, String modeDescription) throws Exception {
		Map<Double, Integer> powerCurveMap = new TreeMap<Double, Integer>();
		try {
			readDataBase("SELECT wind_speed, generated_power FROM power_curve WHERE ew_id="+ewID+" AND mode_description='"+modeDescription+"' ORDER BY wind_speed");
			Double windSpeed; 
			int generatedPower;
			
			double previousWindSpeed = 0;
			int previousGeneratedPower = 0;
			int generatedPowerIncrease = 0;
			
				 while(resultSet.next()){
					 windSpeed = resultSet.getDouble(1);	
					 generatedPower = resultSet.getInt(2);	
					 powerCurveMap.put(windSpeed, generatedPower);		 
					 
					 
					 generatedPowerIncrease=(int) ((generatedPower-previousGeneratedPower)/10);
					 for(int i =1; i<10; i++){
						 previousWindSpeed = ((previousWindSpeed * 10 ) + 1 ) / 10;
						 previousGeneratedPower+= generatedPowerIncrease;		
						 powerCurveMap.put(previousWindSpeed, previousGeneratedPower);
					 }
					
					 previousWindSpeed = windSpeed;
					 previousGeneratedPower = generatedPower;
				 }
		} catch(Exception e){
			throw e;
		} finally {
			connection.close();
		}	
		return powerCurveMap;
	} 
	
	/** 
	 * Method to get list with available WTGs tower
	 * @param ewID - wtg id in dataBase
	 * @return  tempWtgTowerList -list with available tower height for chosen wind turbine
	 * @throws Exception 
	 */
	public List<String> getTowerHeight(int ewID) throws Exception {

		List<String> tempWtgTowerList = new ArrayList<String>();
		try {
			readDataBase("SELECT hub_height FROM wtg_tower WHERE ew_id="+ewID);
			
			String towerHeight="";
		
				 while(resultSet.next()){
					 towerHeight = resultSet.getString(1);
					 tempWtgTowerList.add(towerHeight); 			 
				 }
		} catch(Exception e){
			throw e;
		} finally {
			connection.close();
		}
		return tempWtgTowerList;
	}

}
