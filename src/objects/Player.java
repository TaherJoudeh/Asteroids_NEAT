package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import game.CanvasFrame;
import game.Driver;
import game.GameplayBox;
import neat.Agent;

public class Player extends GameObject {
	
	public static boolean immortal = false;
	
	public final static double MAX_VEL = 5;
	public final static double SURVIVAL_SECOND = 0.005;
	
	private Random random;
	
	private Vector direction, velocityAcceleration;
	
	private Agent agent;
	
	private float width, height,
		velX, velY;
	
	private float size;
	
	private final float angle = Driver.AI ? (float)Math.PI/70 : (float)Math.PI/100;
			
	private float accelerationX = 0,
			accelerationY = 0;
	
	private int bulletsShot, asteroidsShot, missedShots;
	
	private GameObjectHandler gameObjectHandler;
	
	private int score, shootedAsteroids;
	private double acceleratedTime, aliveTime, rotateTime, survivedSeconds, totalTimeSurvived;
	private double rewardOfDistance = 0;
	
	private int count = 1;
	private boolean isAlive;
	private double counter = 1;
	
	public static final double LIFE_SPAN = 20, MAX_LIFE_SPAN = LIFE_SPAN;
	private double lifeSpan = LIFE_SPAN, highestLifeSpan = 0;
	private double minLifeSpan = lifeSpan;
	
	private Rectangle2D.Float hitBox;
	
	private boolean hasRotated = false;
	
	public final static float WHISKER_LENGTH = 500;
	private Line2D.Float[] whiskers;
	private float[] intersectionAsteroids = new float[8];
		
	private boolean inDangerShouldEscape = false, inDangerShouldEvade = false;
	private int dangerLevelEscaping;
	private int EscapingTimes = 0, EscapingTime;
	private int evadingTimes, evadingTime;
	
	private double noDangerTimes;
	private double noDangerTimesCount;
	
	private double isolatedScore = 0;
	private boolean isIsolated;
	
	private boolean lastBulletHit;
	
	private final static int MAX_SHOT_COMBO_COUNTER = 2;
	private int shootComboCounter = 0;
		
	private int shootingRate = Driver.AI ? 500 : 300;
	private double counterForShooting = 0;
	private double lastingReward;
	
	private int threatBoxWidth = 300, threatBoxHeight = 300;
	
	private Rectangle2D.Double topThreatBox = new Rectangle2D.Double(0,GameplayBox.y-threatBoxHeight/2,threatBoxWidth,threatBoxHeight),
				downThreatBox = new Rectangle2D.Double(0,GameplayBox.endY-threatBoxHeight+threatBoxHeight/2,threatBoxWidth,threatBoxHeight),
				rightThreatBox = new Rectangle2D.Double(GameplayBox.endX-threatBoxHeight+threatBoxHeight/2,0,threatBoxHeight,threatBoxWidth),
				leftThreatBox = new Rectangle2D.Double(GameplayBox.x-threatBoxHeight/2,0,threatBoxHeight,threatBoxWidth);
	
	private double[] penaltyThreshold = {20,10,10,10};
	private double[] penaltyCounter = new double[4];
	private double totalPenalty = 1;
	
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

		initWhiskers();
		
		hitBox = new Rectangle2D.Float(getCenter().x-200, getCenter().y-200,400,400);
	}
	
	public Agent getAgent() { return agent; }
	public void setAgent(Agent agent) { this.agent = agent; }
	
	public double getIsolatedScore() { return isolatedScore; }
	
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
	
	public boolean hasRotated() { return hasRotated; }
	public double getRotateTime() { return rotateTime; }
	
	public double getLifeSpan() { return lifeSpan; }
	public double getMinLifeSpan() { return minLifeSpan; }
	
	public double getDistanceReward() { return rewardOfDistance; }
	
	public double getEscapingTimes() { return EscapingTimes; }
	public double getEvadingTimes() { return evadingTimes; }
	public double getLastingReward() { return lastingReward; }
	
	public double getNoDangerTimes() { return noDangerTimes; }
	
	private void initWhiskers() {
		
		Vertex center = getVertecies().get(2);
		double length = WHISKER_LENGTH;
		
		for (int i = 0; i < intersectionAsteroids.length; i++)
			intersectionAsteroids[i] = (float)length;
		
		// Front
		Line2D.Float whisker1 = new Line2D.Float(center.x,center.y,center.x+(direction.x*intersectionAsteroids[0]),center.y+(direction.y*intersectionAsteroids[0]));
		// Back
		Line2D.Float whisker2 = new Line2D.Float(center.x,center.y,center.x+(-direction.x*intersectionAsteroids[1]),center.y+(-direction.y*intersectionAsteroids[1]));
		// Right
		Vector right = new Vector(whisker1);
		right = Vector.normalize(Vector.rotate(right, -Math.PI/2));
		Line2D.Float whisker3 = new Line2D.Float(center.x,center.y,center.x+right.x*intersectionAsteroids[2],center.y+right.y*intersectionAsteroids[2]);
		// Left
		Vector left = new Vector(whisker1);
		left = Vector.normalize(Vector.rotate(left, Math.PI/2));
		Line2D.Float whisker4 = new Line2D.Float(center.x,center.y,center.x+left.x*intersectionAsteroids[3],center.y+left.y*intersectionAsteroids[3]);
		// Right Top
		Vector rightTop = new Vector(whisker1);
		rightTop = Vector.normalize(Vector.rotate(rightTop, -Math.PI/4));
		Line2D.Float whisker5 = new Line2D.Float(center.x,center.y,center.x+rightTop.x*intersectionAsteroids[4],center.y+rightTop.y*intersectionAsteroids[4]);
		// Right Bottom
		Vector rightBottom = new Vector(whisker2);
		rightBottom = Vector.normalize(Vector.rotate(rightBottom, Math.PI/4));
		Line2D.Float whisker6 = new Line2D.Float(center.x,center.y,center.x+rightBottom.x*intersectionAsteroids[5],center.y+rightBottom.y*intersectionAsteroids[5]);
		// Left Top
		Vector leftTop = new Vector(whisker1);
		leftTop = Vector.normalize(Vector.rotate(leftTop, Math.PI/4));
		Line2D.Float whisker7 = new Line2D.Float(center.x,center.y,center.x+leftTop.x*intersectionAsteroids[6],center.y+leftTop.y*intersectionAsteroids[6]);
		// Left Bottom
		Vector leftBottom = new Vector(whisker2);
		leftBottom = Vector.normalize(Vector.rotate(leftBottom, -Math.PI/4));
		Line2D.Float whisker8 = new Line2D.Float(center.x,center.y,center.x+leftBottom.x*intersectionAsteroids[7],center.y+leftBottom.y*intersectionAsteroids[7]);
		
		whiskers = new Line2D.Float [] {whisker1,whisker2,whisker3,whisker4,whisker5,whisker6,whisker7,whisker8};
		
	}
	
	private void updateWhiskersLength() {
		
		Vertex center = getVertecies().get(2);
		
		// Front
		Line2D.Float whisker1 = new Line2D.Float(center.x,center.y,center.x+(direction.x*intersectionAsteroids[0]),center.y+(direction.y*intersectionAsteroids[0]));
		// Back
		Line2D.Float whisker2 = new Line2D.Float(center.x,center.y,center.x+(-direction.x*intersectionAsteroids[1]),center.y+(-direction.y*intersectionAsteroids[1]));
		// Right
		Vector right = new Vector(whisker1);
		right = Vector.normalize(Vector.rotate(right, -Math.PI/2));
		Line2D.Float whisker3 = new Line2D.Float(center.x,center.y,center.x+right.x*intersectionAsteroids[2],center.y+right.y*intersectionAsteroids[2]);
		// Left
		Vector left = new Vector(whisker1);
		left = Vector.normalize(Vector.rotate(left, Math.PI/2));
		Line2D.Float whisker4 = new Line2D.Float(center.x,center.y,center.x+left.x*intersectionAsteroids[3],center.y+left.y*intersectionAsteroids[3]);
		// Right Top
		Vector rightTop = new Vector(whisker1);
		rightTop = Vector.normalize(Vector.rotate(rightTop, -Math.PI/4));
		Line2D.Float whisker5 = new Line2D.Float(center.x,center.y,center.x+rightTop.x*intersectionAsteroids[4],center.y+rightTop.y*intersectionAsteroids[4]);
		// Right Bottom
		Vector rightBottom = new Vector(whisker2);
		rightBottom = Vector.normalize(Vector.rotate(rightBottom, Math.PI/4));
		Line2D.Float whisker6 = new Line2D.Float(center.x,center.y,center.x+rightBottom.x*intersectionAsteroids[5],center.y+rightBottom.y*intersectionAsteroids[5]);
		// Left Top
		Vector leftTop = new Vector(whisker1);
		leftTop = Vector.normalize(Vector.rotate(leftTop, Math.PI/4));
		Line2D.Float whisker7 = new Line2D.Float(center.x,center.y,center.x+leftTop.x*intersectionAsteroids[6],center.y+leftTop.y*intersectionAsteroids[6]);
		// Left Bottom
		Vector leftBottom = new Vector(whisker2);
		leftBottom = Vector.normalize(Vector.rotate(leftBottom, -Math.PI/4));
		Line2D.Float whisker8 = new Line2D.Float(center.x,center.y,center.x+leftBottom.x*intersectionAsteroids[7],center.y+leftBottom.y*intersectionAsteroids[7]);
		
		whiskers = new Line2D.Float [] {whisker1,whisker2,whisker3,whisker4,whisker5,whisker6,whisker7,whisker8};
		
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
			
			topThreatBox.x = getCenter().x-(threatBoxWidth/2);
			downThreatBox.x = getCenter().x-(threatBoxWidth/2);
			rightThreatBox.y = getCenter().y-(threatBoxWidth/2);
			leftThreatBox.y = getCenter().y-((threatBoxWidth/2));
			
			counter += 0.01;
			survivedSeconds += SURVIVAL_SECOND;
			
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
			
			if (count%6000 == 0) {
				lastingReward += 30;
				count = 1;
			}
			
			if (lifeSpan < minLifeSpan)
				minLifeSpan = lifeSpan;

			if (lifeSpan-LIFE_SPAN > highestLifeSpan)
				highestLifeSpan = lifeSpan-LIFE_SPAN;
			}
			
//			if (count%50 == 0) {
//				aliveTime += 0.05;
//				count = 1;
//			}
			
			if (count%7 == 0 && counterForShooting < shootingRate)
				counterForShooting += 50;
			
			count++;
			
			checkIfInDanger();
			getWhiskerCollisionOptimized();
			rewardForDistanceAndIsolation();
			updateWhiskersLength();

			if (Driver.AI) {
				boolean[] dec = agent.decide(getUpdatedInputs());
				if (dec[0]) {
					accelerate();
					penaltyCounter[0] += SURVIVAL_SECOND;
				}else {
					decelerate();
					penaltyCounter[0] = 0;
				}
				if (dec[1]) {
					shoot();
					penaltyCounter[1] += SURVIVAL_SECOND;
				} else penaltyCounter[1] = 0;
				if (dec[2]) {
					rotate(true, true);
					penaltyCounter[2] += SURVIVAL_SECOND;
				} else penaltyCounter[2] = 0;
				if (dec[3]) {
					rotate(false, true);
					penaltyCounter[3] += SURVIVAL_SECOND;
				} else penaltyCounter[3] = 0;
				
				for (int i = 0; i < 4; i++) {
					if (penaltyCounter[i] >= penaltyThreshold[i]) {
						penaltyCounter[i] = 0;
						totalPenalty++;
					}
				}
				
				/*
				double survivalScore = 100*survivedSeconds;
				double asteroidScore = 1000*asteroidsShot;
				double accuracyBonus = getAccuracy() > 0.7 ? 2000*getAccuracy()*asteroidsShot : 0;
				double movementScore = 100*(EscapingTimes+evadingTimes);
				double distanceScore = 50*acceleratedTime;
				double progressiveBonus = Math.min(survivedSeconds, 60) * asteroidsShot * 10;
				double fitness = survivalScore
						+ asteroidScore
						+ accuracyBonus
						+ movementScore
						+ distanceScore
						+ progressiveBonus;
				*/
				
				double fitness = 2*survivedSeconds + 5*EscapingTimes + 2*evadingTimes + (10*getAccuracy()*asteroidsShot);
				fitness /= totalPenalty;
				
				agent.setFitness(fitness);
			}
			
		}
		
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
		
		if (CanvasFrame.showHitBox) {
//			g2d.draw(hitBox);
			
			g2d.setColor(new Color(255,0,0,50));
//			g2d.fill(topThreatBox);
//			g2d.fill(downThreatBox);
//			g2d.fill(rightThreatBox);
//			g2d.fill(leftThreatBox);
			
			if (Driver.AI) {
				/*
				Vertex v = (Vertex)getClosest()[1];
				Vertex v2 = (Vertex)getSecondClosest()[1];
				Vertex v3 = (Vertex)getThirdClosest()[1];
				Vertex v4 = (Vertex)getFourthClosest()[1];
				if (v != null)
					g2d.drawLine((int)hitBox.getCenterX(), (int)hitBox.getCenterY(), (int)v.x, (int)v.y);
				aproximityColor = new Color(1f,0f,0f,0.75f);
				g2d.setColor(aproximityColor);
				if (v2 != null)
					g2d.drawLine((int)hitBox.getCenterX(), (int)hitBox.getCenterY(), (int)v2.x, (int)v2.y);
				aproximityColor = new Color(1f,0f,0f,0.50f);
				g2d.setColor(aproximityColor);
				if (v3 != null)
					g2d.drawLine((int)hitBox.getCenterX(), (int)hitBox.getCenterY(), (int)v3.x, (int)v3.y);
				aproximityColor = new Color(1f,0f,0f,0.25f);
				g2d.setColor(aproximityColor);
				if (v4 != null)
					g2d.drawLine((int)hitBox.getCenterX(), (int)hitBox.getCenterY(), (int)v4.x, (int)v4.y);
				*/
				
				g2d.setColor(new Color(1f,0f,0f,0.35f));
				for (int i = 0; i < whiskers.length; i++)
					g2d.draw(whiskers[i]);
					
			}
		}
			
		
	}

	public void shoot() {
		if (counterForShooting >= shootingRate) {
			bulletsShot++;
			new Bullet(2,2,0, 0, gameObjectHandler);
			counterForShooting = 0;
		}
	}
	
	public void kill() {
		isAlive = false;
		gameObjectHandler.setIsActive(false);
		gameObjectHandler.onlyPlayer();
	}
	
	public boolean isAccelerated() {
		if (velX == 0 && velY == 0)
			return false;
		return true;
	}
	
	public double[] getUpdatedInputs() {
		
		double[] inp = new double[11];
		
		int index = 0;

		inp[index++] = (double)velX/MAX_VEL;
		inp[index++] = (double)velY/MAX_VEL;
		
		/*
		Vertex v = (Vertex)getClosest()[1];
		Vector closestVector = null;
		
		if (v != null)
			closestVector = new Vector(v.x-getCenter().x, v.y-getCenter().y);
			
		if (v == null)
			inp[index++] = 0;
		else inp[index++] = closestVector.getAngle()/Math.PI;
		inp[index++] = direction.getAngle()/Math.PI;
//		inp[index++] = direction.getAngle();
		 */
		
		for (int i = 0; i < intersectionAsteroids.length; i++)
//			inp[index++] = (WHISKER_LENGTH-intersectionAsteroids[i])/(double)WHISKER_LENGTH;
			inp[index++] = ((WHISKER_LENGTH-intersectionAsteroids[i])-(WHISKER_LENGTH*0.5))/WHISKER_LENGTH;
		
		inp[index++] = (counterForShooting-(shootingRate*0.5))/shootingRate;
		
		return inp;
		
	}
	
	public void accelerate() {
		accelerationX = Driver.AI ? direction.x/35f : direction.x/50f;
		accelerationY = Driver.AI ? direction.y/35f : direction.y/50f;
	
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
				
		velX -= velocityAcceleration.x/150d;
		velY -= velocityAcceleration.y/150d;
				
		if (Vector.getLength(velX, velY) < 1e-2) {
			velX = 0;
			velY = 0;
			velocityAcceleration.x = 0;
			velocityAcceleration.y = 0;
		}
		
	}
	
	public void rotate(boolean isClockwise, boolean accelerated) {
		
		hasRotated = true;
		rotateTime += 0.01;
		
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
		
//		front.color = Color.GREEN;
		
		Vertex shipCenter = new Vertex(frontVector.x+(direction.getCounterVector().x*size/2f),frontVector.y+(direction.getCounterVector().y*size/2f));
//		shipCenter.color = Color.YELLOW;
		
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
		
		hasRotated = false;
		rotateTime = 0;
		lifeSpan = LIFE_SPAN;
		highestLifeSpan = 0;
		minLifeSpan = lifeSpan;
		survivedSeconds = 0;
		totalTimeSurvived = 0;
		rewardOfDistance = 0;
				
		lastBulletHit = false;
		
		isolatedScore = 0;
		isIsolated = false;
		
		shootComboCounter = 0;
		
		EscapingTimes = 0;
		EscapingTime = 0;
		inDangerShouldEscape = false;
		
		evadingTimes = 0;
		evadingTime = 0;
		dangerLevelEscaping = 0;
		inDangerShouldEvade = false;
		
		lastingReward = 0;
		
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
		
		penaltyCounter = new double[4];
		totalPenalty = 1;
		
		createVertices();
		
		isAlive = true;
		
	}
	
	public void collisionDetection() {
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			GameObject go = gameObjectHandler.get(i);
			if (go instanceof Asteroid && collisionVectors(go)) {
				isAlive = false;
				CanvasFrame.numberOfDeadAgents++;
				go.elliminate();
				break;
			}
		}
		
		if (!isAlive) {
			gameObjectHandler.setIsActive(isAlive);
			gameObjectHandler.onlyPlayer();
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
	
	public Object[] getClosest() {
	
		Vertex vMin = null;
		Asteroid ast = null;
		double min_dist = Double.MAX_VALUE;
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			if ( !(gameObjectHandler.get(i) instanceof Asteroid) ) continue;
			
			Asteroid asteroid = (Asteroid) gameObjectHandler.get(i);
			
			for (Vertex vertex: asteroid.getVertecies()) {
				
				double dist = Point.distance(getCenter().x, getCenter().y, vertex.x, vertex.y);
				
				if (dist < min_dist) {
					vMin = vertex;
					min_dist = dist;
					ast = asteroid;
				}
				
			}
			
		}
		
		if (vMin == null)
			return new Object [] {null,null};
		
		return new Object [] {ast,vMin};
		
	}
	
	public Object[] getSecondClosest() {
		
		Vertex closest = (Vertex)getClosest()[1];
		Vertex vMin = null;
		Asteroid ast = null;
		
		if (closest == null)
			return new Object [] {null,null};;
			
		double closest_dist = Point.distance(hitBox.getCenterX(), hitBox.getCenterY(), closest.x, closest.y);
		double min_dist = Double.MAX_VALUE;
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			if ( !(gameObjectHandler.get(i) instanceof Asteroid)) continue;
			
			Asteroid asteroid = (Asteroid) gameObjectHandler.get(i);
			
			if (asteroid.getVertecies().contains(closest)) continue;
			
			for (Vertex vertex: asteroid.getVertecies()) {
				
				double dist = Point.distance(hitBox.getCenterX(), hitBox.getCenterY(), vertex.x, vertex.y);
				
				if (dist < min_dist && dist > closest_dist) {
					vMin = vertex;
					min_dist = dist;
					ast = asteroid;
				}
				
			}
			
		}
		
		if (vMin == null)
			return new Object [] {null,null};
		
		return new Object [] {ast,vMin};
		
	}
	
	public Object[] getThirdClosest() {
	
		Vertex closest = (Vertex)getClosest()[1];
		Vertex secondClosest = (Vertex)getSecondClosest()[1];
		Vertex vMin = null;
		Asteroid ast = null;
		
		if (secondClosest == null)
			return new Object [] {null,null};;
		
		double closest_dist = Point.distance(hitBox.getCenterX(), hitBox.getCenterY(), secondClosest.x, secondClosest.y);
		double min_dist = Double.MAX_VALUE;
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			if ( !(gameObjectHandler.get(i) instanceof Asteroid)) continue;
			
			Asteroid asteroid = (Asteroid) gameObjectHandler.get(i);
			
			if (asteroid.getVertecies().contains(secondClosest) || asteroid.getVertecies().contains(closest)) continue;
			
			for (Vertex vertex: asteroid.getVertecies()) {
				
				double dist = Point.distance(hitBox.getCenterX(), hitBox.getCenterY(), vertex.x, vertex.y);
				
				if (dist < min_dist && dist > closest_dist) {
					vMin = vertex;
					min_dist = dist;
					ast = asteroid;
				}
				
			}
			
		}
		
		if (vMin == null)
			return new Object [] {null,null};
		
		return new Object [] {ast,vMin};
		
	}
	
	public Object[] getFourthClosest() {
		
		Vertex closest = (Vertex)getClosest()[1];
		Vertex secondClosest = (Vertex)getSecondClosest()[1];
		Vertex thirdClosest = (Vertex)getThirdClosest()[1];
		Vertex vMin = null;
		Asteroid ast = null;
		
		if (thirdClosest == null)
			return new Object [] {null,null};;
		
		double closest_dist = Point.distance(hitBox.getCenterX(), hitBox.getCenterY(), thirdClosest.x, thirdClosest.y);
		double min_dist = Double.MAX_VALUE;
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			if ( !(gameObjectHandler.get(i) instanceof Asteroid)) continue;
			
			Asteroid asteroid = (Asteroid) gameObjectHandler.get(i);
			
			if (asteroid.getVertecies().contains(thirdClosest) || asteroid.getVertecies().contains(secondClosest) ||
					asteroid.getVertecies().contains(closest)) continue;
			
			for (Vertex vertex: asteroid.getVertecies()) {
				
				double dist = Point.distance(hitBox.getCenterX(), hitBox.getCenterY(), vertex.x, vertex.y);
				
				if (dist < min_dist && dist > closest_dist) {
					vMin = vertex;
					min_dist = dist;
					ast = asteroid;
				}
				
			}
			
		}
		
		if (vMin == null)
			return new Object [] {null,null};
		
		return new Object [] {ast,vMin};
		
	}
	
	public int getEdgeThreat() {
		
		Point p = getCenter().getPoint();
		Rectangle2D.Double criticalRect = null;
		int count = 0;
		
		if (topThreatBox.contains(p))
			criticalRect = downThreatBox;
		else if (downThreatBox.contains(p))
			criticalRect = topThreatBox;
		else if (rightThreatBox.contains(p))
			criticalRect = leftThreatBox;
		else if (leftThreatBox.contains(p))
			criticalRect = rightThreatBox;
		else return 0;
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			if ( !(gameObjectHandler.get(i) instanceof Asteroid)) continue;
			
			Asteroid asteroid = (Asteroid) gameObjectHandler.get(i);
			
			for (Vertex vertex: asteroid.getVertecies()) {
					if (criticalRect.contains(vertex.getPoint())) {
						count++;
						break;
					}
			}
			
		}
		
		if (count > 0)
			return 1;
		else return 0;
		
	}
	
	private void getWhiskerCollision() {
		
		initWhiskers();
		
		for (int j = 0; j < whiskers.length; j++) {
			
			float dist = WHISKER_LENGTH;

			for (int i = 0; i < gameObjectHandler.size(); i++) {
			
			if (!(gameObjectHandler.get(i) instanceof Asteroid))
				continue;
			
			Asteroid ast = (Asteroid) gameObjectHandler.get(i);

				for (int k = 0; k < ast.getVertecies().size(); k++) {
					
					Vertex v1 = ast.getVertecies().get(k),
							v2 = ast.getVertecies().get((k+1)%ast.getVertecies().size());
					
					Line2D.Float line = new Line2D.Float(v1.x,v1.y,v2.x,v2.y);
										
					if (whiskers[j].intersectsLine(line)) {
						
						float distance = (float)(WHISKER_LENGTH*Vector.intersectionRatio(whiskers[j],line));
												
						if (dist > distance)
							dist = distance;
						
					}
										
				}
				
				
			}
			
			intersectionAsteroids[j] = dist;
			
		}
		
	}

	private void getWhiskerCollisionOptimized() {
		
		initWhiskers();
		
		float width = GameplayBox.boxWidth,
				height = GameplayBox.boxHeight;
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			
			if (!(gameObjectHandler.get(i) instanceof Asteroid))
				continue;
			Asteroid ast = (Asteroid) gameObjectHandler.get(i);
			
			for (int j = 0; j < whiskers.length; j++) {
				
				for (int k = 0; k < ast.getVertecies().size(); k++) {
					
					Vertex v1 = ast.getVertecies().get(k),
							v2 = ast.getVertecies().get((k+1)%ast.getVertecies().size());
				
					Line2D.Float line = new Line2D.Float(v1.x,v1.y,v2.x,v2.y);
					Line2D.Float lineUp = new Line2D.Float(v1.x,v1.y-height,v2.x,v2.y-height);
					Line2D.Float lineDown = new Line2D.Float(v1.x,v1.y+height,v2.x,v2.y+height);
					Line2D.Float lineRight = new Line2D.Float(v1.x+width,v1.y,v2.x+width,v2.y);
					Line2D.Float lineLeft = new Line2D.Float(v1.x-width,v1.y,v2.x-width,v2.y);
				
					Line2D.Float lineUpRight = new Line2D.Float(v1.x+width,v1.y-height,v2.x+width,v2.y-height);
					Line2D.Float lineUpLeft = new Line2D.Float(v1.x-width,v1.y+height,v2.x-width,v2.y+height);
					Line2D.Float lineDownRight = new Line2D.Float(v1.x+width,v1.y+height,v2.x+width,v2.y+height);
					Line2D.Float lineDownLeft = new Line2D.Float(v1.x-width,v1.y+height,v2.x-width,v2.y+height);				
					
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
		
	}

	private void checkIfInDanger() {
		
		float inDangerRangeEscaping = WHISKER_LENGTH;
		float inDangerRangeEvading = WHISKER_LENGTH;
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
				
			if (lengthOfShipAstVector > inDangerRangeEvading || !isAccelerated())
				continue;
				
			Vector shipVelocityVector = new Vector(velX,velY);
			
			double dotProduct = Vector.dotProduct(shipVelocityVector, shipAstVector) / (lengthOfShipAstVector*shipVelocityVector.getLength());
				
			double threshold = 0;
			switch (ast.getSize()) {
			case Asteroid.SMALL_Asteroid:
				threshold = 0.02d;
				break;
			case Asteroid.MEDIUM_Asteroid:
				threshold = 0.04d;
				break;
			case Asteroid.BIG_Asteroid:
				threshold = 0.06d;
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
			
			double dotProduct = Vector.dotProduct(astDir, shipAstVector) / (lengthOfShipAstVector);
				
			double threshold = 0;
			switch (ast.getSize()) {
			case Asteroid.SMALL_Asteroid:
				threshold = 0.02d;
				break;
			case Asteroid.MEDIUM_Asteroid:
				threshold = 0.04d;
				break;
			case Asteroid.BIG_Asteroid:
				threshold = 0.06d;
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
				noDangerTimesCount++;
		}else noDangerTimesCount = 0;
		if (hasEscaped && !inDangerShouldEscape) {
			EscapingTime = 0;
			this.dangerLevelEscaping = 0;
		}
		if (hasEscaped && inDangerShouldEscape && EscapingTime >= 100) {
//			if (isAccelerated())
				EscapingTimes++;
//				EscapingTimes += this.dangerLevelEscaping;
			this.dangerLevelEscaping = 0;
			EscapingTime = 0;
			inDangerShouldEscape = false;
		}
		
		if (noDangerTimesCount == 100) {
			noDangerTimes++;
			noDangerTimesCount = 0;
		}
		
	}
	
	private void rewardForDistanceAndIsolation() {
		
		double maxThreshold = 0.3*WHISKER_LENGTH;
		
		double min = maxThreshold;
		double sum = 0;
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			if (!(gameObjectHandler.get(i) instanceof Asteroid))
				continue;
			Asteroid ast = (Asteroid) gameObjectHandler.get(i);
			Vector distanceVector = new Vector(ast.getCenter().x-getCenter().x,ast.getCenter().y-getCenter().y);
			double distance = distanceVector.getLength();
			
			if (distance > maxThreshold)
				continue;
			
			if (distance < min)
				min = distance;

			sum += (distance*distance)/(maxThreshold*maxThreshold);
		}
		
		sum *= 1e-4;
		
//		rewardOfDistance += (min/maxThreshold);
		
//		double threshold = 0.5*WHISKER_LENGTH;
//		if (min < threshold)
//			rewardOfDistance++;
		rewardOfDistance += SURVIVAL_SECOND*((maxThreshold-min)/maxThreshold);
		
//		if (min < rewardOfDistance && min >= 50)
//			rewardOfDistance = min;
		
		double upperThreshold = 0.2d;
		
		if (min > WHISKER_LENGTH*upperThreshold && Vector.getLength(velX, velY) < 0.5) {
			if (!isIsolated) {
				isolatedScore++;
				isIsolated = true;
			}
		}else isIsolated = false;
		
	}
	
}
