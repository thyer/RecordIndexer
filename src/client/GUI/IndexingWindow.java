package client.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import client.BatchManager.BatchState;

public class IndexingWindow extends JFrame{
	private BatchState batchstate;
	private JPanel buttonpanel;
	private JMenuItem downloadBatchItem;
	private ImageComponent image;
	private JSplitPane top_bottom;
	private JSplitPane left_right;
	
	public IndexingWindow(BatchState bs){
		
		//setup
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		batchstate = bs;
		this.setTitle("Indexer");
		this.setLayout(new BorderLayout());
		
		//menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		downloadBatchItem = new JMenuItem("Download Batch", KeyEvent.VK_D);
		downloadBatchItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				batchstate.openDownloadDialog();
				
			}
			
		});
	    fileMenu.add(downloadBatchItem);
	    JMenuItem logoutItem = new JMenuItem("Logout", KeyEvent.VK_L);
	    logoutItem.addActionListener(new ActionListener(){
	    	@Override
	    	public void actionPerformed(ActionEvent e){
	    		try {
					batchstate.logout();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	    	}
	    });
	    fileMenu.add(logoutItem);
	    JMenuItem exitItem = new JMenuItem("Exit");
	    exitItem.addActionListener(new ActionListener(){
	    	@Override
	    	public void actionPerformed(ActionEvent e){
	    		batchstate.exit();
	    	}
	    });
	    fileMenu.add(exitItem);
	    this.setJMenuBar(menuBar);
	    
		//buttonpanel
		buttonpanel = new JPanel();
		buttonpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JButton zoomInButton = new JButton("Zoom In");
		JButton zoomOutButton = new JButton("Zoom Out");
		JButton invertButton = new JButton("Nightvision");
		JButton toggleButton = new JButton("Toggle Highlights");
		JButton saveButton = new JButton("Save");
		JButton submitButton = new JButton("Submit");
		buttonpanel.add(zoomInButton);
		zoomInButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				image.setScale(image.getScale() + 0.1);
			}
		});
		buttonpanel.add(zoomOutButton);
		zoomOutButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				image.setScale(image.getScale() - 0.1);
			}
		});
		buttonpanel.add(invertButton);
		invertButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				image.invertImage();
			}
		});
		buttonpanel.add(toggleButton);
		toggleButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				image.toggleHighlights();
			}
		});
		buttonpanel.add(saveButton);
		saveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					batchstate.save();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		buttonpanel.add(submitButton);
		submitButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				batchstate.submit();
			}
		});
		setButtonBarEnabled(false);
		add(buttonpanel, BorderLayout.NORTH);
		
		//SplitPane
		image = new ImageComponent(batchstate);
		left_right = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new TabbedEntryPane(batchstate), new TabbedHelpPane(batchstate));
		top_bottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT, image, left_right);
		add(top_bottom, BorderLayout.CENTER);
		top_bottom.setDividerLocation(0.6);
		top_bottom.setResizeWeight(0.6);
		left_right.setDividerLocation(0.5);
		left_right.setResizeWeight(0.4);

	}
	
	/**
	 * This will essentially toggle between being able to download a batch or play with the buttons
	 * Because you can't have your cake and eat it too.
	 * @param b true if you want to enable buttons, false if you want to disable them
	 */
	public void setButtonBarEnabled(boolean b){
		for(Component c : buttonpanel.getComponents()){
			c.setEnabled(b);
		}
		downloadBatchItem.setEnabled(!b);
		
	}
	
	/**
	 * inverts the image, then returns whether the image is dark or light
	 * @return true if the image is light and false if the image is dark
	 */
	public boolean invertImage(){
		image.invertImage();
		return image.isInverted();
	}
	
	/**
	 * toggles the highlights, then returns whether the highlights are currently displayed
	 * @return true if the highlights are showing and false if not
	 */
	public boolean toggleHighlights(){
		image.toggleHighlights();
		return image.getHighlights();
	}
	
	public double getZoom(){
		return image.getScale();
	}
	
	public void setZoom(double newScale){
		image.setScale(newScale);
	}
	
	public int getTopBottomSliderPosition(){
		return top_bottom.getDividerLocation();
	}
	
	public void setTopBottomSliderPosition(int location){
		top_bottom.setDividerLocation(location);
	}
	
	public int getLeftRightSliderPosition(){
		return top_bottom.getDividerLocation();
	}
	
	public void setLeftRightSliderPosition(int location){
		top_bottom.setDividerLocation(location);
	}
	
	public int getXTranslate(){
		return image.getCenterX();
	}
	
	public void setXTranslate(int x){
		image.setCenterX(x);
	}
	
	public int getYTranslate(){
		return image.getCenterY();
	}
	
	public void setYTranslate(int y){
		image.setCenterY(y);
	}

}
