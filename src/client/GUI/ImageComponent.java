package client.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import shared.modelclasses.Field;
import client.BatchManager.BatchState;
import client.BatchManager.BatchState.Cell;

public class ImageComponent extends JComponent{
	
	private int w_centerX;
	private int w_centerY;
	private double scale;
	private boolean highlights;
	private boolean dragging;
	private boolean dragged;
	private int w_dragStartX;
	private int w_dragStartY;
	private int w_dragStartOriginX;
	private int w_dragStartOriginY;
	private int highlightX;
	private int highlightY;
	private int highlightW;
	private int highlightH;
	
	private Cell currentCell;
	private BatchState batchstate;
	private Image batchImage;

	private ArrayList<DrawingShape> shapes;
	
	public ImageComponent(BatchState bs){
		batchstate = bs;
		highlights = true;
		w_centerX = 0;
		w_centerY = 0;
		scale = 1.0;
		highlightX = 0;
		highlightY = 0;
		highlightW = 10;
		highlightH = 10;
		
		initDrag();

		shapes = new ArrayList<DrawingShape>();
		
		this.setBackground(new Color(208, 223, 210));
		this.setPreferredSize(new Dimension(700, 700));
		this.setMinimumSize(new Dimension(100, 100));
		this.setMaximumSize(new Dimension(1000, 1000));
		
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		this.addMouseWheelListener(mouseAdapter);
		
		
		
		//add listener
		ActionListener al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				shapes.clear();
				currentCell = batchstate.getCurrentCell();
				batchImage = batchstate.getImage();
				highlightW = batchstate.getBatchInfo().getField_array()[currentCell.getColumn()-1].getWidth();
				highlightH = batchstate.getBatchInfo().getRecord_height();
				highlightX = batchstate.getBatchInfo().getField_array()[currentCell.getColumn()-1].getXcoordinate();
				highlightY = batchstate.getBatchInfo().getFirst_y_coord() + (currentCell.getRow()-1)*highlightH;
				highlightX = 0-batchImage.getWidth(null)/2 + highlightX;
				highlightY = 0-batchImage.getHeight(null)/2 + highlightY;
				shapes.add(new DrawingImage(batchImage, new Rectangle2D.Double(-batchImage.getWidth(null)/2,
						-batchImage.getHeight(null)/2, batchImage.getWidth(null), batchImage.getHeight(null))));
				if(highlights){
					shapes.add(new DrawingRect(new Rectangle2D.Double(highlightX, highlightY, 
							highlightW, highlightH), new Color(210, 180, 140, 100)));
				}	
				repaint();
			}
		};
		batchstate.addListener(al);
		
	}
	
	public void toggleHighlights(){
		highlights = !highlights;
		batchstate.update();
	}
	
	public void updateCell(int x, int y){
		//establishes base measurements
		int firstY = batchstate.getBatchInfo().getFirst_y_coord();
		int numRecords = batchstate.getBatchInfo().getNum_records();
		Field[] fields = batchstate.getBatchInfo().getField_array();
		
		//verifies that x and y are within the grid
		if(y<firstY||y>(firstY+numRecords*highlightH)){ //y is less than the first record's y coordinate or greater than the last
			return;
		}
		else if(x<fields[0].getXcoordinate()){ //x is less than the first x coordinate
			return;
		}
		else if(x>(fields[fields.length-1].getXcoordinate()+fields[fields.length-1].getWidth())){ //x is greater than the last x coordinate
			return;
		}
		
		//sets the new column and row
		int outputRow=0;
		for(int i=firstY; i<(firstY + numRecords*highlightH); i=i+highlightH){
			if(y<i){
				break;
			}
			++outputRow;	
		}
		int outputColumn=0;
		for(Field f : fields){
			++outputColumn;
			if(x>f.getXcoordinate()&x<(f.getXcoordinate()+f.getWidth())){
				break;
			}
		}
		batchstate.setCurrentCell(outputRow, outputColumn);
		batchstate.update();
	}
	
	private void initDrag() {
		dragging = false;
		w_dragStartX = 0;
		w_dragStartY = 0;
		w_dragStartOriginX = 0;
		w_dragStartOriginY = 0;
	}
	
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		drawBackground(g2);

		g2.translate(getWidth()/2.0, getHeight()/2.0);
		g2.scale(scale, scale);
		g2.translate(-w_centerX, -w_centerY);

		drawShapes(g2);
	}
	
	private void drawBackground(Graphics2D g2) {
		g2.setColor(getBackground());
		g2.fillRect(0,  0, getWidth(), getHeight());
	}

	public void setScale(double newScale) {
		scale = newScale;
		if(scale > 5){
			scale = 5;
		}
		else if(scale<0.5){
			scale = 0.5;
		}
		this.repaint();
	}
	
	public double getScale(){
		return scale;
	}
	
	public void invertImage(){
		RescaleOp ro = new RescaleOp (-1.0f, 255f, null);
		batchstate.setImage(ro.filter(batchstate.getImage(),null));
		shapes.clear();
		shapes.add(new DrawingImage(batchstate.getImage(), new Rectangle2D.Double(-batchImage.getWidth(null)/2,
				-batchImage.getHeight(null)/2, batchImage.getWidth(null), batchImage.getHeight(null))));
		if(highlights){
			shapes.add(new DrawingRect(new Rectangle2D.Double(highlightX, highlightY, 
					highlightW, highlightH), new Color(210, 180, 140, 100)));
		}
		repaint();
		
	}
	
	private void drawShapes(Graphics2D g2) {
		for (DrawingShape shape : shapes) {
			shape.draw(g2);
		}
	}
    
    interface DrawingShape {
		boolean contains(Graphics2D g2, double x, double y);
		void draw(Graphics2D g2);
		Rectangle2D getBounds(Graphics2D g2);
	}


	class DrawingRect implements DrawingShape {

		private Rectangle2D rect;
		private Color color;
		
		public DrawingRect(Rectangle2D rect, Color color) {
			this.rect = rect;
			this.color = color;
		}

		@Override
		public boolean contains(Graphics2D g2, double x, double y) {
			return rect.contains(x, y);
		}

		@Override
		public void draw(Graphics2D g2) {
			g2.setColor(color);
			g2.fill(rect);
		}
		
		@Override
		public Rectangle2D getBounds(Graphics2D g2) {
			return rect.getBounds2D();
		}
	}


	class DrawingImage implements DrawingShape {

		private Image image;
		private Rectangle2D rect;
		
		public DrawingImage(Image image, Rectangle2D rect) {
			this.image = image;
			this.rect = rect;
		}

		@Override
		public boolean contains(Graphics2D g2, double x, double y) {
			return rect.contains(x, y);
		}

		@Override
		public void draw(Graphics2D g2) {
			g2.drawImage(image, (int)rect.getMinX(), (int)rect.getMinY(), (int)rect.getMaxX(), (int)rect.getMaxY(),
							0, 0, image.getWidth(null), image.getHeight(null), null);
		}	
		
		@Override
		public Rectangle2D getBounds(Graphics2D g2) {
			return rect.getBounds2D();
		}
	}
	
	private MouseAdapter mouseAdapter = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {
			dragged = false;
			
			int d_X = e.getX();
			int d_Y = e.getY();
			
			AffineTransform transform = new AffineTransform();
			transform.translate(getWidth()/2.0, getHeight()/2.0);
			transform.scale(scale, scale);
			transform.translate(-w_centerX, -w_centerY);
			
			Point2D d_Pt = new Point2D.Double(d_X, d_Y);
			Point2D w_Pt = new Point2D.Double();
			try
			{
				transform.inverseTransform(d_Pt, w_Pt);
			}
			catch (NoninvertibleTransformException ex) {
				return;
			}
			int w_X = (int)w_Pt.getX();
			int w_Y = (int)w_Pt.getY();
			
			boolean hitShape = false;
			
			Graphics2D g2 = (Graphics2D)getGraphics();
			for (DrawingShape shape : shapes) {
				if (shape.contains(g2, w_X, w_Y)) {
					hitShape = true;
					break;
				}
			}
			
			if (hitShape) {
				dragging = true;		
				w_dragStartX = w_X;
				w_dragStartY = w_Y;		
				w_dragStartOriginX = w_centerX;
				w_dragStartOriginY = w_centerY;
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {		
			if (dragging) {
				dragged = true;
				int d_X = e.getX();
				int d_Y = e.getY();
				
				AffineTransform transform = new AffineTransform();
				
				transform.translate(getWidth()/2.0, getHeight()/2.0);
				transform.scale(scale, scale);
				transform.translate(-w_dragStartOriginX, -w_dragStartOriginY);
				
				Point2D d_Pt = new Point2D.Double(d_X, d_Y);
				Point2D w_Pt = new Point2D.Double();
				try
				{
					transform.inverseTransform(d_Pt, w_Pt);
				}
				catch (NoninvertibleTransformException ex) {
					return;
				}
				int w_X = (int)w_Pt.getX();
				int w_Y = (int)w_Pt.getY();
				
				int w_deltaX = w_X - w_dragStartX;
				int w_deltaY = w_Y - w_dragStartY;
				
				w_centerX = w_dragStartOriginX - w_deltaX;
				w_centerY = w_dragStartOriginY - w_deltaY;
				
				repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			initDrag();
			int d_X = e.getX();
			int d_Y = e.getY();
			
			AffineTransform transform = new AffineTransform();
			transform.translate(getWidth()/2.0, getHeight()/2.0);
			transform.scale(scale, scale);
			transform.translate(-w_centerX, -w_centerY);
			
			Point2D d_Pt = new Point2D.Double(d_X, d_Y);
			Point2D w_Pt = new Point2D.Double();
			try
			{
				transform.inverseTransform(d_Pt, w_Pt);
			}
			catch (NoninvertibleTransformException ex) {
				return;
			}
			int x = (int) (w_Pt.getX()+batchImage.getWidth(null)/2.0);
			int y = (int) (w_Pt.getY()+batchImage.getHeight(null)/2.0);
			if(!dragged){
				updateCell(x,y);
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			setScale(scale - e.getPreciseWheelRotation()/15);
		}	
	};


}
