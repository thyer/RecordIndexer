package client.BatchManager;

import java.awt.Dimension;
import java.awt.Point;

import shared.communication.Batch_Result;
import shared.modelclasses.Credentials;

/**
 * This is just a management object used to store data when we need to save or restore a session
 * @author thyer
 *
 */
public class BatchSaveObject extends Object{
	private String[][] indexedData;
	private int topBottomLocation;
	private int leftRightLocation;
	private int xTranslate;
	private int yTranslate;
	private double zoom;
	private boolean highlights;
	private boolean inverted;
	private Point windowPosition;
	private Dimension windowSize;
	private Batch_Result batchinfo;
	private Credentials creds;
	
	public BatchSaveObject(){
		
	}

	public String[][] getIndexedData() {
		return indexedData;
	}

	public void setIndexedData(String[][] indexedData) {
		this.indexedData = indexedData;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public boolean isHighlights() {
		return highlights;
	}

	public void setHighlights(boolean highlights) {
		this.highlights = highlights;
	}

	public boolean isInverted() {
		return inverted;
	}

	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}

	public Batch_Result getBatchinfo() {
		return batchinfo;
	}

	public void setBatchinfo(Batch_Result batchinfo) {
		this.batchinfo = batchinfo;
	}

	public Credentials getCreds() {
		return creds;
	}

	public void setCreds(Credentials creds) {
		this.creds = creds;
	}

	public int getTopBottomLocation() {
		return topBottomLocation;
	}

	public void setTopBottomLocation(int topBottomLocation) {
		this.topBottomLocation = topBottomLocation;
	}

	public int getLeftRightLocation() {
		return leftRightLocation;
	}

	public void setLeftRightLocation(int leftRightLocation) {
		this.leftRightLocation = leftRightLocation;
	}

	public Point getWindowPosition() {
		return windowPosition;
	}

	public void setWindowPosition(Point windowPosition) {
		this.windowPosition = windowPosition;
	}

	public Dimension getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(Dimension windowSize) {
		this.windowSize = windowSize;
	}

	public int getyTranslate() {
		return yTranslate;
	}

	public void setyTranslate(int yTranslate) {
		this.yTranslate = yTranslate;
	}

	public int getxTranslate() {
		return xTranslate;
	}

	public void setxTranslate(int xTranslate) {
		this.xTranslate = xTranslate;
	}
	
	
	
	
}
