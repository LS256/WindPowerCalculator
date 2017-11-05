package pl.codeforfun;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class MainWindow extends JFrame{
	
	MainWindow(){
		PanelPower panelPower = new PanelPower();
		this.setTitle("powerCalculation");
//		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);	
		this.setSize(550, 530);
		this.add(panelPower);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		this.pack();
		
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("Help");
		
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		
		JMenuItem closeMe = new JMenuItem("Close");
		closeMe.addActionListener(action -> System.exit(0));
		fileMenu.add(closeMe);
		
		JMenuItem aboutMe = new JMenuItem("About program");
			aboutMe.addActionListener(action -> JOptionPane.showMessageDialog(this,  "For more details about this application please visit www.codeForFun.pl", "About application", JOptionPane.INFORMATION_MESSAGE, null));
		
		helpMenu.add(aboutMe);
		
		setJMenuBar(menuBar);
		

		this.setVisible(true);
	}
}
