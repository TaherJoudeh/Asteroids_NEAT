package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

public class AIBox {

	private static Random random = new Random();
	
	private static int margin = 5;
	
	public static int x = GameplayBox.endX + margin,
			y = GameplayBox.y,
			width = GameplayBox.boxWidth,
			height = GameplayBox.height;
	
	public static int endX = x + width,
			endY = y + height;
	
	public static int centerX = x + width/2,
			centerY = y + height/2;
	
	public static Rectangle aiBox = new Rectangle(x,y,width,height);
	
	public static int generateRandomXCoordinate() {		
		return random.nextInt(endX-x)+x;
	}
	
	public static int generateRandomYCoordinate() {
		return random.nextInt(endY-y)+y; 
	}
	
	public static void render(Graphics g) {
		
		g.setColor(Color.WHITE);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.draw(aiBox);
		
	}
	
}
