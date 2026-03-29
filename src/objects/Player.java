package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Random;
import game.CanvasFrame;
import game.Driver;
import game.GameplayBox;
import main.java.neat.core.Agent;

public class Player extends GameObject {
	
	public static boolean immortal = false;
	
	public final static double MAX_VEL = 6;
	public final static double UNIT = 0.005;
	
	private Random random;
	
	private Vector direction, velocityAcceleration;
	
	private Agent agent;
	
	private float width, height,
		velX, velY;
	
	private float size;
	
	private final float angle = Driver.AI ? (float)Math.PI/50 : (float)Math.PI/70;
			
	private float accelerationX = 0,
			accelerationY = 0;
	
	private int bulletsShot, asteroidsShot, missedShots;
	
	private GameObjectHandler gameObjectHandler;
	
	private int score, shootedAsteroids;
	private double acceleratedTime, aliveTime, survivedSeconds, totalTimeSurvived;
	private double rewardOfDistance = 0;
	
	private int count = 1;
	private boolean isAlive;
	private double counter = 1;
	
	public static final double LIFE_SPAN = 20, MAX_LIFE_SPAN = LIFE_SPAN;
	private double lifeSpan = LIFE_SPAN, highestLifeSpan = 0;
	private double minLifeSpan = lifeSpan;
	
	private Rectangle2D.Float hitBox;
	
	public final static float WHISKER_LENGTH = 500;
	public final static int NUMBER_OF_WHISKERS = 16;
	private Line2D.Float[] whiskers = new Line2D.Float[NUMBER_OF_WHISKERS];
	private float[] intersectionAsteroids = new float[NUMBER_OF_WHISKERS];
		
	private boolean inDangerShouldEscape = false, inDangerShouldEvade = false;
	private int dangerLevelEscaping;
	private int EscapingTimes = 0, EscapingTime;
	private int evadingTimes, evadingTime;
	
	private double noDangerTimes;
	private double noDangerTimesCount;
	
	private boolean lastBulletHit;
	
	private final static int MAX_SHOT_COMBO_COUNTER = 2;
	private int shootComboCounter = 0;
		
	private final int DEFAULT_SHOOTING_RATE = Driver.AI ? 500 : 300;
	private int shootingRate = DEFAULT_SHOOTING_RATE;
	private double counterForShooting = 0;
	
	private int threatBoxWidth = 300, threatBoxHeight = 300;
	
	private Rectangle2D.Double topThreatBox = new Rectangle2D.Double(0,GameplayBox.y-threatBoxHeight/2,threatBoxWidth,threatBoxHeight),
				downThreatBox = new Rectangle2D.Double(0,GameplayBox.endY-threatBoxHeight+threatBoxHeight/2,threatBoxWidth,threatBoxHeight),
				rightThreatBox = new Rectangle2D.Double(GameplayBox.endX-threatBoxHeight+threatBoxHeight/2,0,threatBoxHeight,threatBoxWidth),
				leftThreatBox = new Rectangle2D.Double(GameplayBox.x-threatBoxHeight/2,0,threatBoxHeight,threatBoxWidth);
			
	private double fitness;
	private double accu_fitness;
	
	private double cooldownShot;
	
	private Object[] closest;
	
	public Player(float width, float height, float velX, float velY,
			GameObjectHandler gameObjectHandler) {
		super(width, height, gameObjectHandler);
		
		this.width = width;
		this.height = height;
		this.velX = velX;
		this.velY = velY;
		this.gameObjectHandler = gameObjectHandler;
		
		random = gameObjectHandler.getRandom();
		
		velocityAcceleration = new Vector(0,0);
		
		score = 0;
		isAlive = true;
		
		initSize();
		randomizeDirection();
		
		createVertices();
		
		hitBox = new Rectangle2D.Float(getCenter().x-200, getCenter().y-200,400,400);
	}
	
	public Agent getAgent() { return agent; }
	public void setAgent(Agent agent) { this.agent = agent; }
		
	public int getShootComboCounter() { return shootComboCounter; }
	public void setShootComboCounter(int shootComboCounter) { this.shootComboCounter = shootComboCounter; }
	public void resetShootComboCounter() { shootComboCounter = 0; }
	public void incrementShootComboCounter() {
		shootComboCounter++;
		if (shootComboCounter > MAX_SHOT_COMBO_COUNTER)
			shootComboCounter = MAX_SHOT_COMBO_COUNTER;
	}
	
	public double getAccuracy() {
		return bulletsShot == 0 ? 1d : asteroidsShot/(double)bulletsShot;
	}
	
	public boolean lastBulletHit() { return lastBulletHit; }
	public void setLastBulletHit(boolean lastBulletHit) { this.lastBulletHit = lastBulletHit; }
	
	public double getTotalTimeSurvivedScore() { return totalTimeSurvived; }
	
	public double getSurvivedSeconds() { return survivedSeconds; }
	
	public void scoreIncrementWithCombo() {
		score += (1+shootComboCounter);
	}
	
	public void scoreIncrement() {
		score++;
	}	
	public void scoreIncrement(int value) {
		score += value;
	}
	
	public double getHighestLifeSpan() { return highestLifeSpan; }
	
	public double getGameCounter() { return counter; }
	
	public double getAliveTime() { return aliveTime; }
	public void resetAliveTime() { aliveTime = 0; }
	public void setAliveTime(double aliveTime) { this.aliveTime = aliveTime; }
	
	public double getAcceleratedTime() { return acceleratedTime; }

	public int getShootedAsteroid() { return shootedAsteroids; }
	public void shootedAsteroidIncrement() { shootedAsteroids++; }
	public void shootedAsteroidIncrement(int x) { shootedAsteroids += x; }
	
	public int getBulletsShooted() { return bulletsShot; }
	public void incrementBulletsShoot() { this.bulletsShot++; }
	
	public int getMissedShots() {
		return missedShots;
	}
	public void incrementMissedShots() { missedShots++; }
	
	public int getAsteroidsShot() { return asteroidsShot; }
	public void incrementAsteroidsShot() { asteroidsShot++; }
			
	public int getScore() { return score; }
	public void setScore(int score) { this.score = score; }
	
	public Vector getDirection() { return direction; }
	
	public boolean isAlive() { return isAlive; }
	public void setIsAlive(boolean isAlive) { this.isAlive = isAlive; }
	
	public double getLifeSpan() { return lifeSpan; }
	public double getMinLifeSpan() { return minLifeSpan; }
	
	public double getDistanceReward() { return rewardOfDistance; }
	
	public double getEscapingTimes() { return EscapingTimes; }
	public double getEvadingTimes() { return evadingTimes; }
	
	public double getNoDangerTimes() { return noDangerTimes; }
	
	public double getFitness() { return fitness; }
	
	public double getAccuFitness() { return accu_fitness; }
	public void resetAccuFitness() { accu_fitness = 0; }
	
	private void updateWhiskers(boolean resetLengths) {
		
		if (resetLengths)
			for (int i = 0; i < NUMBER_OF_WHISKERS; i++)
				intersectionAsteroids[i] = WHISKER_LENGTH;
		
		Vertex center = getVertecies().get(2);
		
		whiskers[0] = new Line2D.Float(center.x,center.y,center.x+(direction.x*intersectionAsteroids[0]),center.y+(direction.y*intersectionAsteroids[0]));
		Vector vector = new Vector(whiskers[0]);
		
		for (int i = 1; i < NUMBER_OF_WHISKERS; i++) {
			vector = Vector.normalize(Vector.rotate(vector, Math.PI/(0.5*NUMBER_OF_WHISKERS)));
			whiskers[i] = new Line2D.Float(center.x,center.y,center.x+(vector.x*intersectionAsteroids[i]),center.y+(vector.y*intersectionAsteroids[i]));
		}
		
	}
	
	@Override
	public void update() {
		
		if (isAlive) {
			for (Vertex v: getVertecies()) {
				v.x += velX;
				v.y += velY;
			}
			getCenter().x += velX;
			getCenter().y += velY;
			
			closest = getClosest();
			
			topThreatBox.x = getCenter().x-(threatBoxWidth/2);
			downThreatBox.x = getCenter().x-(threatBoxWidth/2);
			rightThreatBox.y = getCenter().y-(threatBoxWidth/2);
			leftThreatBox.y = getCenter().y-((threatBoxWidth/2));
			
			counter += 0.01;
			survivedSeconds += UNIT;
			
			hitBox.x = getVertecies().get(2).x-200;
			hitBox.y = getVertecies().get(2).y-200;
			
			teleport();
			
			if (!immortal)
				collisionDetection();
			
			if (Driver.AI) {
			if (lifeSpan <= 0) {
				lifeSpan = 0;
				kill();
			}
			
			if (lifeSpan < minLifeSpan)
				minLifeSpan = lifeSpan;

			if (lifeSpan-LIFE_SPAN > highestLifeSpan)
				highestLifeSpan = lifeSpan-LIFE_SPAN;
			}
			
			if (count%7 == 0 && counterForShooting < shootingRate)
				counterForShooting += 50;
			
			count++;
			
			checkIfInDanger();
			getWhiskerCollisionOptimized();
			rewardForDistanceAndIsolation();

			if (Driver.AI) {
				double[] inputs = getUpdatedInputs();
				
				double[] dec = agent.think(inputs);
				if (dec[0] > 0.5)
					accelerate();
				else
					decelerate();
				
				if (dec[1] > 0.5)
					shoot();
				
				if (dec[2] > 0.5 && dec[2] > dec[3])
					rotate(true);
				else if (dec[3] > 0.5 && dec[3] > dec[2])
					rotate(false);
				
				calculateFitness();
												
			}
			
		}
		
	}
	
	private void calculateFitness() {
		double accuracy = (0.4d + 0.6d*Math.pow(getAccuracy(),2));
		
		fitness = 1 + survivedSeconds;
		fitness *= (1+500*noDangerTimes);
	    
	    fitness += 200*(asteroidsShot*asteroidsShot) * accuracy;
	    
	    if (acceleratedTime == 0)
	    	fitness *= 0.2d;
	    
	    double cooldownProp = cooldownShot / (survivedSeconds + UNIT);
	    fitness *= (0.2d + 0.8d * (1d - cooldownProp));
	}
	
	@Override
	public void render(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		
		boolean hasColor = true;
		
		getVertecies().getFirst().renderLine(getVertecies().get(1), g2d, hasColor);
		getVertecies().getFirst().renderLine(getVertecies().get(3), g2d, hasColor);
		
		getVertecies().get(2).renderLine(getVertecies().get(1), g2d, hasColor);
		getVertecies().get(2).renderLine(getVertecies().get(3), g2d, hasColor);
		
		Color aproximityColor = new Color(1f,0f,0f,1f);
		g2d.setColor(aproximityColor);
		
		if (CanvasFrame.showWhiskers) {
			
			g2d.setColor(new Color(255,0,0,50));
			
			if (Driver.AI) {
				
				g2d.setColor(new Color(1f,0f,0f,0.35f));
				for (int i = 0; i < whiskers.length; i++)
					if (whiskers[i] != null)
						g2d.draw(whiskers[i]);
					
			}
		}
			
		
	}

	public void shoot() {
		if (counterForShooting >= shootingRate) {
			new Bullet(2,2,0, 0, gameObjectHandler);
			counterForShooting = 0;
			
			bulletsShot++;
		}else cooldownShot += UNIT;
		
	}
	
	public void kill() {
		isAlive = false;
		gameObjectHandler.setIsActive(false);
		gameObjectHandler.onlyPlayer();
	}
	
	public boolean hasVelocity() {
		if (velX == 0 && velY == 0)
			return false;
		return true;
	}
	
	public double[] getUpdatedInputs() {
		
		double[] inp = new double[CanvasFrame.neatConfig.getNumberOfInputs()];
		
		int index = 0;

		Vector velocityVector = Vector.normalize(new Vector(velX,velY));
		
		inp[index++] = 2*(Math.sqrt((velX*velX) + (velY*velY))/MAX_VEL)-1;
		inp[index++] = Math.cos(velocityVector.getAngle());
		inp[index++] = Math.sin(velocityVector.getAngle());
		
		// Player Direction
		inp[index++] = Math.cos(direction.getAngle());
		inp[index++] = Math.sin(direction.getAngle());
				
		for (int i = 0; i < intersectionAsteroids.length; i++)
			inp[index++] = (2*(WHISKER_LENGTH-intersectionAsteroids[i])-(WHISKER_LENGTH))/WHISKER_LENGTH;
		
		inp[index++] = 2*(counterForShooting/(double)shootingRate)-1;
		
		return inp;
		
	}
	
	public void accelerate() {
		accelerationX = Driver.AI ? direction.x/12f : direction.x/30f;
		accelerationY = Driver.AI ? direction.y/12f : direction.y/30f;
	
		velX += accelerationX;
		velY += accelerationY;
		
		velocityLimit();

		velocityAcceleration.x = velX;
		velocityAcceleration.y = velY;
		
		acceleratedTime += 0.005;
	}
	
	public void decelerate() {
		
		if (velX == 0 && velY == 0)
			return;
				
		velX -= velocityAcceleration.x/70d;
		velY -= velocityAcceleration.y/70d;
				
		if (Vector.getLength(velX, velY) < 1e-2) {
			velX = 0;
			velY = 0;
			velocityAcceleration.x = 0;
			velocityAcceleration.y = 0;
		}
		
	}
	
	public void rotate(boolean isClockwise) {
				
		float actualAngle = -angle;
		if (isClockwise)
			actualAngle = angle;
			
		Vertex frontVertex = getVertecies().getFirst(),
				corner1Vertex = getVertecies().get(1),
				shipCenterVertex = getVertecies().get(2),
				corner2Vertex = getVertecies().getLast();
		
		Vector centerOfShip = new Vector(shipCenterVertex.x,shipCenterVertex.y);
		
		Vector frontToCenter = new Vector(frontVertex,shipCenterVertex),
				corner1ToCenter = new Vector(corner1Vertex,shipCenterVertex),
				corner2ToCenter = new Vector(corner2Vertex, shipCenterVertex);
		
		direction = Vector.normalize(frontToCenter);

		frontToCenter.rotate(actualAngle,centerOfShip);
		corner1ToCenter.rotate(actualAngle,centerOfShip);
		corner2ToCenter.rotate(actualAngle,centerOfShip);
		
		frontVertex.x = frontToCenter.x;
		frontVertex.y = frontToCenter.y;
		
		corner1Vertex.x = corner1ToCenter.x;
		corner1Vertex.y = corner1ToCenter.y;
		
		corner2Vertex.x = corner2ToCenter.x;
		corner2Vertex.y = corner2ToCenter.y;
						
	}
	
	private void velocityLimit() {
		Vector velocityVector = new Vector(velX,velY);
		if (velocityVector.getLength() > MAX_VEL) {
			double theta = velocityVector.getAngle();
			
			velX = (float) (MAX_VEL*Math.cos(theta));
			velY = (float) (MAX_VEL*Math.sin(theta));
		}
	}
	
	private void initSize() {
		height = 24;
		width = 24;
		
		size = (float)Math.sqrt(height*height + width*width);
	}
	
	private void randomizeDirection() {
		
		float dirX = 2*(random.nextFloat())-1,
				dirY = 2*(random.nextFloat())-1;
		direction = Vector.normalize(new Vector(dirX,dirY));

	}
	
	private void createVertices() {
				
		LinkedList<Vertex> vertices = new LinkedList<> ();

		Vector centerVector = new Vector(GameplayBox.centerX,GameplayBox.centerY);
		Vector frontVector = new Vector(centerVector.x+(direction.x*(size/2f)),centerVector.y+(direction.y*(size/2f)));
				
		Vector back1Vector = Vector.normalize(Vector.rotate(direction.getCounterVector(), Math.PI/6));
		Vector back2Vector = Vector.normalize(Vector.rotate(direction.getCounterVector(), -Math.PI/6));
		
		back1Vector.x *= size;
		back1Vector.y *= size;
		
		back2Vector.x *= size;
		back2Vector.y *= size;
		
		back1Vector.x += frontVector.x;
		back1Vector.y += frontVector.y;
		
		back2Vector.x += frontVector.x;
		back2Vector.y += frontVector.y;
		
		Vertex front = new Vertex(frontVector),
				corner1 = new Vertex(back1Vector),
				corner2 = new Vertex(back2Vector);
		
		Vertex shipCenter = new Vertex(frontVector.x+(direction.getCounterVector().x*size/2f),frontVector.y+(direction.getCounterVector().y*size/2f));
		
		Color colorOfShip = new Color(34,189,150);
		
		front.color = corner1.color = corner2.color = shipCenter.color = colorOfShip;
		
		vertices.add(front);
		vertices.add(corner1);
		vertices.add(shipCenter);
		vertices.add(corner2);
		
		setCenter(shipCenter.clone());
		
		setVertecies(vertices);
	}
	
	public void startOver() {

		randomizeDirection();
		
		lifeSpan = LIFE_SPAN;
		highestLifeSpan = 0;
		minLifeSpan = lifeSpan;
		survivedSeconds = 0;
		totalTimeSurvived = 0;
		rewardOfDistance = 0;
				
		lastBulletHit = false;
		
		shootComboCounter = 0;
		
		EscapingTimes = 0;
		EscapingTime = 0;
		inDangerShouldEscape = false;
		
		evadingTimes = 0;
		evadingTime = 0;
		dangerLevelEscaping = 0;
		inDangerShouldEvade = false;
		
		noDangerTimes = 0;
		noDangerTimesCount = 0;
		
		score = 0;
		shootedAsteroids = 0;
		acceleratedTime = 0;
		aliveTime = 0;
		asteroidsShot = 0;
		bulletsShot = 0;
		missedShots = 0;
		
		counterForShooting = 0;
		
		count = 1;
		counter = 1;
		velX = 0;
		velY = 0;
		
		shootingRate = DEFAULT_SHOOTING_RATE;
		
		cooldownShot = 0;
		
		createVertices();
		
		isAlive = true;
		
	}
	
	public void collisionDetection() {
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			GameObject go = gameObjectHandler.get(i);
			if (go instanceof Asteroid && (((Asteroid) go).hasHitbox()) && collisionVectors(go)) {
				isAlive = false;
				CanvasFrame.numberOfDeadAgents++;
				go.elliminate();
				break;
			}
		}
		
		if (!isAlive) {
			gameObjectHandler.setIsActive(isAlive);
			gameObjectHandler.onlyPlayer();
			accu_fitness += fitness;
			agent.setFitness(accu_fitness/(double)CanvasFrame.EVALUATE_X_GENERATION);
			if (!Driver.AI)
				startOver();
		}
		
	}
	
	private boolean collisionVectors(GameObject go) {
		
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
				return true;
			
		}
		
		return false;
	}
	
	private Object[] getClosest() {
	
		Vertex vMin = null;
		Asteroid ast = null;
		double min_dist = Double.POSITIVE_INFINITY;
		Vertex origVertex = null;
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			if ( !(gameObjectHandler.get(i) instanceof Asteroid) ) continue;
			
			Asteroid asteroid = (Asteroid) gameObjectHandler.get(i);
			
			for (Vertex vertex: asteroid.getVertecies()) {
	
				Vertex closest = getClosest(vertex, asteroid);
				double closestDist = getCenter().getDistance(closest);
				if (closestDist < min_dist) {
					vMin = closest;
					min_dist = closestDist;
					ast = asteroid;
					origVertex = vertex;
				}
				
			}
			
		}
		
		if (vMin == null)
			return new Object [] {null,null,null,null};
		
		return new Object [] {ast,vMin,min_dist,origVertex};
		
	}
	
	// Use it if you wish to improve the AI
	@SuppressWarnings("unused")
	private Object[] getSecondClosest() {
		
		Vertex clo = (Vertex)closest[1];
		
		Vertex vMin = null;
		Asteroid ast = null;
				
		if (clo == null)
			return new Object [] {null,null};
			
		double min_dist = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			if ( !(gameObjectHandler.get(i) instanceof Asteroid)) continue;
			
			Asteroid asteroid = (Asteroid) gameObjectHandler.get(i);
			
			if (asteroid.getVertecies().contains((Vertex)closest[3])) continue;
			
			for (Vertex vertex: asteroid.getVertecies()) {
				
				Vertex secClosest = getClosest(vertex, asteroid);
				double dist = getCenter().getDistance(secClosest);
				if (dist < min_dist && dist > (double)closest[2]) {
					vMin = secClosest;
					min_dist = dist;
					ast = asteroid;
				}
				
			}
			
		}
		
		if (vMin == null)
			return new Object [] {null,null};
		
		return new Object [] {ast,vMin};
		
	}
	
	private void getWhiskerCollisionOptimized() {
		
		updateWhiskers(true);
		float width = GameplayBox.boxWidth,
				height = GameplayBox.boxHeight;
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			
			if (!(gameObjectHandler.get(i) instanceof Asteroid))
				continue;
			Asteroid ast = (Asteroid) gameObjectHandler.get(i);
			
			for (int j = 0; j < whiskers.length; j++) {
				
				for (int k = 0; k < ast.getVertecies().size(); k++) {
				
					float w = ast.getWidth(), h = ast.getHeight();
					
					Vertex v1 = ast.getVertecies().get(k),
							v2 = ast.getVertecies().get((k+1)%ast.getVertecies().size());
				
					Line2D.Float line = new Line2D.Float(v1.x,v1.y,v2.x,v2.y);
					Line2D.Float lineUp = new Line2D.Float(v1.x,v1.y-height-h,v2.x,v2.y-height-h);
					Line2D.Float lineDown = new Line2D.Float(v1.x,v1.y+height+h,v2.x,v2.y+height+h);
					Line2D.Float lineRight = new Line2D.Float(v1.x+width+w,v1.y,v2.x+width+w,v2.y);
					Line2D.Float lineLeft = new Line2D.Float(v1.x-width-w,v1.y,v2.x-width-w,v2.y);
				
					Line2D.Float lineUpRight = new Line2D.Float(v1.x+width+w,v1.y-height-h,v2.x+width+w,v2.y-height-h);
					Line2D.Float lineUpLeft = new Line2D.Float(v1.x-width-w,v1.y-height-h,v2.x-width-w,v2.y-height-h);
					Line2D.Float lineDownRight = new Line2D.Float(v1.x+width+w,v1.y+height+h,v2.x+width+w,v2.y+height+h);
					Line2D.Float lineDownLeft = new Line2D.Float(v1.x-width-w,v1.y+height+h,v2.x-width-w,v2.y+height+h);				
					
					float dist = intersectionAsteroids[j];
					
					if (whiskers[j].intersectsLine(line)) {
						float distance = (float)(WHISKER_LENGTH*Vector.intersectionRatio(whiskers[j],line));
												
						if (dist > distance)
							dist = distance;
					}
					if (whiskers[j].intersectsLine(lineUp)) {
						float distance = (float)(WHISKER_LENGTH*Vector.intersectionRatio(whiskers[j],lineUp));
												
						if (dist > distance)
							dist = distance;
					}
					if (whiskers[j].intersectsLine(lineDown)) {
						float distance = (float)(WHISKER_LENGTH*Vector.intersectionRatio(whiskers[j],lineDown));
												
						if (dist > distance)
							dist = distance;
					}
					if (whiskers[j].intersectsLine(lineRight)) {
						float distance = (float)(WHISKER_LENGTH*Vector.intersectionRatio(whiskers[j],lineRight));
												
						if (dist > distance)
							dist = distance;
					}
					if (whiskers[j].intersectsLine(lineLeft)) {
						float distance = (float)(WHISKER_LENGTH*Vector.intersectionRatio(whiskers[j],lineLeft));
												
						if (dist > distance)
							dist = distance;
					}
					if (whiskers[j].intersectsLine(lineUpRight)) {
						float distance = (float)(WHISKER_LENGTH*Vector.intersectionRatio(whiskers[j],lineUpRight));
												
						if (dist > distance)
							dist = distance;
					}
					if (whiskers[j].intersectsLine(lineUpLeft)) {
						float distance = (float)(WHISKER_LENGTH*Vector.intersectionRatio(whiskers[j],lineUpLeft));
												
						if (dist > distance)
							dist = distance;
					}
					if (whiskers[j].intersectsLine(lineDownRight)) {
						float distance = (float)(WHISKER_LENGTH*Vector.intersectionRatio(whiskers[j],lineDownRight));
												
						if (dist > distance)
							dist = distance;
					}
					if (whiskers[j].intersectsLine(lineDownLeft)) {
						float distance = (float)(WHISKER_LENGTH*Vector.intersectionRatio(whiskers[j],lineDownLeft));
												
						if (dist > distance)
							dist = distance;
					}

						
					intersectionAsteroids[j] = dist;
				}
				
				
			}
						
		}
		
		updateWhiskers(false);
				
	}

	private void checkIfInDanger() {
		
		float inDangerRangeEscaping = 0.4f*WHISKER_LENGTH;
		float inDangerRangeEvading = 0.5f*WHISKER_LENGTH;
		float thresholdFactor = 0.5f;
		boolean hasEscaped = true;
		boolean hasEvaded = true;
		int dangerLevelEscaping = 0;

		for (int i = 0; i < gameObjectHandler.size(); i++) {
			if (!(gameObjectHandler.get(i) instanceof Asteroid))
				continue;
			Asteroid ast = (Asteroid) gameObjectHandler.get(i);

			Vector shipAstVector = new Vector(ast.getCenter().x-getCenter().x,ast.getCenter().y-getCenter().y);
			double lengthOfShipAstVector = shipAstVector.getLength();
				
			if (lengthOfShipAstVector > inDangerRangeEvading || !hasVelocity())
				continue;
				
			Vector shipVelocityVector = new Vector(velX,velY);
			
			double dotProduct = Vector.dotProduct(shipVelocityVector, shipAstVector) / (lengthOfShipAstVector*shipVelocityVector.getLength());
				
			double threshold = 0;
			switch (ast.getSize()) {
			case Asteroid.SMALL_Asteroid:
				threshold = 0.03d;
				break;
			case Asteroid.MEDIUM_Asteroid:
				threshold = 0.05d;
				break;
			case Asteroid.BIG_Asteroid:
				threshold = 0.07d;
				break;
			}
			
			threshold += thresholdFactor*((inDangerRangeEvading-lengthOfShipAstVector)/inDangerRangeEvading);
			
			if (Math.abs(dotProduct-1) < threshold) {
				inDangerShouldEvade = true;
				hasEvaded = false;
				break;
			}
			
		}
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			if (!(gameObjectHandler.get(i) instanceof Asteroid))
				continue;
			Asteroid ast = (Asteroid) gameObjectHandler.get(i);
			
			Vector shipAstVector = new Vector(ast.getCenter().x-getCenter().x,ast.getCenter().y-getCenter().y);
			double lengthOfShipAstVector = shipAstVector.getLength();
				
			if (lengthOfShipAstVector > inDangerRangeEscaping)
				continue;
				
			Vector astDir = new Vector(ast.getDir().x,ast.getDir().y);
			
			double dotProduct = Vector.dotProduct(astDir, shipAstVector) / (lengthOfShipAstVector * astDir.getLength());
				
			double threshold = 0;
			switch (ast.getSize()) {
			case Asteroid.SMALL_Asteroid:
				threshold = 0.03d;
				break;
			case Asteroid.MEDIUM_Asteroid:
				threshold = 0.05d;
				break;
			case Asteroid.BIG_Asteroid:
				threshold = 0.07d;
				break;
			}
			
			threshold += thresholdFactor*((inDangerRangeEscaping-lengthOfShipAstVector)/inDangerRangeEscaping);
			
			if (Math.abs(dotProduct+1) < threshold) {
				inDangerShouldEscape = true;
				dangerLevelEscaping++;
				hasEscaped = false;
			}
			
		}
		
		if (!hasEvaded)
			evadingTime++;
		if (hasEvaded && !inDangerShouldEvade)
			evadingTime = 0;
		if (hasEvaded && inDangerShouldEvade && evadingTime >= 50) {
			evadingTimes++;
			evadingTime = 0;
			inDangerShouldEvade = false;
		}
		
		if (!hasEscaped) {
			EscapingTime++;
			if (this.dangerLevelEscaping < dangerLevelEscaping)
				this.dangerLevelEscaping = dangerLevelEscaping;
			noDangerTimesCount = 0;
		}else noDangerTimesCount++;
		if (hasEscaped && !inDangerShouldEscape) {
			EscapingTime = 0;
			this.dangerLevelEscaping = 0;
		}
		if (hasEscaped && inDangerShouldEscape && EscapingTime >= 100) {
			EscapingTimes++;
			this.dangerLevelEscaping = 0;
			EscapingTime = 0;
			inDangerShouldEscape = false;
		}
		
		if (noDangerTimesCount == 500) {
			noDangerTimes++;
			noDangerTimesCount = 0;
		}
		
	}
	
	private void rewardForDistanceAndIsolation() {
		
		if (closest == null || closest[2] == null)
			return;
		
		double dist = (double)closest[2];
		double threshold = WHISKER_LENGTH*0.4d;
		
		if (dist >= threshold)
			rewardOfDistance += UNIT;
		else rewardOfDistance += Math.pow((dist/threshold),2)*UNIT;
		
	}
	
	private Vertex getClosest(Vertex vertex, Asteroid asteroid) {
		
		Vertex center = getCenter();
	    
	    Vertex vMin = vertex;
	    double min_dist = center.getDistance(vertex);

	    Vertex[] wrappedPositions = {
	        new Vertex(vertex.x, vertex.y - GameplayBox.height - asteroid.getHeight()),           // Up
	        new Vertex(vertex.x, vertex.y + GameplayBox.height + asteroid.getHeight()),           // Down
	        new Vertex(vertex.x + GameplayBox.width + asteroid.getWidth(), vertex.y),            // Right
	        new Vertex(vertex.x - GameplayBox.width - asteroid.getWidth(), vertex.y),            // Left
	        new Vertex(vertex.x + GameplayBox.width + asteroid.getWidth(), vertex.y - GameplayBox.height - asteroid.getHeight()),   // UpRight
	        new Vertex(vertex.x - GameplayBox.width - asteroid.getWidth(), vertex.y - GameplayBox.height - asteroid.getHeight()),   // UpLeft
	        new Vertex(vertex.x + GameplayBox.width + asteroid.getWidth(), vertex.y + GameplayBox.height + asteroid.getHeight()),   // DownRight
	        new Vertex(vertex.x - GameplayBox.width - asteroid.getWidth(), vertex.y + GameplayBox.height + asteroid.getHeight())    // DownLeft
	    };

	    for (Vertex wrapped : wrappedPositions) {
	        double dist = center.getDistance(wrapped);
	        if (dist < min_dist) {
	            vMin = wrapped;
	            min_dist = dist;
	        }
	    }

	    return vMin;
		
	}
	
}
