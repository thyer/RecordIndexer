package client.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.coobird.thumbnailator.Thumbnails;
import client.BatchManager.BatchState;
import shared.modelclasses.Project;

public class DownloadDialog extends JDialog{
	private List<Project> projects;
	private BatchState batchstate;
	private JComboBox projectsBox;
	
	public DownloadDialog(BatchState bs){
		super();
		batchstate = bs;
		setTitle("Download Batch");
		
		//Projects
		JPanel viewingPane = new JPanel();
		add(viewingPane);
		projectsBox = new JComboBox<String>();
		for(String s : batchstate.getProjects()){
			projectsBox.addItem(s);
		}
		viewingPane.add(projectsBox);
		
		//Buttons
		JPanel buttonPane = new JPanel();
		JButton sampleButton = new JButton("View Sample Image");
		JButton downloadButton = new JButton("Download Image");
		JButton cancelButton = new JButton("Cancel");
		viewingPane.add(sampleButton);
		viewingPane.add(buttonPane);
		buttonPane.add(downloadButton);
		buttonPane.add(cancelButton);
		
		//Listeners
		sampleButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				BufferedImage image = batchstate.getSampleImage(projectsBox.getSelectedIndex()+1); 
				try {
					image = Thumbnails.of(image).size(600, 400).asBufferedImage();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				ImageIcon icon = new ImageIcon(image);
				JPanel j = new JPanel();
				j.add(new JLabel(icon));
				addImage(j);
			}
			
		});
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				close();
			}
		});
		downloadButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(batchstate.downloadBatch(projectsBox.getSelectedIndex()+1)){
					close();
				}
			}
		});
	}
	
	public void addImage(JPanel j){
		final JDialog imageDialog = new JDialog(this);
		JButton cancelButton = new JButton("Exit");
		j.add(cancelButton);
		imageDialog.add(j);
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				imageDialog.dispose();
			}
		});
		
		imageDialog.setLocationRelativeTo(null);
		imageDialog.setSize(610,470);
		imageDialog.setModal(true);
		imageDialog.setResizable(false);
		imageDialog.setVisible(true);
	}
	
	public void close(){
		this.dispose();
	}
	
	
}
