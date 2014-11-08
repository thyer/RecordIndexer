package client.GUI;

import java.awt.*;


public class ClientGUI {

	public static void main(String[] args) {
		EventQueue.invokeLater(
				new Runnable() {
					public void run() {
						StartMenuFrame frame = new StartMenuFrame();			
						frame.setVisible(true);
						frame.setLocationRelativeTo(null);
					}
				}
		);

	}

}