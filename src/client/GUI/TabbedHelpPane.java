package client.GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.coobird.thumbnailator.Thumbnails;
import client.BatchManager.BatchState;

public class TabbedHelpPane extends JTabbedPane{
	private BatchState batchstate;
	private JEditorPane field_help;
	private JScrollPane field_help_pane;
	private JPanel image_navigator;
	
	public TabbedHelpPane (BatchState bs){
		super();
		batchstate = bs;
		
		//Image navigator, ignore most of this, it's not functional. Extra credit didn't seem worth it after 60+ hours of coding
		image_navigator = new JPanel(){
			BufferedImage image = batchstate.getImage();
			
		    @Override
		    protected void paintComponent(Graphics g) {
		    	this.setBackground(new Color(208, 223, 210));
		        super.paintComponent(g);
		        try {
		        	image = batchstate.getImage();					//this only allows setup if a batch is downloaded and available
		        	if(image !=null){
		        		image = Thumbnails.of(image).size(this.getWidth(), this.getHeight()).asBufferedImage();	//found this with Google
		        	}
				} catch (IOException e) {
					e.printStackTrace();
				}
		        g.drawImage(image, 10, 10, null);      
		    }
		};
		

		//Sets up the field help image
		field_help = new JEditorPane();
		field_help.setEditable(false);
		field_help.setContentType("text/html");			//prepares it to receive an HTML image
		URL fieldhelpURL = batchstate.getHelpURL();
		if(fieldhelpURL!=null){
			try {
				field_help.setPage(fieldhelpURL);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else{
			//Do nothing, it's not available yet
		}

		field_help_pane = new JScrollPane(field_help);	//packages the image inside a scroll pane
		this.add("Field Help", field_help_pane);
		this.add("Image Navigator", image_navigator);
		
		batchstate.addListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				try {
					field_help.setPage(batchstate.getHelpURL());
				} catch (IOException e1) {
					System.out.println("Invalid helpURL found: " + batchstate.getHelpURL());
				}
				repaint();
			}
		});
		
	}

}