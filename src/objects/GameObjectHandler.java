package objects;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.Random;

public class GameObjectHandler {

	 private LinkedList<GameObject> gameObjects;
	 private int asteroidNum;
	 
	 private boolean isActive;
	 
	 private long seed;
	 private Random random = new Random();
	 
	 public GameObjectHandler() {
		 gameObjects = new LinkedList<> ();
		 isActive = true;
	 }
	 
	 public GameObjectHandler(long seed) {
		 gameObjects = new LinkedList<> ();
		 isActive = true;
		 
		 this.seed = seed;
		 random.setSeed(seed);
	 }
	 
	 public Random getRandom() { return random; }
	 public void setRandom(Random random) { this.random = random; }
	 
	 public void resetRandom() { random = new Random(seed); }
	 public void setSeed(long seed) { random.setSeed(seed); }
	 
	 public boolean isActive() { return isActive; }
	 public void setIsActive(boolean isActive) { this.isActive = isActive; }
	 
	 public int getAsteroidNum() { return asteroidNum; }
	 public void setAsteroidNum(int asteroidNum) { this.asteroidNum = asteroidNum; }
	 
	 public void incrementAstroidNum() { asteroidNum++; }
	 public void incrementAstroidNum(int num) { asteroidNum += num; }
	 public void decrementAstroidNum() { asteroidNum--; }
	 
	 public void adjustingAstroidNum(int x) { asteroidNum += x; }
	 
	 public void setPlayer(Player player) {
		 
		 gameObjects.remove(getPlayer());
		 gameObjects.add(player);
		 
	 }
	 
	 public void onlyPlayer() {
		 Player player = getPlayer();
		 gameObjects.clear();
		 asteroidNum = 0;
		 gameObjects.add(player);
	 }
	 
	 public Player getPlayer() {
		 for (GameObject gameObject: gameObjects)
			 if (gameObject instanceof Player)
				 return (Player)gameObject;
		 return null;
	 }
	 public GameObject get(int index) {
		 return gameObjects.get(index);
	 }
	 	 
	 public boolean addGameObject(GameObject gameObject) {
		 return gameObjects.add(gameObject);
	 }
	 
	 public void addGameObject(int index, GameObject gameObject) {
		 gameObjects.add(index, gameObject);
	 }
	 
	 public boolean removeGameObject(GameObject gameObject) {
		 
		 if (gameObjects.isEmpty())
			 return false;
		 
		 return gameObjects.remove(gameObject);
		 
	 }
	
	 public GameObject removeGameObject(int index) {
		 
		 if (gameObjects.isEmpty())
			 return null;
		 if (index < 0 || index >= gameObjects.size())
			 return null;
		 
		 return gameObjects.remove(index);
		 
	 }
	 
	 public void clear() {
		 gameObjects.clear();
	 }
	 
	 public int size() {
		 return gameObjects.size();
	 }
	 
	 public void update() {

		 if (gameObjects.isEmpty() || !isActive)
			 return;
//		 for (GameObject go: gameObjects) {
		 for (int i = 0; i < gameObjects.size(); i++) {
			 GameObject go = gameObjects.get(i);
			 
			 if (go != null)
				 go.update();
		 }
	 }
	 
	 public void render(Graphics g) {
		 if (gameObjects.isEmpty())
			 return;
		 for (int i = 0; i < gameObjects.size(); i++) {
			 GameObject go = gameObjects.get(i);
			 if (go != null)
				 go.render(g);
		 }
	 }
	 
}
