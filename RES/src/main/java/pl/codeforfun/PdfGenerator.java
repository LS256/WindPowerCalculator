package pl.codeforfun;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.time.LocalTime;
import java.util.Map;
import java.util.stream.Collectors;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

public class PdfGenerator {
	
	PanelPower panelPower;
	MyChartPanel myChartPanel;
	protected PdfGenerator(){
		
	}
	
	PdfGenerator(Map<String, Map<Double, Integer>> totalGeneratedPowerChart, String analyzedFiles) {
		panelPower = new PanelPower();
		try {
			
			myChartPanel = new MyChartPanel();
			myChartPanel.detailedChartJpg("Generated Power", totalGeneratedPowerChart);
			myChartPanel.mainChartJpg(totalGeneratedPowerChart, 1);
			
			LocalTime localTime = LocalTime.now();

			 
			 
			 PdfWriter writer = new PdfWriter("x2.pdf");
			 PdfDocument pdf = new PdfDocument(writer);
			 Document document = new Document(pdf);
			 
			 document.add(new Paragraph(localTime+""));
			 document.add(new Paragraph("Analyzed files:"));
			 document.add(new Paragraph(analyzedFiles).setMargins(0,  0,  0,  50)); 
 
			 document.add(new Paragraph("Analyzed wind turbines: "));
			 totalGeneratedPowerChart.forEach((k, v) ->  document.add(new Paragraph(k).setMargins(0,  0,  0,  50))); 
			 
			 
			 document.add(new Paragraph(localTime.toString()));

			 
			 ImageData preImg = ImageDataFactory.create("details.jpg");
			 Image img = new Image(preImg);
			 document.add(img);
		
			 preImg = ImageDataFactory.create("main.jpg");
			 img = new Image(preImg);
			 document.add(img);
			 
			 
			 int tableSize = totalGeneratedPowerChart.size()+1;				 
			 Table table = new Table(tableSize, true);
			 
			 table.addHeaderCell(new Cell().setKeepTogether(true).add(new Paragraph("Vmean")));
			 totalGeneratedPowerChart.forEach((k,v) -> table.addHeaderCell(new Cell().setKeepTogether(true).add(new Paragraph(k))));
			  
			 document.add(table);
			 
			 for(double  i = 0.0 ; i<=25.0 ; i+=1.0) {
				 
				 table.addCell(new Cell().setKeepTogether(true).add(new Paragraph(i+"").setMargins(0, 0, 0, 0)));
				 for(Map.Entry<String, Map<Double, Integer>> wtgName : totalGeneratedPowerChart.entrySet()) {
					if(wtgName.getValue().get(i) == null) {
						table.addCell(new Cell().setKeepTogether(true).add(new Paragraph("0")).setMargins(0, 0, 0, 0));
					}	else { 
						table.addCell(new Cell().setKeepTogether(true).add(new Paragraph(panelPower.convertkWhToMWh(wtgName.getValue().get(i), 100)+"")).setMargins(0, 0, 0, 0));
					}		
				 }
			 }	
			 
			 //	adding to table summary of generated energy
			 table.addCell(new Cell().setKeepTogether(true).add(new Paragraph("Total energy")).setMargins(0, 0, 0, 0)); 
			 totalGeneratedPowerChart.forEach((k,v) -> {
					table.addCell(new Cell().setKeepTogether(true).add(new Paragraph((v.values().stream().collect(Collectors.summingInt(Integer::intValue))/(1000)+"")).setMargins(0, 0, 0, 0)));
				});	
			 // adding to table summary of generated full load hours
			 table.addCell(new Cell().setKeepTogether(true).add(new Paragraph("Total Full Load hours")).setMargins(0, 0, 0, 0)); 
			 totalGeneratedPowerChart.forEach((k,v) -> {
				String[] wtgPowerTab = k.split(" "); 
				double wtgPower = Double.parseDouble(wtgPowerTab[wtgPowerTab.length - 1]);
				table.addCell(new Cell().setKeepTogether(true).add(new Paragraph(((int)(v.values().stream().collect(Collectors.summingInt(Integer::intValue))/(1000*wtgPower))+"")).setMargins(0, 0, 0, 0)));
			System.out.println("k= " + k + ", iloœæ elementów: " + wtgPower);	
			 });

				//	TODO tak zrobiæ sumowanie full load hours i total generated power
				totalGeneratedPowerChart.forEach((k,v) -> {
					System.out.println("W stream: " + v.values().stream().collect(Collectors.summingInt(Integer::intValue))/1000);
				});
					 
			 
			 
		     table.complete();
			 
			 System.out.println("size is: " + totalGeneratedPowerChart.size());
			 
			 
			 document.close();
			
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		}		
		
		
		
	}

}
