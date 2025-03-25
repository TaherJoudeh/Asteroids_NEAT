package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import game.GameplayBox;

public class Asteroid extends GameObject {
	
	public final static int BIG_Asteroid = 24,
			MEDIUM_Asteroid = 18,
			SMALL_Asteroid = 12;
	
	public final static float MAX_SPEED = 5;
	public final static float MAX_INIT_SPEED = 1.5f;
	
	private final int NUMBER_OF_VERTECIES = 12;
	
	private Random random;
	
	private float width, height,
				velX, velY;
	
	private int size;
	
	private boolean isAlive;
		
	public Asteroid(float width, float height, GameObjectHandler gameObjectHandler) {
		super(width, height, gameObjectHandler);
		isAlive = true;
		random = gameObjectHandler.getRandom();

		initSize(0);
		createVertices(null);
		initVelocity(false, null , 0);
		initWidthAndHeight();
		gameObjectHandler.incrementAstroidNum();
	}
	
	public Asteroid(float width, float height, GameObjectHandler gameObjectHandler, int size, Vertex origin, Vector dir, float speed) {
		super(width, height, gameObjectHandler);
		isAlive = true;
		random = gameObjectHandler.getRandom();

		initSize(size);
		createVertices(origin);
		initVelocity(true, dir, speed);
		initWidthAndHeight();
		gameObjectHandler.incrementAstroidNum();
	}
	
	public Asteroid(float width, float height, GameObjectHandler gameObjectHandler, Vertex origin) {
		super(width, height, gameObjectHandler);
		isAlive = true;
		random = gameObjectHandler.getRandom();
		
		initSize(0);
		createVertices(origin);
		initVelocity(false, null , 0);
		initWidthAndHeight();
		gameObjectHandler.incrementAstroidNum();
	}
	
	public Vector getDir() {
		return Vector.normalize(new Vector(velX, velY));
	}
	
	public boolean isAlive() { return isAlive; }
	public void setIsAlive(boolean alive) { isAlive = alive; }
	
	public int getSize() { return size; }
	
	public double getVelocityDirection() { return Math.atan((double)velY/(double)velX); }
	public double getAverageVelocity() { return Math.sqrt((velX*velX)+(velY*velY)); }
	
	@Override
	public void update() {

		move();
		teleport();
		
	}

	@Override
	public void render(Graphics g) {
		for (int i = 0; i < getVertecies().size(); i++)
			getVertecies().get(i).renderLines(getVertecies(), g, false);
		
//		getCenter().render(g, true);
//		Graphics2D g2d = (Graphics2D) g;
//		g2d.setColor(Color.RED);
//		g2d.draw(getBounds());
	}
	
	private void initSize(int size) {
		
		if (size == 0) {
			int detSize = random.nextInt(BIG_Asteroid);
		
			if (detSize > MEDIUM_Asteroid)
				this.size = BIG_Asteroid;
			else if (detSize > SMALL_Asteroid)
				this.size = MEDIUM_Asteroid;
			else
				this.size = SMALL_Asteroid;
			
			/*
			switch (this.size) {
			case BIG_Asteroid:
				getGameObjectHandler().incrementAstroidNum(7);
				break;
			case MEDIUM_Asteroid:
				getGameObjectHandler().incrementAstroidNum(3);
				break;
			default:
				getGameObjectHandler().incrementAstroidNum();
				break;
			}
			*/
		}else this.size = size;
		
	}
	
	private void initWidthAndHeight() {
		width = getLastVertexXCoordinate() - getFirstVertexXCoordinate();
		height = getLastVertexYCoordinate() - getFirstVertexYCoordinate();
	}
	
	private Rectangle2D.Float getBounds() {
		return new Rectangle2D.Float(getFirstVertexXCoordinate(), getFirstVertexYCoordinate(),
				width, height);
	}
	
	private void initVelocity(boolean isChild, Vector dir, float speed) {
		
		if (isChild) {
			
			speed = Math.min(MAX_SPEED, speed);
			
			velX = dir.x*speed;
			velY = dir.y*speed;
			
			return;
		}
		
		float minSpeed = 1f;
		
		velX = (random.nextFloat()*(MAX_INIT_SPEED-minSpeed)+minSpeed);
		velY = (random.nextFloat()*(MAX_INIT_SPEED-minSpeed)+minSpeed);
		
		velX = random.nextBoolean() ? velX : -velX;
		velY = random.nextBoolean() ? velY : -velY;
		
	}
	
	private void createVertices(Vertex origin) {
				
		LinkedList<Vertex> vertices = new LinkedList<> ();
//				correctedVertices = new LinkedList<> ();
		
		float scalar = size*3.5f;
		
		Vertex originVertex = null;
		
		if (origin == null)
			originVertex = new Vertex(GameplayBox.generateRandomBorderVertex(random));
		else originVertex = origin;
		
		setCenter(new Vertex(originVertex.x, originVertex.y));
		getCenter().color = Color.GREEN;
		
		double angleBetweenVertices = Math.toRadians(360d/NUMBER_OF_VERTECIES);
		
		Vector randomDir = new Vector((float)random.nextGaussian(),(float)random.nextGaussian());
		randomDir = Vector.normalize(randomDir);
		
		float minRadius = 0.5f;
		float maxRadius = 0.75f;
		
		float minXVertex = Float.MAX_VALUE, maxXVertex = 0;
		float minYVertex = Float.MAX_VALUE, maxYVertex = 0;
		
		for (int i = 0; i < NUMBER_OF_VERTECIES; i++) {
			 
			float randomRadius = ((random.nextFloat()*(maxRadius-minRadius))+minRadius) * scalar;
			float dirX = randomDir.x*randomRadius,
					dirY = randomDir.y*randomRadius;
			Vertex v = new Vertex( getCenter().x+dirX,
					getCenter().y+dirY);
			vertices.add(v);
			
			if (minXVertex > v.x)
				minXVertex = v.x;
			if (maxXVertex < v.x)
				maxXVertex = v.x;

			if (minYVertex > v.y)
				minYVertex = v.y;
			if (maxYVertex < v.y)
				maxYVertex = v.y;
			
			randomDir.rotate((float)angleBetweenVertices, null);
		}
		
		setWidth(maxXVertex-minXVertex);
		setHeight(maxYVertex-minYVertex);
		
		/*
		while (!vertices.isEmpty())
		for (int i = 0; i < vertices.size(); i++) {
			float minAngle = Float.MAX_VALUE;
			int index = i;
			
			for (int j = 0; j < vertices.size(); j++) {
				Vertex vj = vertices.get(j);

				float angle = (float)Math.atan2( getCenter().y-vj.y , vj.x-getCenter().x );
				
				if (angle < minAngle) {
					minAngle = angle;
					index = j;
				}
			}
			correctedVertices.add(vertices.remove(index));
		}
				*/
		super.setVertecies(vertices);
		
	}
	
	private void move() {
		for (Vertex v: getVertecies()) {
			v.x += velX;
			v.y += velY;
		}
		getCenter().x += velX;
		getCenter().y += velY;
	}
	
}
