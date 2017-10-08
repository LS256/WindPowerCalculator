package pl.codeforfun;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RES {

	
	static final Logger logger = LogManager.getLogger(RES.class.getName());
	
	
	public static void main(String[] args) {
		
		DBaccess dataBaseAccess = new DBaccess();
		
		logger.info("Welcome in RES world.. - don't happy be worry");
		
		List<NoiseEmiter> noiseEmitersList = new ArrayList<NoiseEmiter>();
		List<NoiseReceiver> noiseReceiversList = new ArrayList<NoiseReceiver>();
		
//		noiseEmitersList.add(new NoiseEmiter(new Coordinates(401775, 556807),105));
//		noiseEmitersList.add(new NoiseEmiter(new Coordinates(401759, 558160),105));
		
//		noiseReceiversList.add(new NoiseReceiver(new Coordinates(402267, 558030)));
		
//		RowFileAnalyzer rowFileAnalyzer = new RowFileAnalyzer();
//		rowFileAnalyzer.fileReader("2011_01_80m.row");
//		rowFileAnalyzer.fileReader("2011_02_80m.row");
//
////		rowFileAnalyzer.showData();
//		try {
//			Map<Double, Integer> tempPowerCurve = new DBaccess().selectPowerCurve(1, "level_0");
////			List<DBpowerCurve> tempPowerCurve = new DataBaseAccess().selectedPowerCurve(1, "level_0");
//			rowFileAnalyzer.showGeneratedPower(tempPowerCurve);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		
//		wspDomu
		noiseReceiversList.add(new NoiseReceiver(new Coordinates(416655, 572872)));
		
//		wsp punktow oddalonych od domu
		noiseEmitersList.add(new NoiseEmiter(new Coordinates(416597, 573025),105));
		noiseEmitersList.add(new NoiseEmiter(new Coordinates(416515, 573106),105));
		noiseEmitersList.add(new NoiseEmiter(new Coordinates(416538, 573158),105));
		noiseEmitersList.add(new NoiseEmiter(new Coordinates(416551, 573309),105));
		noiseEmitersList.add(new NoiseEmiter(new Coordinates(416612, 573348),105));
		noiseEmitersList.add(new NoiseEmiter(new Coordinates(416611, 573358),105));
		noiseEmitersList.add(new NoiseEmiter(new Coordinates(416607, 573377),105));
		noiseEmitersList.add(new NoiseEmiter(new Coordinates(416600, 573388),105));
		
		
		
		
		
		
		BiFunction<Integer, Integer, Double> test = (x, y) -> { 
			return Math.sqrt(x*x+y*y);
			
			 };
			 
		 
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						JFrame mainWindow = new MainWindow();
					}
				});
			 
//	show positions on emiter list		
//			 noiseEmitersList.forEach(p -> logger.info("x:{}, y:{}, noise:{}dB", p.getCoordinates().getXCoordinate(), p.getCoordinates().getYCoordinate(), p.getNoiseEmission()));

//		oblciz odleglosc za pomoca lambdy
//		logger.info("wynik to {}", test.apply(5, 3)); 
		
		// cod where we find distance between noise receiver and noise emiter 
//		for(NoiseReceiver noiseReceiver : noiseReceiversList){
//			for(NoiseEmiter noiseEmiter : noiseEmitersList){
//				int a = noiseReceiver.getCoordinates().getXCoordinate() - noiseEmiter.getCoordinates().getXCoordinate();
//				int b = noiseReceiver.getCoordinates().getYCoordinate() - noiseEmiter.getCoordinates().getYCoordinate();
//				int c = (int) Math.sqrt(a*a+b*b);
//				logger.info("Wyliczona odleg³oœæ to {}m, a:{}, b:{}", c, a, b);
//			}
//		}
		
		// review of existing data base with wtg
	
//		try {
//			List<DBwtg> wtgList = dataBaseAccess.selectWTG();
//			wtgList.forEach((p) -> System.out.println(p.toString()));
//		} catch (Exception e) {
//			logger.error("Problem occoured in connection to data base. {}", e);
//		}
			 
		
		//	Read power curve from data base
//		try{
//			Map<Double, Integer> powerCurveMap = dataBaseAccess.selectPowerCurve();
//			powerCurveMap.forEach((k, v) -> logger.info("{}, {}", k, v)); 
//			
//		} catch (Exception e) {
//			logger.error("Problem occoured in connection to data base. {}", e);
//		}
		
	
		
	
		
	}
	
	

	
	


}
