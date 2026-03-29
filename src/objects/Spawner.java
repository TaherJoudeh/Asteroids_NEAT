package objects;

import java.util.Random;

import game.Driver;
import game.GameplayBox;

public class Spawner {
	
	private GameObjectHandler gameObjectHandler;
	private Random generateRandom;
	
	public final static int MAX_NUM_OF_ASTEROIDS = Driver.AI ? 9 : 20;

	private int count = 1;
	private final static double DEFAULT_SPAWN_TIMER = 200;
	
	private double spawnTimer = DEFAULT_SPAWN_TIMER;
	
	public Spawner(GameObjectHandler gameObjectHandler) {
		this.gameObjectHandler = gameObjectHandler;
		generateRandom = gameObjectHandler.getRandom();
	}
	
	public void setSeed(long seed) {
		generateRandom.setSeed(seed);
	}
	
	public boolean spawnLittleAsteroids(Asteroid asteroid) {
		
		if (asteroid.getSize() == Asteroid.SMALL_Asteroid)
			return false;
			
			Vector dir = asteroid.getDir();
			
			Vector v1 = Vector.rotate(dir, Math.PI/5),
					v2 = Vector.rotate(dir, Math.PI/-5);
			
			int size = 0;
			
			if (asteroid.getSize() == Asteroid.BIG_Asteroid)
				size = Asteroid.MEDIUM_Asteroid;
			else size = Asteroid.SMALL_Asteroid;
			
			spawnLittleAsteroid(v1, size, asteroid.getCenter(), dir.getLength());
			spawnLittleAsteroid(v2, size, asteroid.getCenter(), dir.getLength());
			
		return true;
		
	}
	
	public void spawnAsteroids() {

		if (count%spawnTimer == 0) {
			if (gameObjectHandler.getAsteroidNum() < MAX_NUM_OF_ASTEROIDS) {
				spawnRandomAsteroid();
			}
			count = 1;
		}
		
		count++;
			
	}
	
	private void spawnLittleAsteroid(Vector dir, int size, Vertex origin, float speed) {
		new Asteroid(0,0, gameObjectHandler, size, origin, dir, speed);
	}
	
	private void spawnRandomAsteroid() {
		new Asteroid(0,0,gameObjectHandler, GameplayBox.generateRandomBorderVertex(generateRandom));		
	}
	
	public void update() {
		
		if (!gameObjectHandler.isActive())
			return;
		
		for (int i = 0; i < gameObjectHandler.size(); i++) {
			if (gameObjectHandler.get(i) instanceof Asteroid) {
				Asteroid asteroid = (Asteroid)gameObjectHandler.get(i);
				
				if (!asteroid.isAlive()) {
					spawnLittleAsteroids(asteroid);
					asteroid.elliminate();
					break;
				}
				
			}
		}
		
		spawnAsteroids();
		
	}
	
	public void reset() {
		spawnTimer = DEFAULT_SPAWN_TIMER;
		count = 1;
	}
		
}
