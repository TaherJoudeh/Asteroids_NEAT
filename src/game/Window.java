package game;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

public class Window implements WindowListener {

	public static int width = Toolkit.getDefaultToolkit().getScreenSize().width,
			height = Toolkit.getDefaultToolkit().getScreenSize().height;
	public static Dimension dimension = new Dimension(width,height);
	
	public static JFrame jframe;
	public static GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
	public static GraphicsDevice device = environment.getDefaultScreenDevice();
	
	public static SCREEN_STATE currentScreenState = SCREEN_STATE.FULLSCREEN;
	
	private static CanvasFrame canvasFrame;
	
	private static String name;
	
	public Window(String name, CanvasFrame canvasFrame, boolean fullScreen) {
		
		Window.name = name;
		
		jframe = new JFrame(name);
		
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		if (currentScreenState == SCREEN_STATE.FULLSCREEN) {
			
			width = Toolkit.getDefaultToolkit().getScreenSize().width;
			height = Toolkit.getDefaultToolkit().getScreenSize().height;
						
		}else {
			width = 1280;
			height = 720;
		}
		
		dimension.setSize(width, height);
		
		canvasFrame.setMinimumSize(dimension);
		canvasFrame.setPreferredSize(dimension);
		canvasFrame.setMaximumSize(dimension);
		
		jframe.setMinimumSize(dimension);
		jframe.setPreferredSize(dimension);
		jframe.setMaximumSize(dimension);
		
		jframe.setResizable(false);
		jframe.setLocationRelativeTo(null);

		jframe.add(canvasFrame);
		
		jframe.addWindowListener(this);
		
		if (currentScreenState == SCREEN_STATE.FULLSCREEN) {
			
			jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
			jframe.setUndecorated(true);
		
			if (device.isFullScreenSupported())
				device.setFullScreenWindow(jframe);
			
		}
		
		jframe.pack();
		jframe.requestFocus();
		canvasFrame.requestFocus();
		jframe.setVisible(true);
		canvasFrame.start();
		
	}
	
	public static void changeStateTo(SCREEN_STATE screenState) {
		
		if (currentScreenState == screenState)
			return;
		
//		canvasFrame.pause();
		JFrame newFrame = new JFrame(name);
		newFrame.add(canvasFrame);
		jframe.dispose();
		
		initFrame(newFrame,canvasFrame,true);
		
		if (screenState == SCREEN_STATE.FULLSCREEN) {
			
			if (device.isFullScreenSupported())
				device.setFullScreenWindow(newFrame);
			
			currentScreenState = screenState;
			
		}else if (screenState == SCREEN_STATE.BORDERLESS) {
			
			newFrame.setUndecorated(true);
			newFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			
			currentScreenState = screenState;
			
		}else if (screenState == SCREEN_STATE.WINDOWED) {
		
			newFrame.setUndecorated(false);
			newFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			
			currentScreenState = screenState;
			
		}
		
//		jframe.add(canvasFrame);
		jframe = newFrame;

		jframe.pack();
		jframe.setVisible(true);
		canvasFrame.start();
//		canvasFrame.resume();
		
	}

	private static void initFrame(JFrame jframe, CanvasFrame canvasFrame, boolean clone) {
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setResizable(false);
		
		jframe.setMinimumSize(dimension);
		jframe.setPreferredSize(dimension);
		jframe.setMaximumSize(dimension);
		
		jframe.setLocationRelativeTo(null);
		
		if (!clone)
			jframe.add(canvasFrame);
				
	}
	
	public static enum SCREEN_STATE {
		FULLSCREEN,BORDERLESS,WINDOWED;
	}
	
	@Override
	public void windowActivated(WindowEvent e) {
		CanvasFrame.recievingInputs = true;
		CanvasFrame.rendering = true;
	}

	@Override
	public void windowClosed(WindowEvent e) { /*NOTHING*/ }

	@Override
	public void windowClosing(WindowEvent e) { /*NOTHING*/ }

	@Override
	public void windowDeactivated(WindowEvent e) {
		CanvasFrame.recievingInputs = false;
		CanvasFrame.rendering = false;
	}

	@Override
	public void windowDeiconified(WindowEvent e) { /*NOTHING*/ }

	@Override
	public void windowIconified(WindowEvent e) { /*NOTHING*/ }

	@Override
	public void windowOpened(WindowEvent e) { /*NOTHING*/ }
	
}
