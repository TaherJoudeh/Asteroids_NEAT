package input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import game.CanvasFrame;
import game.Driver;
import game.World;
import graph.Graph.GraphType;
import objects.Bullet;
import objects.Player;

public class KeyInput extends KeyAdapter {

	public World world;
	
	private World[] worlds;
	
	public final static int IDLE = 0;
	public final static int ROTATE_RIGHT = 1, ROTATE_LEFT = 2;
		
	private Player player;
	
	private int rotationState = IDLE;
	private boolean accelerated, shooted;
	
	private CanvasFrame canvasFrame;
	
	private boolean shiftIsPressed;
	
	public KeyInput(World[] worlds, World world, CanvasFrame canvasFrame) {
		this.world = world;
		player = world.getHandler().getPlayer();
		
		this.worlds = worlds;
		
		this.canvasFrame = canvasFrame;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		if (!CanvasFrame.recievingInputs)
			return;
		
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_SHIFT)
			shiftIsPressed = true;
		
		if (key == KeyEvent.VK_ESCAPE) {
			
			CanvasFrame.paused = true;
			int input = JOptionPane.showConfirmDialog(canvasFrame,"Do you really want to exit?","Exit?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null);
			
			if (input == JOptionPane.NO_OPTION) {
				CanvasFrame.paused = false;
				return;
			}
			if (input == JOptionPane.YES_OPTION)
				canvasFrame.stop();
		}
		
		if (Driver.AI)
			return;
		
		if (key == KeyEvent.VK_UP)
			accelerated = true;
		if (key == KeyEvent.VK_RIGHT)
			rotationState = ROTATE_RIGHT;
		if (key == KeyEvent.VK_LEFT)
			rotationState = ROTATE_LEFT;
		if (key == KeyEvent.VK_SPACE && !shooted) {
			new Bullet(2,2,0, 0, world.getHandler());
			shooted = true;
		}
		
		super.keyPressed(e);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {

		if (!CanvasFrame.recievingInputs)
			return;
		int key = e.getKeyCode();
		
		if (key == KeyEvent.VK_SHIFT)
			shiftIsPressed = false;
		
		if (key == KeyEvent.VK_P)
			CanvasFrame.paused = !CanvasFrame.paused;
		
		if (key == KeyEvent.VK_H)
			CanvasFrame.showWhiskers = !CanvasFrame.showWhiskers;
		
		if (Driver.AI) {
		
			if (key == KeyEvent.VK_K) {
				worlds[CanvasFrame.currentWorldIndex].getPlayer().kill();
				CanvasFrame.numberOfDeadAgents++;
			}
			
			if (key == KeyEvent.VK_E)
				CanvasFrame.showDisabled = !CanvasFrame.showDisabled;
			
			if (key == KeyEvent.VK_G) {
				if (CanvasFrame.showGraph) {
					if (CanvasFrame.currentGraphShown == GraphType.NODE)
						CanvasFrame.currentGraphShown = GraphType.RECTANGLE;
					else CanvasFrame.showGraph = false;
				}else {
					CanvasFrame.currentGraphShown = GraphType.NODE;
					CanvasFrame.showGraph = true;
				}
			}
			
			if (CanvasFrame.showGraph) {
				if (key == KeyEvent.VK_CLOSE_BRACKET) {
					canvasFrame.getNodeGraph().setStartIndexToLast();
					canvasFrame.getRectangleGraph().setStartIndexToLast();
				}
				else if (key == KeyEvent.VK_OPEN_BRACKET) {
					canvasFrame.getNodeGraph().setStartIndexToFirst();
					canvasFrame.getRectangleGraph().setStartIndexToFirst();
				}
			}
			
			if (key == KeyEvent.VK_RIGHT) {
				if (shiftIsPressed) {
					if (CanvasFrame.showGraph) {
						canvasFrame.getNodeGraph().incrementStartIndex();
						canvasFrame.getRectangleGraph().incrementStartIndex();
					}
				} else getActiveWorld(true);
			} else if (key == KeyEvent.VK_LEFT) {
				if (shiftIsPressed) {
					if (CanvasFrame.showGraph) {
						canvasFrame.getNodeGraph().decrementStartIndex();
						canvasFrame.getRectangleGraph().decrementStartIndex();
					}
				}else getActiveWorld(false);
			}
			
			validWorldIndex();
			return;
		}
		
		if (key == KeyEvent.VK_UP)
			accelerated = false;
		if (key == KeyEvent.VK_RIGHT)
			rotationState = IDLE;
		if (key == KeyEvent.VK_LEFT)
			rotationState = IDLE;
		if (key == KeyEvent.VK_SPACE)
			shooted = false;
		
		super.keyReleased(e);
	}
	
	public void update() {
		
		if (accelerated)
			player.accelerate();
		else player.decelerate();
		
		if (rotationState == ROTATE_RIGHT)
			player.rotate(true);
		if (rotationState == ROTATE_LEFT)
			player.rotate(false);
		
	}
	
	private void validWorldIndex() {
		if (CanvasFrame.currentWorldIndex < CanvasFrame.startIndex)
			CanvasFrame.currentWorldIndex = CanvasFrame.startIndex;
		else if (CanvasFrame.currentWorldIndex >= CanvasFrame.endIndex)
			CanvasFrame.currentWorldIndex = CanvasFrame.endIndex-1;
	}
	
	private void getActiveWorld(boolean next) {
		
		if (next) {
			
			for (int i = CanvasFrame.currentWorldIndex+1; i < CanvasFrame.endIndex; i++) {
				if (worlds[i].getHandler().isActive()) {
					CanvasFrame.currentWorldIndex = i;
					return;
				}
			}
			
		}else {
			for (int i = CanvasFrame.currentWorldIndex-1; i >= CanvasFrame.startIndex; i--) {
				if (worlds[i].getHandler().isActive()) {
					CanvasFrame.currentWorldIndex = i;
					return;
				}
			}
		}
		
	}
	
}
