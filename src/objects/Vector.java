package objects;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Vector {

	public float x, y;
	
	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Vector v) {
		this(v.x,v.y);
	}
	
	public Vector(Vertex v) {
		this(v.x,v.y);
	}
	
	public Vector(Vector v1, Vector v2) {
		this(v2.x - v1.x,v2.y - v1.y);
	}
	
	public Vector(Vertex v1, Vertex v2) {
		this(v1.x - v2.x,v1.y - v2.y);
	}
	
	public Vector(Line2D.Float line) {
		this(line.x2-line.x1,line.y2-line.y1);
	}
	
	public Vector(Line2D.Double line) {
		this((float)line.x2-(float)line.x1,(float)line.y2-(float)line.y1);
	}
	
	public void rotate(float angle, Vector center) {

		float length = getLength();
		
		Vector v = normalize(rotate(normalize(new Vector(x,y)), angle));
		
		v.x *= length;
		v.y *= length;
		
		if (center != null) {
			v.x += center.x;
			v.y += center.y;
		}
		
		x = v.x;
		y = v.y;
		
	}
	
	public float getAngle() {
		return (float)Math.atan2(y,x);
	}
	
	public float getAngle(Vector other) {
		Vector v = new Vector(x-other.x,y-other.y);
		return v.getAngle();
	}
	
	public float getLength() {
		return (float)Math.sqrt((x*x) + (y*y));
	}
	
	public static float getLength(double x, double y) {
		return (float)Math.sqrt((x*x) + (y*y));
	}
	
	public static Vector normalize(Vector v) {
		return new Vector(v.x/v.getLength(),v.y/v.getLength());
	}
	
	public Vector getCounterVector() {
		return new Vector(-x,-y);
	}
	
	public float cross(Vector v) {
		return x*v.y - y*v.x;
	}
	
	public float dot(Vector v) {
		return (x*v.x) + (y*v.y);
	}
	
	public static float cross(Vector v1, Vector v2) {
		return v1.x*v2.y - v1.y*v2.x;
	}
	
	public static Vector rotate(Vector vector, Vector center, double angle) {
		return new Vector( center.x + (vector.x*((float)Math.cos(angle))) - (vector.y*((float)Math.sin(angle))),
				center.y  + (vector.x*((float)Math.sin(angle))) + (vector.y*((float)Math.cos(angle))) );
	}
	
	public static Vector rotate(Vector vector, double angle) {
		return new Vector((vector.x*((float)Math.cos(angle))) - (vector.y*((float)Math.sin(angle))),
				(vector.x*((float)Math.sin(angle))) + (vector.y*((float)Math.cos(angle))) );
	}
	
	public static Vector rotateCG(Vector vector, Vector center, double angle) {
		return new Vector(vector.x * (float)Math.cos(angle) - vector.y * (float)Math.sin(angle),
				vector.x * (float)Math.sin(angle) + vector.y * (float)Math.cos(angle));
	}
	
	public static boolean linesIntersect(Vertex a1, Vertex a2, Vertex b1, Vertex b2) {
		return Line2D.linesIntersect(a1.x, a1.y, a2.x, a2.y, b1.x, b1.y, b2.x, b2.y);
	}
	
	public static double intersectionRatio(Line2D.Float l1, Line2D.Float l2) {
		
		Point2D p1 = new Point2D.Double(l1.getX1(),l1.getY1());
		Point2D p2 = new Point2D.Double(l1.getX2(), l1.getY2());
		Point2D q1 = new Point2D.Double(l2.getX1(), l2.getY1());
		Point2D q2 = new Point2D.Double(l2.getX2(), l2.getY2());

		 double dx1 = p2.getX() - p1.getX();
	        double dy1 = p2.getY() - p1.getY();
	        double dx2 = q2.getX() - q1.getX();
	        double dy2 = q2.getY() - q1.getY();

	        // Calculate the determinant
	        double determinant = dx1 * dy2 - dy1 * dx2;

	        // If the determinant is zero, the lines are parallel
	        if (Math.abs(determinant) < 1e-10)
	            return 1;

	        // Calculate the parameters t and u
	        double t = ((q1.getX() - p1.getX()) * dy2 - (q1.getY() - p1.getY()) * dx2) / determinant;
	        double u = ((q1.getX() - p1.getX()) * dy1 - (q1.getY() - p1.getY()) * dx1) / determinant;

	        // Check if the intersection point lies on both line segments
	        if (t >= 0 && t <= 1 && u >= 0 && u <= 1)
	            return t;
	        else
	            return 1;
	        
	}
	
	public static double dotProduct(Vector v1, Vector v2) {
		return v1.x*v2.x + v1.y*v2.y;
	}

	@Override
	public String toString() {
		return String.format("(%f,%f)", x, y);
	}
	
}
