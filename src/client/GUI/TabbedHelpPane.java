package client.GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.coobird.thumbnailator.Thumbnails;
import client.BatchManager.BatchState;

public class TabbedHelpPane extends JTabbedPane{
	private BatchState batchstate;
	private JPanel field_help;
	private JPanel image_navigator;
	
	public TabbedHelpPane (BatchState bs){
		super();
		batchstate = bs;
		image_navigator = new JPanel(){
			BufferedImage image = batchstate.getImage();
			
		    @Override
		    protected void paintComponent(Graphics g) {
		    	this.setBackground(Color.cyan);
		        super.paintComponent(g);
		        try {
		        	image = batchstate.getImage();
		        	if(image !=null){
		        		//System.out.println("Setting thumnail size to width: " + this.getWidth()+", height: " + this.getHeight());
		        		image = Thumbnails.of(image).size(this.getWidth(), this.getHeight()).asBufferedImage();
		        	}
				} catch (IOException e) {
					e.printStackTrace();
				}
		        g.drawImage(image, 10, 10, null);      
		    }
		};
		
		batchstate.addListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				repaint();
			}
		});
		
		field_help = new JPanel(){
			 @Override
			    protected void paintComponent(Graphics g) {
				 	try{
				 		String field_help = batchstate.getBatchInfo().getField_array()[0].getField_help_file_path();
				 		//System.out.println("Field help: " + field_help);
				 	} catch(Exception e){
				 		//System.out.println("No field help file found yet");
				 	}
				 	
			    	this.setBackground(Color.gray);
			        super.paintComponent(g);   
			    }
		};
		this.add("Field Help", field_help);
		this.add("Image Navigator", image_navigator);
	}

}