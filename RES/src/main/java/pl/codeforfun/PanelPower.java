package pl.codeforfun;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PanelPower extends JPanel{
	
	GridBagConstraints gbc = new GridBagConstraints();
	RowFileAnalyzer rowFileAnalyzer = new RowFileAnalyzer();
	DBaccess dbAccess = new DBaccess();
	JComboBox wtgList; 
	JComboBox powerCurveList; 
	JTextArea analyzedFiles;
	JScrollPane analyzedFilesScroll;
	JButton loadButton;
	JButton removeButton;
	JButton calculateButton;
	JTextArea resultsText;
	JScrollPane resultsTextScroll;
	JButton saveButton;
	JFileChooser fileChooser;
	JButton textEaser;
	
	PanelPower(){
		setLayout(new GridBagLayout());
		gbc.insets = new Insets(5,5,5,5);
		
		//	create comboBox list with WTG writen in dataBase
		wtgList = new JComboBox();
		try {
			dbAccess.selectWTG().forEach(p -> wtgList.addItem(p.getWtgType()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		wtgList.addActionListener(p -> {
			powerCurveList.removeAllItems();
			try {
				dbAccess.getPowerMode(wtgList.getSelectedIndex()+1).forEach(r -> powerCurveList.addItem(r));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		setPosition(0, 0, 2, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(wtgList, gbc);
		
		// create comboBox list with power curve parameters depends on choosen WTG
		powerCurveList = new JComboBox();
		try {
			dbAccess.getPowerMode(wtgList.getSelectedIndex()+1).forEach(r -> powerCurveList.addItem(r));
		} catch (Exception e) {
			e.printStackTrace();
		}
		setPosition(2, 0, 2, 1);
		add(powerCurveList, gbc);
		
		// create text area where we will see added files with measured wind data
		analyzedFiles = new JTextArea(5,5);
		analyzedFiles.setEditable(false);
		analyzedFilesScroll = new JScrollPane(analyzedFiles);
		setPosition(0, 1, 4, 4);
		gbc.fill = GridBagConstraints.BOTH;
		add(analyzedFilesScroll, gbc);
		
		//	create button for loading analyzed files
		loadButton = new JButton("Load");
		Map<String, String> loadedFiles = new TreeMap<String, String>();
		loadButton.addActionListener(p->{
			fileChooser = new JFileChooser();
			if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
				File file = fileChooser.getSelectedFile();
				if(loadedFiles.get(file.getName()) == null){
					analyzedFiles.append(file.getName()+"\n");
					loadedFiles.put(file.getName(), file.getPath());
				} else {
					JOptionPane.showMessageDialog(this, "plik o nazwie: "+file.getName()+" ju� znajduje si� na li�cie.");
				}
			}
		});
		setPosition(4, 1, 1, 1);
		add(loadButton, gbc);
		
		//	create button for removing last loaded file
		removeButton = new JButton("Remove");
		removeButton.addActionListener(p -> {
				String[] fileToRemove = analyzedFiles.getText().split("\n");
				loadedFiles.remove(fileToRemove[fileToRemove.length-1]);
				analyzedFiles.setText("");
				loadedFiles.forEach((k, v) -> analyzedFiles.append(k+"\n"));
		});
		setPosition(4, 3, 1, 1);
		add(removeButton, gbc);
		
		//	create button responsible for making calculation
		calculateButton = new JButton("Calculate");

		calculateButton.addActionListener(p -> {
			Map<Double, Integer> powerCurve;
			Map<Double, Integer> totalGeneratedPower = new TreeMap<Double, Integer>();
			Map<String, Map<Double, Integer>> calculatedPower = new TreeMap<String, Map<Double, Integer>>();

			
			String[] powerCurveParameters = powerCurveList.getSelectedItem().toString().split(" ");
		
			loadedFiles.forEach((k,v) -> rowFileAnalyzer.fileReader(v));
			
			try {
				//	power curve map keeps parameters of power curve for chosen windTurbine
				powerCurve = dbAccess.selectPowerCurve(wtgList.getSelectedIndex()+1, powerCurveParameters[0]);
				calculatedPower = rowFileAnalyzer.getGeneratedPower(powerCurve);
	
				resultsText.append("Results generated for: " + wtgList.getSelectedItem()+", " + powerCurveList.getSelectedItem()+"\n");
				
				//	Display in resultsText title of columns with generated power
				for(int i = 0 ; i<47; i++) resultsText.append(" ");
				powerCurve.forEach((k, v) -> {
					resultsText.append("\t" + k + "[m/s]");
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
					if(totalGeneratedPower.get(u) == null){
						totalGeneratedPower.put(u, w);
					} else {
						totalGeneratedPower.put(u, w+totalGeneratedPower.get(u));
					}		
//					totalGeneratedPower.put(u, w);
					if(u>=3 & u<=25){			
//						resultsText.append(w+"\t");	
						resultsText.append(convertkWhToMWh(w, 100)+"\t");
					} 
				});
				resultsText.append("\n");
			});
			
			resultsText.append("\nGenerated electricity MWh");
			for(int i = 0 ; i<10; i++) resultsText.append(" ");
			totalGeneratedPower.forEach((k, v) -> {
				if(k>=3 & k<=25){
//					resultsText.append("\t"+v);					
					resultsText.append("\t"+convertkWhToMWh(v,100));
				}
			});
			resultsText.append("\n");
			
			//	Display full load hours
			resultsText.append("Full Load Hours [MWh/y]");
			for(int i = 0 ; i<10; i++) resultsText.append(" ");
			totalGeneratedPower.forEach((k, v) -> {
				if(k>=3 & k<=25){	
					resultsText.append("\t"+convertkWhToMWhFullLoad(v, 100));			
				}
			});
			resultsText.append("\n\n");
			
			
			
		});
		
		setPosition(0, 5, 5, 1);
		add(calculateButton, gbc);
		
		// create text area where after calculation we will put results
		resultsText = new JTextArea(10,10);
		resultsTextScroll = new JScrollPane(resultsText);
		setPosition(0, 10, 4, 4);
		add(resultsTextScroll, gbc);
		
		//	create button for saving data from resultsText
		saveButton = new JButton("Save");
		setPosition(4, 10, 1, 1);
		add(saveButton, gbc);
		
		//	remove actual content of resultsText
		textEaser = new JButton("Clean");
		textEaser.addActionListener(p->resultsText.setText(""));
		setPosition(4,11,1,1);
		add(textEaser, gbc);
		
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
		doubleMWh = precision*(doubleMWh/dbAccess.getWTGnominalPower(wtgList.getSelectedIndex()+1));
		int intMWh = (int) doubleMWh/1;
		doubleMWh = (double) intMWh / precision;		
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
	
	
}