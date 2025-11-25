package objects;

import java.awt.Graphics;
import java.util.LinkedList;

import game.GameplayBox;

public abstract class GameObject {
	
	private float width, height;
	
	private LinkedList<Vertex> vertices;
	private GameObjectHandler gameObjectHandler;
	private Vertex center;
		
	public GameObject(float width, float height, GameObjectHandler gameObjectHandler) {
		
		this.width = width;
		this.height = height;
		
		this.gameObjectHandler = gameObjectHandler;
		
		vertices = new LinkedList<> ();
		
		gameObjectHandler.addGameObject(this);
	}
	
	public Vertex getCenter() { return center; }
	public void setCenter(Vertex center) { this.center = center; }
	
	public float getWidth() { return width; }
	public void setWidth(float width) { this.width = width; }
	
	public float getHeight() { return height; }
	public void setHeight(float height) { this.height = height; }
	
	public LinkedList<Vertex> getVertecies() { return vertices; }
	public void setVertecies(LinkedList<Vertex> vertices) { this.vertices = vertices; }
		
	public GameObjectHandler getGameObjectHandler() { return gameObjectHandler; }
	
	public boolean addVertex(float x, float y) {
		if (!GameplayBox.isPointWithinGameplayBox(x, y))
			return false;
		vertices.add(new Vertex(x, y));
		return true;
	}
	
	public boolean addVertex(Vertex vertex) {
		if (!GameplayBox.isVertexWithinGameplayBox(vertex))
			return false;
		vertices.add(vertex);
		return true;
	}
	
	public float getFirstVertexXCoordinate() {
		
		float minX = Float.MAX_VALUE;
		
		for (int i = 0; i < getVertecies().size(); i++) {
			Vertex v = getVertecies().get(i);
			
			if (v.x < minX)
				minX = v.x;
		}
		
		return minX;
	}
	
	public float getLastVertexXCoordinate() {
		
		float maxX = Float.MIN_VALUE;
		
		for (int i = 0; i < getVertecies().size(); i++) {
			Vertex v = getVertecies().get(i);
			
			if (v.x > maxX)
				maxX = v.x;
		}
		
		return maxX;
	}
	
	public float getFirstVertexYCoordinate() {
		
		float minY = Float.MAX_VALUE;
		
		for (int i = 0; i < getVertecies().size(); i++) {
			Vertex v = getVertecies().get(i);
			
			if (v.y < minY)
				minY = v.y;
		}
		
		return minY;
	}
	public float getLastVertexYCoordinate() {
	
		float maxY = Float.MIN_VALUE;
		
		for (int i = 0; i < getVertecies().size(); i++) {
			Vertex v = getVertecies().get(i);
			
			if (v.y > maxY)
				maxY = v.y;
		}
		
		return maxY;
	}
	
	public void teleport() {
		if (getLastVertexXCoordinate() < GameplayBox.x)
			teleportToRight();
		if (getFirstVertexXCoordinate() > GameplayBox.endX)
			teleportToLeft();
		
		if (getLastVertexYCoordinate() < GameplayBox.y)
			teleportToDown();
		if (getFirstVertexYCoordinate() > GameplayBox.endY)
			teleportToUp();
	}
	
	public boolean remove() {
		if (getLastVertexXCoordinate() < GameplayBox.x ||
		getFirstVertexXCoordinate() > GameplayBox.endX ||
		getLastVertexYCoordinate() < GameplayBox.y ||
		getFirstVertexYCoordinate() > GameplayBox.endY) {
			gameObjectHandler.removeGameObject(this);
			return true;
		}
		return false;
	}

	
	public void elliminate() {
		if (this instanceof Asteroid)
			gameObjectHandler.decrementAstroidNum();
		gameObjectHandler.removeGameObject(this);
	}
	
	private void teleportToUp() {
		for (Vertex v: getVertecies())
			v.y += -GameplayBox.height-height+1;
		if (center != null)
			center.y += -GameplayBox.height-height+1;
	}
	private void teleportToDown() {
		for (Vertex v: getVertecies())
			v.y += GameplayBox.height+height-1;
		if (center != null)
			center.y += GameplayBox.height+height-1;
	}
	private void teleportToRight() {
		for (Vertex v: getVertecies())
			v.x += GameplayBox.width+width-1;
		if (center != null)
			center.x += GameplayBox.width+width-1;
	}
	private void teleportToLeft() {
		for (Vertex v: getVertecies())
			v.x += -GameplayBox.width-width+1;
		if (center != null)
			center.x += -GameplayBox.width-width+1;
	}
	
	public abstract void update();
	public abstract void render(Graphics g);
	
}
