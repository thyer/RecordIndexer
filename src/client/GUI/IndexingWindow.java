package client.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

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
	    		batchstate.logout();
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
		buttonpanel.add(submitButton);
		setButtonBarEnabled(false);
		add(buttonpanel, BorderLayout.NORTH);
		
		//SplitPane
		image = new ImageComponent(batchstate);
		JSplitPane left_right = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new TabbedEntryPane(batchstate), new TabbedHelpPane(batchstate));
		JSplitPane top_bottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT, image, left_right);
		add(top_bottom, BorderLayout.CENTER);
		top_bottom.setDividerLocation(0.7);
		top_bottom.setResizeWeight(0.7);
		left_right.setDividerLocation(0.5);
		left_right.setResizeWeight(0.4);

	}
	
	public void setButtonBarEnabled(boolean b){
		for(Component c : buttonpanel.getComponents()){
			c.setEnabled(b);
		}
		downloadBatchItem.setEnabled(!b);
		
	}
	

}
