package pl.codeforfun;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class PanelPower extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	GridBagConstraints gbc = new GridBagConstraints();
	PdfGenerator pdfGenerator;
	RowFileAnalyzer rowFileAnalyzer = new RowFileAnalyzer();
	DBaccess dbAccess = new DBaccess();
	
	JComboBox<String> wtgList; 
	JComboBox<String> powerCurveList; 
	JComboBox<String> wtgTower;
	JTextArea resultsText;
	JTextArea analyzedFiles;
	JScrollPane analyzedFilesScroll;
	JScrollPane resultsTextScroll;
	JButton saveButton;
	JFileChooser fileChooser;
	JButton textEaser;
	JButton detailedChartCreator;
	JButton mainChartCreator;
	JButton pdfButton;
	JButton loadButton;
	JButton removeButton;
	JButton calculateButton;
	
	File file; 
	Map<Double, Integer> totalGeneratedPower; 
	Map<String, Map<Double, Integer>> totalGeneratedPowerChart = new TreeMap<String, Map<Double, Integer>>();
	Map<String, Double> generatedPowerSummary = new TreeMap<String, Double>();
	Map<String, Double> fullLoadHoursSummary = new TreeMap<String, Double>();
	
	PanelPower(){
		setLayout(new GridBagLayout());
		gbc.insets = new Insets(5,5,5,5);
		
		//	create comboBox list with WTG written in dataBase
		JLabel wtgListLabel = new JLabel("Turbine type");
		setPosition(0, 0, 2, 1);
		add(wtgListLabel, gbc);
		
		wtgList = new JComboBox<String>();
		try {
			dbAccess.selectWTG().forEach(p -> wtgList.addItem(p.getWtgType()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		wtgList.addActionListener(p -> {
			buttonsDeactivation();
			powerCurveList.removeAllItems();
			wtgTower.removeAllItems();
			try {
				dbAccess.getPowerMode(wtgList.getSelectedIndex()+1).forEach(r -> powerCurveList.addItem(r));
				dbAccess.getTowerHeight(wtgList.getSelectedIndex()+1).forEach(r -> wtgTower.addItem(r));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		setPosition(0, 1, 2, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(wtgList, gbc);
		
		// create comboBox list with power curve parameters depended on chosen WTG
		JLabel powerCurveListLabel = new JLabel("Noie emision");
		setPosition(2, 0, 2, 1);
		add(powerCurveListLabel, gbc);
		
		powerCurveList = new JComboBox<String>();
		try {
			dbAccess.getPowerMode(wtgList.getSelectedIndex()+1).forEach(r -> powerCurveList.addItem(r));
		} catch (Exception e) {
			e.printStackTrace();
		}
		powerCurveList.addActionListener(p -> buttonsDeactivation());
		setPosition(2, 1, 2, 1);
		add(powerCurveList, gbc);
		
		//	Create comboBox list with tower heights available for chosen windTurbine
		JLabel wtgTowerLabel = new JLabel("Hub height");
		setPosition(4, 0, 1, 1);
		add(wtgTowerLabel, gbc);
		wtgTower = new JComboBox<String>();
		try {
			dbAccess.getTowerHeight(wtgList.getSelectedIndex()+1).forEach(r -> wtgTower.addItem(r));
		} catch (Exception e) {
			e.printStackTrace();
		}
		wtgTower.addActionListener(p->buttonsDeactivation());
		setPosition(4, 1, 1, 1);
		add(wtgTower, gbc);	
		
		gbc.insets = new Insets(5,5,5,5);
		
		// create text area where we will see added files with measured wind data
		JLabel analyzedFilesLabel = new JLabel("Analyzed files");
		setPosition(0,2,4,1);
		add(analyzedFilesLabel, gbc);
		
		analyzedFiles = new JTextArea(5,5);
		analyzedFiles.setEditable(false);
		analyzedFilesScroll = new JScrollPane(analyzedFiles);
		setPosition(0, 3, 4, 4);
		gbc.fill = GridBagConstraints.BOTH;
		add(analyzedFilesScroll, gbc);
		
		//	create button for loading analyzed files
		loadButton = new JButton("Load");
		Map<String, String> loadedFiles = new TreeMap<String, String>();
		loadButton.addActionListener(p->{
	
			fileChooser = new JFileChooser();
			if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
				calculateButton.setEnabled(true);
				file = fileChooser.getSelectedFile();
				if(loadedFiles.get(file.getName()) == null){
					analyzedFiles.append(file.getName()+"\n");
					loadedFiles.put(file.getName(), file.getPath());
				} else {
					JOptionPane.showMessageDialog(this, "plik o nazwie: "+file.getName()+" ju� znajduje si� na li�cie.");
				}
			}
		});
		setPosition(4, 3, 1, 1);
		add(loadButton, gbc);
		
		//	create button for removing last loaded file
		removeButton = new JButton("Remove");
		removeButton.addActionListener(p -> {
				String[] fileToRemove = analyzedFiles.getText().split("\n");
				loadedFiles.remove(fileToRemove[fileToRemove.length-1]);
				analyzedFiles.setText("");
				loadedFiles.forEach((k, v) -> analyzedFiles.append(k+"\n"));
		});
		setPosition(4, 4, 1, 1);
		add(removeButton, gbc);
		
		//	create button responsible for making calculation
		calculateButton = new JButton("Calculate");
		calculateButton.setEnabled(false);
		calculateButton.addActionListener(p -> {
			buttonsActivation();
			Map<Double, Integer> powerCurve;
			totalGeneratedPower = new TreeMap<Double, Integer>();
			Map<String, Map<Double, Integer>> calculatedPower = new TreeMap<String, Map<Double, Integer>>();
						
			String[] powerCurveParameters = powerCurveList.getSelectedItem().toString().split(" ");
		
			loadedFiles.forEach((k,v) -> rowFileAnalyzer.fileReader(v));
			
			double searchedHeight = Double.valueOf(wtgTower.getSelectedItem().toString());
			double searchedWindSpeed = rowFileAnalyzer.calculateWindSpeed(searchedHeight);
					
			try {
				//	power curve map keeps parameters of power curve for chosen windTurbine
				powerCurve = dbAccess.selectPowerCurve(wtgList.getSelectedIndex()+1, powerCurveParameters[0]);			
				
				calculatedPower = rowFileAnalyzer.getGeneratedPower(powerCurve, searchedWindSpeed);
				
				resultsText.append("Results generated for: " + wtgList.getSelectedItem()+", " + powerCurveList.getSelectedItem()+"\n");
				
				//	Display in resultsText title of columns with generated power
				for(int i = 0 ; i<47; i++) resultsText.append(" ");
				powerCurve.forEach((k, v) -> {
					if(k%1 == 0 & k>=2){
						resultsText.append("\t" + k + "[m/s]");
					}
				});
 				resultsText.append("\n");
			
			} catch (Exception e) {
				e.printStackTrace();
			}

			//	Display generated power
			calculatedPower.forEach((k,v) -> {
				File file = new File(k);
				String fileName = file.getName();
				resultsText.append(fileName+"\t");
				v.forEach((u,w) -> {
					if((totalGeneratedPower.get(u) == null) & (u % 1 == 0)){
						totalGeneratedPower.put(u, w);
					} else {
						totalGeneratedPower.put(u, w+totalGeneratedPower.get(u));
					}		
					if(u>=2 & u<=25){			
						resultsText.append(convertkWhToMWh(w, 100)+"\t");
					} 
				});
				resultsText.append("\n");
			});
			
			resultsText.append("\nGenerated electricity MWh");
			for(int i = 0 ; i<10; i++) resultsText.append(" ");
			totalGeneratedPower.forEach((k, v) -> {
				if(k>=2 & k<=25){

					double generatedPower = convertkWhToMWh(v,100);
					resultsText.append("\t"+generatedPower);
				}
			});
			resultsText.append("\n");
			
			//	Display full load hours
			resultsText.append("Full Load Hours [MWh/y]");
			for(int i = 0 ; i<10; i++) resultsText.append(" ");

			totalGeneratedPower.forEach((k, v) -> {
				if(k>=2 & k<=25){
					double fullLoadHours = convertkWhToMWhFullLoad(v, 100);
					resultsText.append("\t"+fullLoadHours);		
				}
			});
			resultsText.append("\n\n");
						
			double nominalPower = 0;
			try{
			nominalPower = dbAccess.getNominalWtgPower(wtgList.getSelectedIndex()+1)/1000.0;
			} catch(Exception e){
				e.printStackTrace();
			}
			final String keyDescription = wtgList.getSelectedItem()+" " + powerCurveList.getSelectedItem()+ " " + wtgTower.getSelectedItem()+" m "+nominalPower;

			totalGeneratedPowerChart.put(keyDescription, totalGeneratedPower);
		
			//	Summarize total gnerated energy and full load hours
			int generatedPower = totalGeneratedPower.values().stream().mapToInt(r -> r.intValue()).sum();
			double fullLoadHours = generatedPower/ nominalPower; 
			
			generatedPowerSummary.put(keyDescription, convertkWhToMWh(generatedPower, 100));
			fullLoadHoursSummary.put(keyDescription, convertkWhToMWh(fullLoadHours, 100));			
			
		});
		
		setPosition(0, 7, 5, 1);
		add(calculateButton, gbc);
		
		// create text area where after calculation we will put results
		JLabel resultTextLabel = new JLabel("Calculation results");
		setPosition(0, 8, 1, 1);
		add(resultTextLabel, gbc);
		
		resultsText = new JTextArea(10,10);
		resultsTextScroll = new JScrollPane(resultsText);
		setPosition(0, 9, 5, 5);
		add(resultsTextScroll, gbc);
		
		//	create button for saving data from resultsText
		saveButton = new JButton("Save");
		saveButton.setEnabled(false);
		saveButton.addActionListener(p -> {
			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("C://Users/"));
			if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
				try {
					FileWriter fileWriter = new FileWriter(fileChooser.getSelectedFile()+".txt");
					BufferedWriter bufferedReader = new BufferedWriter(fileWriter);
					bufferedReader.write(resultsText.getText());
					bufferedReader.close();
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
		});
		setPosition(0, 18, 1, 1);
		add(saveButton, gbc);
		
		//	remove actual content of resultsText
		textEaser = new JButton("Clean");
		textEaser.setEnabled(false);
		textEaser.addActionListener(p->resultsText.setText(""));
		setPosition(1, 18, 1, 1);
		add(textEaser, gbc);

		// Create chart with production results for every measured wind
		detailedChartCreator = new JButton("Details Graph");
		detailedChartCreator.setEnabled(false);
		detailedChartCreator.addActionListener(p -> {

			
			MyChartPanel myChartPanel = new MyChartPanel("generated power", totalGeneratedPowerChart);			
			myChartPanel.pack();
			myChartPanel.setVisible(true);
		});
		setPosition(2, 18, 1, 1);
		add(detailedChartCreator, gbc);
		
		//	Create chart with summary of production for chosen turbine
		mainChartCreator = new JButton("Final Graph");
		mainChartCreator.setEnabled(false);
		mainChartCreator.addActionListener(p->{
			
			MyChartPanel myChartPanel = new MyChartPanel(totalGeneratedPowerChart, 1);
			
			myChartPanel.pack();
			myChartPanel.setVisible(true);
			
		});
		setPosition(3, 18, 1, 1);
		add(mainChartCreator, gbc);
			
		
/*
 * Method for generating report in PDF format
 * 
 */
		pdfButton = new JButton("PDF");		
		pdfButton.setEnabled(false);
		pdfButton.addActionListener(action -> {
			String getAnalyzedFiles = analyzedFiles.getText();
			pdfGenerator = new PdfGenerator(totalGeneratedPowerChart, getAnalyzedFiles, rowFileAnalyzer);
		});
		setPosition(4,18,1,1);
		add(pdfButton, gbc);	
	}
	
	/**
	 * Method to help setup positions of elements in panel 
	 * @param xPos - column position
	 * @param yPos - row position
	 * @param xWidth - how many columns element will get
	 * @param yHeight - how many rows element will get
	 */
	public void setPosition(int xPos, int yPos, int xWidth, int yHeight){
		gbc.gridx = xPos;
		gbc.gridy = yPos;
		gbc.gridwidth = xWidth;
		gbc.gridheight = yHeight;
	}
	
	/**
	 * Method to convert generated energy from kWh to general unit of MWh
	 * @param kWh - amount of generated kWh
	 * @param precision - how many digits after comma should be included in result
	 * @return doubleMWh - converted energy in MWh  
	 */
	public double convertkWhToMWhFullLoad(int kWh, int precision){
		double doubleMWh = (double) kWh;
		try {
			doubleMWh = precision*(doubleMWh/dbAccess.getNominalWtgPower(wtgList.getSelectedIndex()+1));
			int intMWh = (int) doubleMWh/1;
			doubleMWh = (double) intMWh / precision;	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doubleMWh;
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
	
	
	public double convertkWhToMWh(double doubleMWh, int precision){
		doubleMWh = precision*(doubleMWh/1000);
		int intMWh = (int) doubleMWh/1;
		doubleMWh = (double) intMWh / precision;		
		return doubleMWh;
	}
	
	/**
	 * Method responsible for buttons activation
	 */
	public void buttonsActivation(){
		saveButton.setEnabled(true);
		textEaser.setEnabled(true);
		mainChartCreator.setEnabled(true);
		detailedChartCreator.setEnabled(true);
		pdfButton.setEnabled(true);
	}
	
	/**
	 * Method responsible for buttons deactivation
	 */
	public void buttonsDeactivation(){
		saveButton.setEnabled(false);
		textEaser.setEnabled(false);
		mainChartCreator.setEnabled(false);
		detailedChartCreator.setEnabled(false);
		pdfButton.setEnabled(false);
	}
	
}
