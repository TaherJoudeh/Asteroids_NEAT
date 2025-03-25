package game;

import javax.swing.SwingUtilities;

public class Driver {

	public final static boolean fullScreen = true,
			AI = true,
			VALIDATE = false;
	
	public static void main(String[] args) {

		System.setProperty("sun.java2d.opengl", "True");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Window("Asteroids",new CanvasFrame(),fullScreen);
			}
		});
		
	}
	
}
