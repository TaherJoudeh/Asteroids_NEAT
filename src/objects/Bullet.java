package objects;

import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.LinkedList;

import game.Driver;

public class Bullet extends GameObject {

	private float x, y, velX, velY, width, height;
	private Player player;
	private Vector direction;
	
	private GameObjectHandler gameObjectHandler;
			
	public Bullet(float width, float height, float velX, float velY, GameObjectHandler gameObjectHandler) {
		super(width, height, gameObjectHandler);
		
		this.gameObjectHandler = gameObjectHandler;
	
		player = gameObjectHandler.getPlayer();
		
		this.x = player.getVertecies().getFirst().x;
		this.y = player.getVertecies().getFirst().y;
		
		direction = player.getDirection();
		
		initSize();
		initVelocity();
		
		createVertecies();
				
	}
	
	private void initSize() {
		width = 2;
		height = 2;
	}
	
	private void initVelocity() {
		velX = 10f;
		velY = 10f;
	}
	
	@Override
	public void update() {
		move();
		collisionDetection();
	}

	@Override
	public void render(Graphics g) {
//		g.setColor(Color.WHITE);
//		g.fillRect((int)(x-width/2), (int)(y-height/2), (int)width, (int)height);
		
		if (getVertecies().isEmpty())
			return;
		
		getVertecies().get(0).renderLine(getVertecies().get(1), g, false);
		getVertecies().get(0).renderLine(getVertecies().get(2), g, false);
		getVertecies().get(2).renderLine(getVertecies().get(3), g, false);
		getVertecies().get(3).renderLine(getVertecies().get(1), g, false);
	}
	
	private void move() {
		moveVertices();
		boolean removed = removeByCounter();
		if (removed) {
			player.incrementMissedShots();
			player.setLastBulletHit(false);
			player.resetShootComboCounter();
		}
		else teleport();
	}
	
	private void moveVertices() {
//		x += velX*direction.x;
//		y += velY*direction.y;
		for (Vertex v: getVertecies()) {
			v.x += velX*direction.x;
			v.y += velY*direction.y;
		}
		getCenter().x += velX*direction.x;
		getCenter().y += velY*direction.y;
	}
	
	private void createVertecies() {
		
		LinkedList<Vertex> vertices = new LinkedList<> ();
		vertices.add(new Vertex(x-width/2,y-height/2));
		vertices.add(new Vertex((x-width/2)+width,(y-height/2)));
		vertices.add(new Vertex(x-width/2,(y-height/2)+height/2));
		vertices.add(new Vertex((x-width/2)+width,(y-height/2)+height/2));
		
		setCenter(new Vertex(x+(width/2),y+(height/2)));
		
		setVertecies(vertices);
	}
	
	public void collisionDetection() {
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			GameObject go = gameObjectHandler.get(i);
			if (!(go instanceof Asteroid))
				continue;
			
			Vertex[] verts = collisionVectorsVertex(go);
			if (verts != null) {

				((Asteroid)go).setIsAlive(false);
				elliminate();
				
				switch (((Asteroid)go).getSize()) {
				case Asteroid.SMALL_Asteroid:
					player.scoreIncrement(10);
					break;
				case Asteroid.MEDIUM_Asteroid:
					player.scoreIncrement(5);
					break;
				case Asteroid.BIG_Asteroid:
					player.scoreIncrement(2);
					break;
				}
				
//				player.scoreIncrement();
				player.incrementAsteroidsShot();
				player.setLastBulletHit(true);
				break;
				
			}
		}
		
	}
	
	private Vertex[] collisionVectorsVertex(GameObject go) {
		
		for (int i = 0; i < go.getVertecies().size(); i++) {
			
			Vertex v1 = go.getVertecies().get(i),
					v2 = go.getVertecies().get((i+1)%go.getVertecies().size());
			
			Vertex vertex1 = getVertecies().get(0),
					vertex2 = getVertecies().get(1),
					vertex3 = getVertecies().get(2),
					vertex4 = getVertecies().get(3);
			
			if (Vector.linesIntersect(v1, v2, vertex1, vertex2) ||
				Vector.linesIntersect(v1, v2, vertex2, vertex3) ||
				Vector.linesIntersect(v1, v2, vertex3, vertex4) ||
				Vector.linesIntersect(v1, v2, vertex4, vertex1))
				return new Vertex[] {v1,v2};
			
			Line2D.Float lineAsteroid = new Line2D.Float(v1.x,v1.y,v2.x,v2.y);
			
			Line2D.Float line = new Line2D.Float(getCenter().x,getCenter().y,getCenter().x-direction.x*2*velX,getCenter().y-direction.y*2*velY);
			
			if (line.intersectsLine(lineAsteroid))
				return new Vertex[] {v1,v2};
			
		}
		
		return null;
	}

}
