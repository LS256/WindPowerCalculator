package pl.codeforfun;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JDialog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities; 


/** 
 * Class for calculated results summary and visualization
 * @author LS256
 */
public class MyChartPanel extends JDialog {

	private static final long serialVersionUID = 1L;
	PanelPower panelPower = new PanelPower();
	
	//	Constructor exists only for java requirements
	protected MyChartPanel(){	
	}
	
	/**
	 * Constructor for preparing chart with detailed yield for every wind speed
	 * @param chartTitle - tile to chart
	 * @param generatedPowerMap - map with generated power for each chosen wind generator
	 */
	MyChartPanel(String chartTitle, Map<String, Map<Double, Integer>> generatedPowerMap){
		JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Mean Wind Speed [m/s]", "Generated Power [MWh]", createDataset(generatedPowerMap), PlotOrientation.VERTICAL, true, true, false);
		barChart.getPlot().setBackgroundPaint(Color.WHITE);
		 
	    ChartPanel chartPanel = new ChartPanel(barChart);  
	    chartPanel.setPreferredSize(new java.awt.Dimension( 1000 , 367 ) );        		    
	    add(chartPanel);
	}
	
	/**
	 * Constructor for preparing chart with summary of main parameters like yield and full load hours
	 * @param chartTitle - tile to chart
	 * @param generatedPowerMap - map with generated power for each chosen wind generator
	 */
	MyChartPanel(Map<String, Map<Double, Integer>> totalGeneratedPowerChart, double nominalPower){
	
		JFreeChart barChart = ChartFactory.createBarChart("Summary of main results", "Chosen wind turbine", "Generated Power [MWh]", createMainDataset(totalGeneratedPowerChart, nominalPower), PlotOrientation.VERTICAL, true, true, false);
		barChart.getPlot().setBackgroundPaint(Color.WHITE);
		 
	    CategoryPlot plot = barChart.getCategoryPlot();
        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		ChartPanel chartPanel = new ChartPanel(barChart);      
	    chartPanel.setPreferredSize(new java.awt.Dimension( 500 , 367 ) );        	   
	    add(chartPanel);
	}
	
	/**
	 * Constructor for preparing chart in jpg file with detailed yield for every wind speed
	 * @param chartTitle - tile to chart
	 * @param generatedPowerMap - map with generated power for each chosen wind generator
	 */
	public void detailedChartJpg(String chartTitle, Map<String, Map<Double, Integer>> generatedPowerMap){
		JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Mean Wind Speed [m/s]", "Generated Power [MWh]", createDataset(generatedPowerMap), PlotOrientation.VERTICAL, true, true, false);
	    barChart.getPlot().setBackgroundPaint(Color.WHITE);
	    File jpgChart = new File("details.jpg");
	    try {
			ChartUtilities.saveChartAsJPEG(jpgChart,  barChart,  800,  450);
	    } catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor for preparing chart in jpg file with summary of main parameters like yield and full load hours
	 * @param nominalPower - variable only for constructor overloading
	 * @param generatedPowerMap - map with generated power for each chosen wind generator
	 */
	public void mainChartJpg(Map<String, Map<Double, Integer>> totalGeneratedPowerChart, double nominalPower){
	
		JFreeChart barChart = ChartFactory.createBarChart("Summary of main results", "Chosen wind turbine", "Generated Power [MWh]", createMainDataset(totalGeneratedPowerChart, nominalPower), PlotOrientation.VERTICAL, true, true, false);
		barChart.getPlot().setBackgroundPaint(Color.WHITE);
		 
	    File jpgChart = new File("main.jpg");
	    try {
			ChartUtilities.saveChartAsJPEG(jpgChart,  barChart,  800,  450);
	    } catch (IOException e) {
			e.printStackTrace();
		}
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
					if(t == null){
						tempT = 0;
					}
					else {
						tempT = t/1000;	
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