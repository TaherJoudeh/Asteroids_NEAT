package game;

import java.awt.Graphics;
import java.util.Random;

import objects.Asteroid;
import objects.GameObjectHandler;
import objects.Player;
import objects.Spawner;

public class World {

	private GameObjectHandler gameObjectHandler;
	private Spawner spawner;
	
	private static final int startAsteroids = 5;
	
	private long seed = (long)((Math.random()-1)*1000000d);
	private Random random;
			
	public World() {
		gameObjectHandler = new GameObjectHandler();
		
		random = new Random(seed);
		
		gameObjectHandler.setRandom(random);
		spawner = new Spawner(gameObjectHandler);
		
		spawnStart();
	}
	
	public World(long seed) {
		gameObjectHandler = new GameObjectHandler();
		
		this.seed = seed;
		random = new Random(seed);
		
		gameObjectHandler.setRandom(random);
		spawner = new Spawner(gameObjectHandler);
		spawnStart();
	}
	
	public void spawnStart() {
		new Player(5,5,0,0,gameObjectHandler);
		
		for (int i = 0; i < startAsteroids; i++)
			new Asteroid(0,0,gameObjectHandler);
	}
	
	public void startAgain() {
		for (int i = 0; i < startAsteroids; i++)
			new Asteroid(0,0,gameObjectHandler);
		spawner.reset();
	}
	
	public GameObjectHandler getHandler() { return gameObjectHandler; }

	public Player getPlayer() {
		return gameObjectHandler.getPlayer();
	}
	
	public void setSeed(long seed) {
		gameObjectHandler.setSeed(seed);
	}
	
	public void update() {
		gameObjectHandler.update();
		spawner.update();
	}
	public void render(Graphics g) {
		gameObjectHandler.render(g);
	}
	
}
