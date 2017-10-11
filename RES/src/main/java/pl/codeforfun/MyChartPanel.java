package pl.codeforfun;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.ChartPanel; 



public class MyChartPanel extends JDialog {	//	ApplicationFrame {
	PanelPower panelPower = new PanelPower();
	
	MyChartPanel(String applicationTitle , String chartTitle, Map<String, Map<Double, Integer>> generatedPowerMap){
//		super(applicationTitle);
		JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Mean Wind Speed [m/s]", "Generated Power [MWh]", createDataset(generatedPowerMap), PlotOrientation.VERTICAL, true, true, false);
	    ChartPanel chartPanel = new ChartPanel(barChart);      
	    chartPanel.setPreferredSize(new java.awt.Dimension( 1000 , 367 ) );        	
	    
	    JLabel test = new JLabel("Hello world by Adel Hello world by Adel Hello world by Adel Hello world by Adel");
	    chartPanel.add(test, BorderLayout.SOUTH);
	    add(chartPanel);
	    
	    

	    
	    
	    
	    this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
	    
//	    generatedPowerMap.forEach((k, v) -> System.out.println(k+", " + v));
	    
	}
	
	private CategoryDataset createDataset(Map<String, Map<Double, Integer>> tempGeneratedPowerMap){
	    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();  
//		panelPower = new PanelPower();
//		panelPower.totalGeneratedPower.forEach((k, v) -> {
//	//		dataset.addValue(v, "Full Load", "speed");
//			System.out.println(k+" - " + v);
//		});
//		   return dataset; 
	     
			final String description = "Generated Power";
			final String turbineType = "Gamesa G114";
					
			tempGeneratedPowerMap.forEach((k, v) ->{
				v.forEach((u, t) -> {
					System.out.println("rysuj : " + k + " - " + u + " - " + t);
					int tempT;
					if(t == null){
						tempT = 0;
					}
					else {
						tempT = t/1000;
					}
					dataset.addValue(tempT , k, u);
				});
				
			});
			
			
//			
//			tempGeneratedPowerMap.forEach((k,v) -> {
//				int tempV;
//				if(v == null){
//					tempV = 0;
//				}
//				else {
//					tempV = v/1000;
//				}
//				dataset.addValue(tempV , turbineType, k);
//			});
			
			
//		final String fiat = "FIAT";        
//	      final String audi = "AUDI";        
//	      final String ford = "FORD";        
//	      final String speed = "Speed";        
//	      final String millage = "Millage";        
//	      final String userrating = "User Rating";        
//	      final String safety = "safety";        
//	
//	      dataset.addValue( 1.0 , fiat , speed );        
//	      dataset.addValue( 3.0 , fiat , userrating );        
//	      dataset.addValue( 5.0 , fiat , millage ); 
//	      dataset.addValue( 5.0 , fiat , safety );           
//	      dataset.addValue( 5.0 , audi , speed );        
//	      dataset.addValue( 6.0 , audi , userrating );       
//	      dataset.addValue( 10.0 , audi , millage );        
//	      dataset.addValue( 4.0 , audi , safety );
//
//	      dataset.addValue( 4.0 , ford , speed );        
//	      dataset.addValue( 2.0 , ford , userrating );        
//	      dataset.addValue( 3.0 , ford , millage );        
//	      dataset.addValue( 6.0 , ford , safety );               

	      return dataset; 
	   }
}