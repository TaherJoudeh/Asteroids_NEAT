package graph;

import java.util.HashMap;
import java.util.LinkedList;

public class Graph {

	public final static int MAX_NUM_OF_POINTS = 100;
	
	public static enum GraphType {
		NODE,
		RECTANGLE
	}
	
	private HashMap<Integer,Double> data;
	private LinkedList<ShapeGraph> points;
		
	private int size;
	private int index;
	
	private GraphType graphType;
	
	public Graph(GraphType graphType) {
		data = new HashMap<> ();
		points = new LinkedList<> ();
		this.graphType = graphType;
	}
	
	public GraphType getGraphType() { return graphType; }
	
	public LinkedList<ShapeGraph> getPoints() { return points; }
	public int size() { return size; }
	public int getRelativeSize(int startIndex, int endIndex) { return endIndex-startIndex; }
		
	public int getStartIndex() { return index*MAX_NUM_OF_POINTS; }
	public void setStartIndex(int index) {
		if (index*MAX_NUM_OF_POINTS >= size || index < 0)
			return;
		this.index = index;
	}
	public void incrementStartIndex() {
		setStartIndex(index+1);
	}
	public void decrementStartIndex() {
		setStartIndex(index-1);
	}
	
	public void setStartIndexToFirst() { index = 0; }
	public void setStartIndexToLast() { index = getLastStartIndex()/MAX_NUM_OF_POINTS; }
	
	public int getEndIndex() {
		int endIndex = (index+1)*MAX_NUM_OF_POINTS;
		if (endIndex >= size)
			return size;
		return endIndex;
	}
	
	public int getLastStartIndex() { return (MAX_NUM_OF_POINTS)*(int)((size-1)/(double)(MAX_NUM_OF_POINTS)); }
	
	public void addValue(int label, double value) {
		boolean isLast = getEndIndex() >= size;
		data.put(label, value);
		size++;
		if (graphType == GraphType.NODE)
			points.add(new NodeGraph(value,label));
		else if (graphType == GraphType.RECTANGLE)
			points.add(new RectangleGraph(value,label));
		
		if ((size-1)%(MAX_NUM_OF_POINTS) == 0 && size != 1 && isLast)
			index++;
	}
	
	public double getValue(int label) {
		if (data.isEmpty())
			return 0;
		return data.get(label);
	}
	
	public double remove(int label) {
		return data.remove(label);
	}
	
	public double getMax(int from, int to) {
		
		double max = Double.NEGATIVE_INFINITY;
		for (int i = from; i < to; i++)
			if (max < data.get(i+1))
				max = data.get(i+1);
		
		return max;
		
	}
	
	public void updateDisplayed() {
		
		int numOfPoints = 10;
		
		int size = getRelativeSize(getLastStartIndex(), this.size);
		
		if (size <= numOfPoints)
			return;
		
		double reducingFactor = (double)size/numOfPoints;
		int[] indexArray = new int[numOfPoints];
		
		for (int i = 0; i < numOfPoints; i++)
			indexArray[i] = (int)((this.size-1)-(i*reducingFactor));
		
		hidAllPointsExceptFor(indexArray);
		
	}
	
	private void hidAllPointsExceptFor(int[] indecies) {
		int count = indecies.length-1;
		for (int i = getLastStartIndex(); i < size; i++) {
			if (i == indecies[count])
				points.get(indecies[count--]).display();
			else points.get(i).hid();
		}
		
	}
	
}
