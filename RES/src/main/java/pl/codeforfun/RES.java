package pl.codeforfun;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class RES {

	public static void main(String[] args) {
		 
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						System.setProperty("log4j.skipJansi",  "true");
						JFrame mainWindow = new MainWindow();
						mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				}
				});	
	}
}
