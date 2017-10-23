package pl.codeforfun;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.swing.JDialog;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.ui.ApplicationFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities; 



public class MyChartPanel extends JDialog {	//	ApplicationFrame {
	PanelPower panelPower = new PanelPower();
	
	/**
	 * Constructor for preparing chart with detailed yield for every wind speed
	 * @param chartTitle - tile to chart
	 * @param generatedPowerMap - map with generated power for each chosen wind generator
	 */
	MyChartPanel(String chartTitle, Map<String, Map<Double, Integer>> generatedPowerMap){
		JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Mean Wind Speed [m/s]", "Generated Power [MWh]", createDataset(generatedPowerMap), PlotOrientation.VERTICAL, true, true, false);
 
	    
	    ChartPanel chartPanel = new ChartPanel(barChart);  
	    chartPanel.setPreferredSize(new java.awt.Dimension( 1000 , 367 ) );        		    
	    add(chartPanel);
	    this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
	}
	

	/**
	 * Constructor for preparing chart with summary of main parameters like yield and full load hours
	 * @param chartTitle - tile to chart
	 * @param generatedPowerMap - map with generated power for each chosen wind generator
	 */
	MyChartPanel(Map<String, Map<Double, Integer>> totalGeneratedPowerChart, double nominalPower){
	
		JFreeChart barChart = ChartFactory.createBarChart("Summary of main results", "Chosen wind turbine", "Generated Power [MWh]", createMainDataset(totalGeneratedPowerChart, nominalPower), PlotOrientation.VERTICAL, true, true, false);
	 
	    CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(0xEE, 0xEE, 0xFF));
        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
 
	
		ChartPanel chartPanel = new ChartPanel(barChart);      
	    chartPanel.setPreferredSize(new java.awt.Dimension( 500 , 367 ) );        	   
	    
	    add(chartPanel);
	    
	    this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);

// jpgChart = new File("jpgChart.jpg");
//	    try {
//			ChartUtilities.saveChartAsJPEG(jpgChart,  barChart,  300,  300);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	
	/**
	 * Create data set for chart with detailed information about generated power for each wind speed
	 * @param tempGeneratedPowerMap - map with generated power for each chosen wind generator
	 * @return dataset - data set with generated power for each chosen wind generator
	 */
	private CategoryDataset createDataset(Map<String, Map<Double, Integer>> tempGeneratedPowerMap){
	    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();  
			
			tempGeneratedPowerMap.forEach((k, v) ->{
				v.forEach((u, t) -> {
					int tempT;
//					int tempU;
					if(t == null){
						tempT = 0;
					}
					else {
						tempT = t/1000;
					
						System.out.println(t +" - " + u);
						dataset.addValue(tempT , k, u);
					}
				});
			});
	      return dataset; 
	   }
	
	/**
	 * Create data set for chart with Main information about generated power and full load hours for each wind speed
	 * @param tempGeneratedPowerMap - map with generated power for each chosen wind generator
	 * @param nominalPower - nominal power for chosen wind generator
	 * @return mainDataset - data set with generated power for each chosen wind generator
	 */	
	private CategoryDataset createMainDataset(Map<String, Map<Double, Integer>> totalGeneratedPowerChart, double nominalPower){
		  final DefaultCategoryDataset mainDataset = new DefaultCategoryDataset();
	
			totalGeneratedPowerChart.forEach((k,v) -> {
				String[] zet = k.split(" ");
				double nomin = Double.valueOf(zet[zet.length-1]);
				int powerSum = v.values().stream().collect(Collectors.summingInt(Integer::intValue))/1000;
				int fullLoadHours = (int) (powerSum/ nomin);	//(nominalPower));
				
				mainDataset.addValue(powerSum, "Generated Power", k);
				mainDataset.addValue(fullLoadHours, "Full Load hours", k);
			});
		  

		
		return mainDataset; 
	}
	

	
	
	
}