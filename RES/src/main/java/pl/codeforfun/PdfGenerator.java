package pl.codeforfun;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

/**
 * Class to summarize results of user calculation. All results will be saved in pdf file
 * @author LS256
 *
 */

public class PdfGenerator {
	
	PanelPower panelPower;
	MyChartPanel myChartPanel;
	JFileChooser fileChooser;
	PdfWriter writer;
	PdfDocument pdf;
	Document document;
	DBaccess dbAccess;
	
	protected PdfGenerator(){
		
	}
	
	PdfGenerator(Map<String, Map<Double, Integer>> totalGeneratedPowerChart, String analyzedFiles, RowFileAnalyzer rowFileAnalyzer) {
		panelPower = new PanelPower();
		dbAccess = new DBaccess();
		
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("C://Users/"));
		if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
			try {
				
				writer = new PdfWriter(fileChooser.getSelectedFile()+".pdf");
				pdf = new PdfDocument(writer);
				document = new Document(pdf);

			int totalMeasuredHours = rowFileAnalyzer.getMeasuredHours();
			
			document.add(new Paragraph("Wind Yield Analyzer  -  www.codeForFun.pl").setItalic().setHorizontalAlignment(HorizontalAlignment.CENTER));
			document.add(new Paragraph(""));
			//	Prepare a list of files taken under analysis
			document.add(new Paragraph("Files taken under analysis").setBold());
			document.add(new Paragraph(analyzedFiles).setMarginLeft(100F));
			
			
			//	Prepare table of turbines used in user calculation
			document.add(new Paragraph("Turbines used in user calculation and their main parameters").setBold());
			float[] colWidths = {205, 80, 80, 80, 80 };
			Table usedTurbinesTab = new Table(colWidths);
			usedTurbinesTab.setWidth(525);
			String[] usedTurbinesTabHeader = {"Turbine model", "Power mode", "Nominal Power", "Tower height", "Rotor diameter"};
			String[] usedTurbinesTabUnits = {"-", "[MW]", "[m]", "[m]"};
		
			boolean twoRowsCell = true;
			//	Add columns title to table with turbines used in calculation
			for(String header : usedTurbinesTabHeader) {
				if(twoRowsCell) {
					Cell cell = new Cell(2,1).setTextAlignment(TextAlignment.CENTER).add(header).setBold();
					usedTurbinesTab.addHeaderCell(cell);
					twoRowsCell=false;
				} else {
					usedTurbinesTab.addHeaderCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(header).setBold());
				}
			}

			//	add columns units to table with turbines used in calculation
			for(String units : usedTurbinesTabUnits) {
				usedTurbinesTab.addHeaderCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(units));
			}
			
			//	fill table with parameters of used turbines
			totalGeneratedPowerChart.forEach((k, v) -> {
				String[] wtgPowerTab = k.split(" "); 
				String nominalPower = wtgPowerTab[wtgPowerTab.length-1];
				String towerHeight = wtgPowerTab[wtgPowerTab.length-3];
				String powerMode = wtgPowerTab[wtgPowerTab.length-6] + " " + wtgPowerTab[wtgPowerTab.length-5];
				String wtgModel = wtgPowerTab[0] + " " + wtgPowerTab[1];
				
				
				String wtgRotorDiameter ="unknown";
				try {
					wtgRotorDiameter = dbAccess.getRotorDiameter(wtgModel);
				} catch (Exception e) {
					e.printStackTrace();
				}
							
				usedTurbinesTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(wtgModel));
				usedTurbinesTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(powerMode));
				usedTurbinesTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(nominalPower));
				usedTurbinesTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(towerHeight));
				usedTurbinesTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(wtgRotorDiameter));
			});
			
			document.add(usedTurbinesTab);
			
			
			//	Prepare summarize of main calculation results		
			document.add(new Paragraph(""));	
			document.add(new Paragraph(""));	
			document.add(new Paragraph("Key results for chosen wind turbines:").setBold());			 					 

			Table keyResultsTab = new Table(colWidths);
			keyResultsTab.setWidth(525);
			 
			String [] keyResultsTabHeader = {"Analyzed wind turbine", "Generated Energy", "Full load hours", "Capacity Factor", "Mean Wind speed"};
			String [] keyResultsTabUnits = {"[MWh]", "[h]", "[%]", "[m/s]"};
			
			twoRowsCell = true;
			//	Add columns title to table with main results
			for(String header : keyResultsTabHeader){
				if(twoRowsCell) {
					Cell cell = new Cell(2,1).setTextAlignment(TextAlignment.CENTER).add(header).setBold();
					keyResultsTab.addHeaderCell(cell);
					twoRowsCell=false;
				} else {
					keyResultsTab.addHeaderCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(header).setBold());
				}
			}
			
			//	add columns units to table with main results
			for(String unit : keyResultsTabUnits){
				keyResultsTab.addHeaderCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(unit));
			}
			 			
			totalGeneratedPowerChart.forEach((k,v) -> {
				String[] wtgPowerTab = k.split(" "); 
				double wtgPower = Double.parseDouble(wtgPowerTab[wtgPowerTab.length - 1]);
				double analyzedTowerHeight =  Double.parseDouble(wtgPowerTab[wtgPowerTab.length - 3]);
				int meanWindSpeedI = (int) (100 * rowFileAnalyzer.getMeanWindSpeed(analyzedTowerHeight));
				double meanWindSpeedD = meanWindSpeedI / 100.0;
					
				int turbineGeneratedPower = v.values().stream().collect(Collectors.summingInt(Integer::intValue))/(1000);
						
				int capacityFactorI =(int) ( 1000 * ( (turbineGeneratedPower / wtgPower) / totalMeasuredHours));
				double capacityFactorD = capacityFactorI / 10.0;
					
				 
				keyResultsTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(k));
				keyResultsTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(turbineGeneratedPower+""));
				keyResultsTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add((int)(turbineGeneratedPower/wtgPower)+""));
				keyResultsTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(capacityFactorD+""));
				keyResultsTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(meanWindSpeedD+""));			
				
			});	
			
			document.add(keyResultsTab);

			//	Prepare summarize of calculation results details
			document.add(new Paragraph(""));	
			document.add(new Paragraph(""));	
			document.add(new Paragraph("Detailed calculation results").setBold());			
			
			//	Because depends on user choice, number of turbines used in calculation may vary, and because of that column width is calculated on the basis of numbers of used turbines
			int tableSize = totalGeneratedPowerChart.size()+1;	
			float[] newColumns = new float[tableSize];
			newColumns[0] = 50;
			int colWidht = (int) (475 / (tableSize -1));
			for( int i =1; i < tableSize; i++){
				newColumns[i] = colWidht;
			}
			
			Table detailedInfoTab = new Table(newColumns);
			
			detailedInfoTab.addHeaderCell(new Cell().setTextAlignment(TextAlignment.CENTER).add("Vmean [m/s]").setBold());
			totalGeneratedPowerChart.forEach((k,v) -> detailedInfoTab.addHeaderCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(k).setBold()));
			
			 for(double  i = 0.0 ; i<=25.0 ; i+=1.0) {
				 
				 detailedInfoTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(i + ""));
				 for(Map.Entry<String, Map<Double, Integer>> wtgName : totalGeneratedPowerChart.entrySet()) {
					if(wtgName.getValue().get(i) == null) {
						detailedInfoTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add("0"));
					}	else { 	
						detailedInfoTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(panelPower.convertkWhToMWh(wtgName.getValue().get(i), 100)+""));
					}		
				 }
			 }			
			 
			 //	Add to table summary of generated energy
			 detailedInfoTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add("Total Energy"));
	
			 totalGeneratedPowerChart.forEach((k,v) -> {
					detailedInfoTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(v.values().stream().collect(Collectors.summingInt(Integer::intValue))/(1000)+"").setBold());
				});	
			 
			 // Add to table summary of generated full load hours
			 detailedInfoTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add("Total Full Load Hours"));
			 
			 totalGeneratedPowerChart.forEach((k,v) -> {
				String[] wtgPowerTab = k.split(" "); 
				double wtgPower = Double.parseDouble(wtgPowerTab[wtgPowerTab.length - 1]);
				detailedInfoTab.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add((int)(v.values().stream().collect(Collectors.summingInt(Integer::intValue))/(1000*wtgPower))+"").setBold());
			 });

			 document.add(detailedInfoTab);
			 		 
			// Preparing charts	
			myChartPanel = new MyChartPanel();
			myChartPanel.detailedChartJpg("Generated Power", totalGeneratedPowerChart);
			myChartPanel.mainChartJpg(totalGeneratedPowerChart, 1);


				
			 //	add chart with detailed results
			 ImageData preImg = ImageDataFactory.create("details.jpg");
			 Image img = new Image(preImg);
			 document.add(img.scale(0.625F,  0.625F));
			 
			//	Put empty 3 lines between charts
			document.add(new Paragraph(""));
			document.add(new Paragraph(""));
			document.add(new Paragraph(""));
			
			 //	Add chart with main results
			 preImg = ImageDataFactory.create("main.jpg");
			 img = new Image(preImg);
			 document.add(img.scale(0.625F, 0.625F)); 
	
			 
			 document.close();
			
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}			

	}
}
