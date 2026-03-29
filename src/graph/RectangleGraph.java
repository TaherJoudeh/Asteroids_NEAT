package graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

public class RectangleGraph extends ShapeGraph {
	
	public RectangleGraph(double value, int label) {
		super(value,label, label,0,0);
	}
	
	public RectangleGraph(double value, int labelFrom, int labelTo) {
		super(value,labelFrom, labelTo, 0, 0);
	}
	
	public void render(Graphics2D g2d) {
		
		g2d.setColor(new Color(60, 160, 230, 200));
		g2d.fill(new Rectangle2D.Double(getX(),getY(),getWidth(),getHeight()));
		
	}
	
	public void renderValue(Graphics2D g2d, Point p) {
		g2d.setColor(Color.BLACK);
		
		String nodeValueString = String.format("%.7f", getValue());
		String labelStringValue = String.format("Gen %s", getLabel());
		
		int nodeValueWidth = g2d.getFontMetrics().stringWidth(nodeValueString);
		int genWidth = g2d.getFontMetrics().stringWidth(labelStringValue);
		int fontHeight = g2d.getFontMetrics().getHeight();
		int fontAscent = g2d.getFontMetrics().getAscent();

		Rectangle2D.Double highlight = new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
		g2d.setColor(new Color(250,250,250,255));
		g2d.draw(highlight);
		
		g2d.setColor(Color.BLACK);
		
		Rectangle2D.Float rect = new Rectangle2D.Float(p.x, p.y, nodeValueWidth, 2f*fontHeight);
		rect.y -= rect.height;
		g2d.fill(rect);
		
		g2d.setColor(Color.WHITE);
		g2d.drawString(labelStringValue, (int) (rect.getCenterX()-(genWidth/2)), rect.y+fontAscent);
		g2d.drawString(nodeValueString, rect.x, rect.y+fontHeight+fontAscent);
	}
	
	@Override
	public String toString() {
		return String.format("(%d,%d)", getX(),getY());
	}

	@Override
	public boolean isPointWithin(Point point) {
		return new Rectangle2D.Double(getX(),getY(),getWidth(),getHeight()).contains(point);
	}
	
}
