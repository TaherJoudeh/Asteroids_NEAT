package input;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import game.CanvasFrame;
import graph.Graph;
import graph.GraphHandler;

public class MouseInput extends MouseMotionAdapter {

	
	private Point point;
	
	public Point getPoint() { return point; }
	
	@Override
	public void mouseMoved(MouseEvent e) {
		
		Point point = e.getPoint();
		this.point = point;
		super.mouseMoved(e);
	}
	
	public void renderNodeValue(Graphics2D g2d, GraphHandler graph, Point p) {
		
		if (CanvasFrame.showGraph) {
			
			Graph g = graph.getGraph();
			for (int i = g.getStartIndex(); i< g.getEndIndex(); i++) {
				if (g.getPoints().get(i).isPointWithin(p))
					g.getPoints().get(i).renderValue(g2d, p);
			}
			
			return;
		}
	}
	
}
