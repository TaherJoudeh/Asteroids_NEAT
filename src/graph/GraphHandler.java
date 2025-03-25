package graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.LinkedList;
import game.AIBox;
import graph.Graph.GraphType;

public class GraphHandler {

	private Graph graph;
	
	private Point originPoint = new Point(AIBox.x+50,AIBox.endY-40);
	private float scalar = 10.75f;
	private double max;
	private int tens;
	
	public GraphHandler(GraphType graphType) {
		graph = new Graph(graphType);
	}
	
	public Graph getGraph() { return graph; }
	
	public LinkedList<ShapeGraph> getPoints() { return graph.getPoints(); }
	
	public void addValue(int label, double value) {
		graph.addValue(label, value);
		graph.updateDisplayed();
		updatePositionOfPoints();
	}
	
	public void update() {
		max = graph.getMax(graph.getStartIndex(),graph.getEndIndex());
		tens = getTens(max);
	}
	
	public void render(Graphics2D g2d) {
		drawBaseGraphCoordinate(g2d);
		
		if (graph.getGraphType() == GraphType.NODE)
			connectNodes(g2d);
		
		for (int i = graph.getStartIndex(); i < graph.getEndIndex(); i++)
			graph.getPoints().get(i).render(g2d);
		
		drawLabels(g2d);
		
//		String highestPopulationFitness = String.format("Highest Population Fitness: %.3f", Neat.highestPopulationFitness);
//		int fontWidth = g2d.getFontMetrics().stringWidth(highestPopulationFitness);
//		g2d.setColor(new Color(1f,1f,1f,0.7f));
//		g2d.drawString(highestPopulationFitness, AIBox.endX-fontWidth, AIBox.endY+g2d.getFontMetrics().getHeight());
		
	}
	
	private void connectNodes(Graphics2D g2d) {
		
		if (graph.size() == 0)
			return;
		
		Font originalFont = g2d.getFont();
		Font font = new Font("Arial",Font.PLAIN,15);
		g2d.setFont(font);
		
		g2d.setColor(new Color(0.5f,0.6f,1f));

		Path2D.Double path = new Path2D.Double();
		
		int startIndex = graph.getStartIndex();
		int endIndex = graph.getEndIndex();
		
		if (startIndex == 0)
			path.moveTo(originPoint.x, originPoint.y);
		else
			path.moveTo(originPoint.x, graph.getPoints().get(startIndex).getY());
		
		for (int i = startIndex; i < endIndex; i++) {
			ShapeGraph n1 = graph.getPoints().get(i);
			path.lineTo(getCenterX(n1), getCenterY(n1));
		}

		path.lineTo(getCenterX(graph.getPoints().get(graph.getEndIndex()-1)),originPoint.y);
		
		if (startIndex != 0)
			path.lineTo(originPoint.x,originPoint.y);
		
		path.closePath();
		g2d.setColor(new Color(0.5f,0.6f,1f));
		g2d.fill(path);
		g2d.setColor(new Color(0.7f,0.7f,0.7f,0.7f));
		g2d.setFont(originalFont);
		
	}
	
	private void updatePositionOfPoints() {
		
		int from = graph.getLastStartIndex();
		int to = graph.size();
		int size = graph.getRelativeSize(from,to);
		
		double max = graph.getMax(from, to);
		int tens = getTens(max);
		
		correctPositionX(from, to, size);
		correctPositionY(from, to, size, tens);
		
		if (graph.getGraphType() == GraphType.RECTANGLE)
			updateSizesOfPoints(from, to, size);
		
	}

	private void updateSizesOfPoints(int from, int to, int size) {
		double width = (AIBox.endX-50)-originPoint.x;
		width -= 1d*(size-1);
		for (int i = from; i < to; i++) {
			graph.getPoints().get(i).setWidth(width/size);
			graph.getPoints().get(i).setHeight(originPoint.y-graph.getPoints().get(i).getY());
		}
	}
	
	private void correctPositionX(int from, int to, int size) {
		for (int i = from; i < to; i++)
			graph.getPoints().get(i).setX(xPosition((i+1)-from,size));
	}
	
	private void correctPositionY(int from, int to, int size, int tens) {
		for (int i = from; i < to; i++)
			graph.getPoints().get(i).setY(yPosition(graph.getValue(i+1),
					graph.getPoints().get(i).getHeight(),tens));
	}

	private double xPosition(int order, int size) {
		
		double width = (AIBox.endX-50) - originPoint.x;
		if (graph.getGraphType() == GraphType.NODE)
			return originPoint.x + (((double)order/size)*width) - 2;
		else {
			width -= 1d*(size-1);
			if (order == 1)
				return (double)originPoint.x + ((((double)order-1d)/(double)size)*width);
			else return (double)originPoint.x + ((((double)order-1d)/(double)size)*width) + 1d*(order-1);
		}
		
	}
	
	private double yPosition(double value, double height, int tens) {
		
		if (graph.getGraphType() == GraphType.NODE)
			return (double)((-((value/Math.pow(10, tens))*((double)AIBox.height/scalar)))+originPoint.y)-height/2;
		else return (double)((-((value/Math.pow(10, tens))*((double)AIBox.height/scalar)))+originPoint.y);
		
	}
	
	private int getTens(double value) {
		int count = 0;
		int n = (int)value;
		
		while (n >= 10) {
			n /= 10;
			count++;
		}
		
		return count;
	}
	
	private String getTensLabel(int tens) {
		String label = "";
		switch (tens) {
		case 1:
			label = "(10)";
			break;
		case 2:
			label = "(H)";
			break;
		case 3:
			label = "(K)";
			break;
		case 4:
			label = "(10K)";
			break;
		case 5:
			label = "(100K)";
			break;
		case 6:
			label = "(M)";
			break;
		}
		
		return label;
		
	}
	
	private void drawBaseGraphCoordinate(Graphics2D g2d) {
		
		double x = originPoint.x;
		double y = originPoint.y;
		double scalar = AIBox.height/this.scalar;
		for (int i = 0; i < 11; i++) {
			String label = i+"";
			int width = g2d.getFontMetrics().stringWidth(label);
			int labelX = (int) ((x + AIBox.x)/2d-width/2d);
			
			g2d.setColor(Color.WHITE);
			
			g2d.drawString(i+"", labelX, (int)(y+g2d.getFontMetrics().getHeight()/4));
			
			g2d.setColor(new Color(0.5f,0.5f,0.5f,0.25f));
			
			if (i == 0)
				g2d.setColor(Color.WHITE);
			
			g2d.draw(new Line2D.Double(x,y,AIBox.endX-50,y));
			
			if (i == 0 || i == 10)
				g2d.setColor(Color.WHITE);
			
			if (i == 0)
				g2d.draw(new Line2D.Double(AIBox.endX-50,y-10,AIBox.endX-50,y+10));
			else if (i == 10) {
					g2d.draw(new Line2D.Double(x-10,y,x+10,y));
					g2d.draw(new Line2D.Double(originPoint.x,originPoint.y,x,y));
			}
			
			y -= scalar;
		}
		
		String tensLabel = getTensLabel(tens);
		int width = g2d.getFontMetrics().stringWidth(tensLabel);
		g2d.drawString(tensLabel, (int)(((AIBox.x+x)/2d)-(width/2d)), (int)(originPoint.y+35));
		
	}
	
	private void drawLabels(Graphics2D g2d) {
		for (int i = graph.getStartIndex(); i < graph.getEndIndex(); i++) {
			ShapeGraph point = graph.getPoints().get(i);
			if (point.isDisplayed()) {
				String label = (point.getLabel())+"";
				int width = g2d.getFontMetrics().stringWidth(label);
				g2d.setColor(new Color(0.7f,0.7f,0.7f,0.7f));
				g2d.drawString(label, (int)(point.getX()+(point.getWidth()/2d)-width/2d), originPoint.y+g2d.getFontMetrics().getHeight()+5);
			}
		}
	}
	
	private double getCenterX(ShapeGraph shapeGraph) {
		return shapeGraph.getX()+(shapeGraph.getWidth()/2d);
	}
	private double getCenterY(ShapeGraph shapeGraph) {
		return shapeGraph.getY()+(shapeGraph.getHeight()/2d);
	}
	
}
