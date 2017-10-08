package pl.codeforfun;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MainWindow extends JFrame{
	
	MainWindow(){
		this.setTitle("powerCalculation");
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setResizable(false);
		
//		this.setSize(500,600);
		
		PanelPower panelPower = new PanelPower();
		add(panelPower);
		pack();
	}
}
