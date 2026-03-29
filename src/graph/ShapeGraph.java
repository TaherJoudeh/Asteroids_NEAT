package graph;

import java.awt.Graphics2D;
import java.awt.Point;

public abstract class ShapeGraph {
	
	private double value;
	private int labelFrom;
	private int labelTo;
	
	private boolean displayed;
	
	private double x, y;
	private Double width, height;
	
	public ShapeGraph(double value, int labelFrom, int labelTo, double width, double height) {
		this.value = value;
		this.labelFrom = labelFrom;
		this.labelTo = labelTo;
		this.width = width;
		this.height = height;
		displayed = true;
	}
	
	public double getX() { return x; }
	public void setX(double x) { this.x = x; }
	
	public double getY() { return y; }
	public void setY(double y) { this.y = y; }
	
	public double getWidth() { return width; }
	public void setWidth(double width) { this.width = width; }
	
	public double getHeight() { return height; }
	public void setHeight(double height) { this.height = height; }	
	
	public double getValue() { return value; }
	public void setValue(double value) { this.value = value; }
	
	public int getLabelFrom() { return labelFrom; }
	public void setLabelFrom(int labelFrom) { this.labelFrom = labelFrom; }
	
	public int getLabelTo() { return labelTo; }
	public void setLabelTo(int labelTo) { this.labelTo= labelTo; }
	
	public void setLabel(int labelFrom, int labelTo) {
		this.labelFrom = labelFrom;
		this.labelTo = labelTo;
	}
	
	public String getLabel() {
		if (labelFrom == labelTo)
			return labelFrom + "";
		return labelFrom + "-" + labelTo;
	}
	
	public boolean isAveraged() {
		return labelFrom != labelTo;
	}
	
	public boolean isDisplayed() { return displayed; }
	public void setDisplayed(boolean displayed) { this.displayed = displayed; }
	public void hid() { displayed = false; }
	public void display() { this.displayed = true; }
	
	public abstract boolean isPointWithin(Point point);
	public abstract void render(Graphics2D g2d);
	public abstract void renderValue(Graphics2D g2d, Point p);
		
	@Override
	public String toString() {
		return String.format("(%d,%d)", x,y);
	}
	
}
