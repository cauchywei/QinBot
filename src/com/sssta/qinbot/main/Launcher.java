package com.sssta.qinbot.main;

import javax.swing.SwingUtilities;

import com.sssta.qinbot.swing.MainWindow;

public class Launcher {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){    
            public void run(){    
            //update the GUI   
        		MainWindow window = new MainWindow();
        		window.launchFrame();
            }   
		});
	}
	

}
