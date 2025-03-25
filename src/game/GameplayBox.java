package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

import objects.Vertex;

public class GameplayBox {

	private static Random random = new Random();
	
	public static int boxX = Window.width/30,
			boxY = Window.height/20,
			boxWidth = Driver.AI ? Window.width/2 - boxX : Window.width-2*boxX,
			boxHeight = Window.height-Window.height/8;
	
	public static Rectangle gameplayBox = new Rectangle(boxX, boxY, boxWidth, boxHeight);
	public static int x = boxX,
			y = boxY,
			width = boxWidth,
			height = boxHeight,
			centerX = x+(width/2),
			centerY = y+(height/2);
	
	public static int endX = x + width,
			endY = y + height;
	
	public static Rectangle topBox = new Rectangle(x+100,y,width-200,100),
			bottomBox = new Rectangle(x+100,endY-100,width-200,100),
			rightBox = new Rectangle(endX-100,y+100,100,height-200),
			leftBox = new Rectangle(x,y+100,100,height-200),
			topRightCornerBox = new Rectangle(endX-100,y,100,100),
			topLeftCornerBox = new Rectangle(x,y,100,100),
			bottomRightCornerBox = new Rectangle(endX-100,endY-100,100,100),
			bottomLeftCornerBox = new Rectangle(x,endY-100,100,100);
	
	public static boolean isPointWithinGameplayBox(float x, float y) {
		return x > gameplayBox.x && x < gameplayBox.x+gameplayBox.width &&
				y > gameplayBox.y && y < gameplayBox.y+gameplayBox.height;
	}
	public static boolean isVertexWithinGameplayBox(Vertex vertex) {
		return isPointWithinGameplayBox(vertex.x, vertex.y);
	}
	
	public static int generateRandomXCoordinate() {		
		return random.nextInt(endX-x)+x;
	}
	
	public static int generateRandomXCoordinate(Random random) {
		return random.nextInt(endX-x)+x;
	}
	
	public static int generateRandomYCoordinate() {
		return random.nextInt(endY-y)+y; 
	}
	
	public static int generateRandomYCoordinate(Random random) {
		return random.nextInt(endY-y)+y;
	}
	
	public static Vertex generateRandomXCoordinateHorizantalBorders() {
		
		int xC = random.nextInt(endX-x)+x,
				yC = y;
		
		if (random.nextBoolean())
			yC = endY;
		
		return new Vertex(xC,yC);
		
	}
	
	public static Vertex generateRandomYCoordinateVerticalBorders() {
		int yC = random.nextInt(endY-y)+y,
				xC = x;
		
		if (random.nextBoolean())
			xC = endX;
		
		return new Vertex(xC,yC);
	}
	
public static Vertex generateRandomXCoordinateHorizantalBorders(Random random) {
		
		int xC = random.nextInt(endX-x)+x,
				yC = y;
		
		if (random.nextBoolean())
			yC = endY;
		
		return new Vertex(xC,yC);
		
	}
	
	public static Vertex generateRandomYCoordinateVerticalBorders(Random random) {
		int yC = random.nextInt(endY-y)+y,
				xC = x;
		
		if (random.nextBoolean())
			xC = endX;
		
		return new Vertex(xC,yC);
	}
	
	public static Vertex generateRandomBorderVertex() {
		
		Vertex v = random.nextBoolean() ? generateRandomXCoordinateHorizantalBorders() : generateRandomYCoordinateVerticalBorders();
		return v;
		
	}
	
	public static Vertex generateRandomBorderVertex(Random random) {
		
		Vertex v = random.nextBoolean() ? generateRandomXCoordinateHorizantalBorders(random) : generateRandomYCoordinateVerticalBorders(random);
		return v;
		
	}
	
	public static void update() {
		gameplayBox.x = boxX;
		gameplayBox.y = boxY;
		gameplayBox.width = boxWidth;
		gameplayBox.height = boxHeight;
		
		x = boxX;
		y = boxY;
		width = boxWidth;
		height = boxHeight;
		
		endX = x + width;
		endY = y + height;
	}
	
	public static void render(Graphics g) {
		g.setColor(Color.WHITE);
		
		//Render border
		g.drawRect(x, y, width, height);
	}
	
}
