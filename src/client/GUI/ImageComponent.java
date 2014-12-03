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

import client.BatchManager.BatchState;

public class ImageComponent extends JComponent{
	
	private int w_centerX;
	private int w_centerY;
	private double scale;

	private boolean dragging;
	private int w_dragStartX;
	private int w_dragStartY;
	private int w_dragStartOriginX;
	private int w_dragStartOriginY;
	private int highlightX;
	private int highlightY;
	private int highlightW;
	private int highlightH;
	
	private BatchState batchstate;
	private Image batchImage;

	private ArrayList<DrawingShape> shapes;
	
	public ImageComponent(BatchState bs){
		batchstate = bs;
		
		w_centerX = 0;
		w_centerY = 0;
		scale = 1.0;
		highlightX = 0;
		highlightY = 0;
		highlightW = 10;
		highlightH = 10;
		
		initDrag();

		shapes = new ArrayList<DrawingShape>();
		
		this.setBackground(new Color(178, 223, 210));
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
				batchImage = batchstate.getImage();
				highlightX = batchstate.getBatchInfo().getField_array()[0].getXcoordinate();
				highlightY = batchstate.getBatchInfo().getFirst_y_coord();
				highlightW = batchstate.getBatchInfo().getField_array()[0].getWidth();
				highlightH = batchstate.getBatchInfo().getRecord_height();
				shapes.add(new DrawingImage(batchImage, new Rectangle2D.Double(-batchImage.getWidth(null)/2,
						-batchImage.getHeight(null)/2, batchImage.getWidth(null), batchImage.getHeight(null))));
				
				shapes.add(new DrawingRect(new Rectangle2D.Double(highlightX, highlightY, 
						highlightW, highlightH), new Color(210, 180, 140, 100)));
				repaint();
			}
		};
		batchstate.addListener(al);
		
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
		if(scale > 10){
			scale = 10;
		}
		else if(scale<0.1){
			scale = 0.1;
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
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			setScale(scale - e.getPreciseWheelRotation()/15);
		}	
	};


}
