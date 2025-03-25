package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.LinkedList;
import java.util.Random;

import game.GameplayBox;

public class Vertex {

	public float x, y;
	public Color color;
	
	public Vertex(float x, float y) {
	
		this.x = x;
		this.y = y;
		
		color = Color.WHITE;
		
	}
	
	public Vertex(Vertex v) {
		this(v.x,v.y);
	}
	
	public Vertex(Vector vector) {
		this(vector.x,vector.y);
	}
	
	public Point getPoint() {
		return new Point((int)x,(int)y);
	}
	
	public void render(Graphics g, boolean colored) {
		if (colored)
			g.setColor(color);
		else g.setColor(Color.WHITE);
		float size = 2;
		g.fillRect((int)(x-(size/2f)), (int)(y-(size/2f)), (int)size, (int)size);
	}

	public void renderLine(Vertex vertex, Graphics g, boolean colored) {
		if (colored)
			g.setColor(color);
		else g.setColor(Color.WHITE);
		Graphics2D g2d = (Graphics2D) g;
		Line2D.Float line = new Line2D.Float(x,y, vertex.x, vertex.y);
		//g.drawLine((int)x, (int)y, (int)vertex.x, (int)vertex.y);
		g2d.draw(line);
	}
	
	public void renderLines(LinkedList<Vertex> vertices, Graphics g, boolean colored) {
		
		g.setColor(color);
		for (int i = 0; i < vertices.size()-1; i++) {
//			g.setColor(vertices.get(i).color);
//			vertices.get(i).render(g);
			vertices.get(i).renderLine(vertices.get(i+1), g, colored);
		}
		
//		vertices.getLast().render(g);
		
		g.setColor(vertices.getLast().color);
		vertices.getLast().renderLine(vertices.getFirst(), g, colored);
		
//		float minDist = Float.MAX_VALUE;
//		Vertex v = null;
//		
//		for (int i = 0; i < vertices.size(); i++) {
//			if (this == vertices.get(i)) continue;
//			float dist = getDistance(vertices.get(i));
//			if (dist < minDist) {
//				minDist = dist;
//				v = vertices.get(i);
//			}
//		}
//		
//		render(g);
//		renderLine(v, g);
		
	}
	
	public static Vertex randomVertex() {
		return new Vertex(GameplayBox.generateRandomXCoordinate(),GameplayBox.generateRandomYCoordinate());
	}
	
	public static Vertex randomVertex(Random random) {
		return new Vertex(GameplayBox.generateRandomXCoordinate(random),GameplayBox.generateRandomYCoordinate(random));
	}
	
	public float getDistance(Vertex other) {
		
		return (float)Math.sqrt( ((x-other.x)*(x-other.x)) + ((y-other.y)*(y-other.y)) );
		
	}
	
	public float getSlop(Vertex vertex) {
		
		return (y-vertex.y)/(x-vertex.x);
		
	}

	public Vertex clone() {
		return new Vertex(x,y);
	}
	
	public static float getCenterX(Vertex v1, Vertex v2) {
		return (v1.x + v2.x)/2f;
	}
	
	public static float getCenterY(Vertex v1, Vertex v2) {
		return (v1.y + v2.y)/2f;
	}
	
	@Override
	public String toString() {
		return String.format("(%f,%f)", x, y);
	}
	
}
