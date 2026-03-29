package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import graph.Graph;
import graph.Graph.GraphType;
import graph.GraphHandler;
import input.KeyInput;
import input.MouseInput;
import main.java.neat.config.ActivationConfig;
import main.java.neat.config.AggregationConfig;
import main.java.neat.config.ConfigFactory;
import main.java.neat.config.NEATConfig;
import main.java.neat.config.NEATConfig.CONNECTIVITY;
import main.java.neat.config.NEATConfig.SELECTION_TYPE;
import main.java.neat.config.NEATConfigBuilder;
import main.java.neat.core.Agent;
import main.java.neat.core.Neat;
import main.java.neat.functions.ActivationFunction.ACTIVATION_FUNCTION;
import main.java.neat.functions.AggregationFunction.AGGREGATION_FUNCTION;
import main.java.neat.io.GenomeFileHandler;
import main.java.neat.visualizer.GenomeVisualizer;
import main.java.neat.visualizer.GenomeVisualizerBuilder;

public class CanvasFrame extends Canvas implements Runnable {

	private static final long serialVersionUID = -7762560848690684678L;
	private Thread thisThread;
	private boolean isRunning;
		
	private int batchSize = Driver.VALIDATE ? 1 : 100;
	
	public static World[] worlds;
	public static NEATConfig neatConfig;
	private GenomeVisualizer genomeVisualizer;
	public static Neat neat;
	
	private KeyInput keyInput;
	private MouseInput mouseInput;
	
	public static int startIndex;
	public static int endIndex;
	
	public static int currentWorldIndex = 0;
	public static int batch = 1;
	public final static int EVALUATE_X_GENERATION = Driver.VALIDATE ? 1 : 2;
	public final static int SINGLE_SEED_PERIOD = 1;
	
	private int seed_counter = 0;
	private long seed;
	
	public static boolean showHitBox = false, showBoxes = false,
			paused = false, recievingInputs = true, rendering = true, showGraph = false,
			showDisabled = false;
	public static GraphType currentGraphShown = GraphType.NODE;
	
	private GraphHandler nodeGraph, rectangleGraph;
	
	public static int FRAME_RATE = 100;
	
	public static int numberOfDeadAgents = 0;
	private int gen_counter = 0;
	
	private Random random = new Random();
		
	public CanvasFrame() {
		
		setFocusTraversalKeysEnabled(false);
		requestFocusInWindow();
		
		init();
		
		addKeyListener(keyInput);
		addMouseMotionListener(mouseInput);
		
	}
	
	private void init() {
		thisThread = new Thread(this);
		
		if (Driver.AI)
			initAI();
		endIndex = batchSize;
		
		thisThread.setPriority(Thread.MAX_PRIORITY);
		
		if (Driver.AI) {
			if (Driver.VALIDATE) {
				worlds = new World[1];
			} else worlds = new World[neatConfig.getPopulationSize()];
		} else worlds = new World[1];

		seed = generateSeed();
		
		Agent[] agents = neat.getPopulation();
		for (int i = 0; i < worlds.length; i++)
			worlds[i] = new World(seed);
		
		currentWorldIndex = 0;
		
		if (Driver.AI) {
			for (int i = 0; i < worlds.length; i++)
				worlds[i].getPlayer().setAgent(agents[i]);
			if (Driver.VALIDATE)
				worlds[0].getPlayer().getAgent().setGenome(GenomeFileHandler.loadGenome("Genomes/genome.neat"));
			nodeGraph = new GraphHandler(GraphType.NODE);
			rectangleGraph = new GraphHandler(GraphType.RECTANGLE);
		}
		
		keyInput = new KeyInput(worlds,worlds[0],this);
		if (Driver.AI)
			mouseInput = new MouseInput();
	}
	
	private void initAI() {
		genomeVisualizer = new GenomeVisualizerBuilder().defaultGenomeVisuals().build();
		AggregationConfig agC = new AggregationConfig(AGGREGATION_FUNCTION.SUM);
		ActivationConfig acC = ConfigFactory.createDefaultActivationConfig();
		
		neatConfig = new NEATConfigBuilder(Driver.VALIDATE ? 1 : 400, 22, 4, agC, acC)
			    .setStartingActivationFunctionForHiddenNodes(ACTIVATION_FUNCTION.SIGMOID)
			    .setActivationFunctionForOutputNodes(ACTIVATION_FUNCTION.SIGMOID)
			    
			    .setFeedForward(false)
			    .setProbRecurrentConnection(0.4)
			    
			    .setWeightMaxValue(8)
			    .setWeightMinValue(-8)
			    .setWeightMutationPower(0.1)
			    .setWeightAdjustingRate(0.8)
			    .setWeightRandomizingRate(0.1)
			    .setWeightInitStdev(1)
			    
			    .setBiasMaxValue(8)
			    .setBiasMinValue(-8)
			    .setBiasMutationPower(0.1)
			    .setBiasAdjustingRate(0.8)
			    .setWeightRandomizingRate(0.1)
			    .setBiasInitStdev(1)
			    
			    .setCompatibilityThreshold(3.0)
			    .setDynamicCompatibilityThreshold(true)
			    .setCompatabilityThresholdAdjustingFactor(0.1)
			    .setTargetNumberOfSpecies(15)
			    .setCompatibilityExcessCoefficient(1.0)
			    .setCompatibilityDisjointCoefficient(1.0)
			    .setCompatibilityWeightCoefficient(0.4)
			    
			    .setSurvivalThreshold(0.4)
			    .setSelectionType(SELECTION_TYPE.TOURNAMENT)
			    .setTournamentSize(3)
			    
			    .setStagnation(20)
			    .setSpeciesElitism(3)
			    .setElitism(2)
			    
			    .setInitConnectivity(CONNECTIVITY.PARTIAL_NO_DIRECT)
			    .setProbConnectInit(0.5)
			    .setMaxNumberOfHiddenNodes(30)
			    
			    .setProbAddConnection(0.04)
			    .setProbAddNode(0.01)
			    
			    .build();
		
		neat = new Neat(neatConfig);
	}
	
	public synchronized void start() {
		if (isRunning)
			return;
		isRunning = true;
		thisThread.start();
	}
	public synchronized void stop() {
		if (!isRunning)
			return;
		isRunning = false;
		System.exit(0);
	}
	
	@Override
	public void run() {
		
		long lastUpdateTime = System.nanoTime();

		while (isRunning) {
			long targetDelta = 1000000000 / FRAME_RATE;
		    long now = System.nanoTime();
		    long updateDelta = now - lastUpdateTime;

		    if (!rendering || (rendering && updateDelta >= targetDelta)) {
		    	if (!paused)
		    		update();
		    	if (rendering)
		    		render();
		    	if (updateDelta >= targetDelta)
			        lastUpdateTime = now;
		    }
		}
		
	}
	
	@SuppressWarnings("unused")
	private synchronized void update() {
		if (recievingInputs && !Driver.AI)
			keyInput.update();
		
		if (Driver.AI) {
			nodeGraph.update();
			rectangleGraph.update();
		}		
		
		for (int i = startIndex; i < endIndex && i < worlds.length; i++)
			worlds[i].update();
		
		int count = 0;
		for (int i = startIndex; i < endIndex && i < worlds.length; i++)
			if (!worlds[i].getHandler().isActive())
				count++;
		
		if (currentWorldIndex >= neatConfig.getPopulationSize())
			currentWorldIndex = neatConfig.getPopulationSize()-1;
		else if (currentWorldIndex < 0)
			currentWorldIndex = 0;
		
		if (count == batchSize) {
			startIndex += batchSize;
			endIndex += batchSize;
			batch++;
		}
		
			if (!worlds[currentWorldIndex].getHandler().isActive()) {
				boolean recieved = recievingInputs;
				if (Driver.AI) {
					boolean isWorldActive = checkIfAWorldIsActive();
					if (!isWorldActive) {
						recievingInputs = false;
						newGeneration();
					}
						
				}else
					worlds[currentWorldIndex].startAgain();
				worlds[currentWorldIndex].getHandler().setIsActive(true);
				recievingInputs = recieved;
			}
						
	}

	private synchronized void render() {
		
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Render Background
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, Window.width, Window.height);
		
		// Render gameplay box.
		GameplayBox.render(g);
		
		// Render world
		worlds[currentWorldIndex].render(g);
		
		// Render corrections
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, GameplayBox.x+GameplayBox.width+1, GameplayBox.y);
		g2d.fillRect(0, 0, GameplayBox.x, GameplayBox.y+GameplayBox.height+1);
		g2d.fillRect(0, GameplayBox.endY+1, GameplayBox.x+GameplayBox.width+1, Window.height-GameplayBox.endY);
		g2d.fillRect(GameplayBox.endX+1, 0, Window.width-GameplayBox.endX-1, Window.height);
		
		Font font = new Font("Arial",Font.PLAIN,15);
		g2d.setFont(font);
		g2d.setColor(new Color(1f,1f,1f,0.7f));
		
		// Render AI box.
		if (Driver.AI) {
			AIBox.render(g);
			if (showGraph) {
				if (currentGraphShown == GraphType.NODE)
					nodeGraph.render(g2d);
				else rectangleGraph.render(g2d);
			} else {
				BufferedImage vg = GenomeVisualizer.visualizeGenome(genomeVisualizer,"#000000",showDisabled,false,worlds[currentWorldIndex].getPlayer().getAgent().getGenome(),
						neatConfig.getWeightMaxValue(), AIBox.width-1,AIBox.height-1);
				g2d.drawImage(vg, AIBox.x+1, AIBox.y+1,null);
			}
		}
		
		// HUD render
		int score = worlds[currentWorldIndex].getHandler().getPlayer().getScore();
		g2d.drawString("Score: " + score, GameplayBox.x, GameplayBox.y-font.getSize()/2);
		
		if (Driver.AI) {
			g2d.drawString("World  " + (currentWorldIndex+1), GameplayBox.x, g2d.getFontMetrics().getHeight());
			String generationAndBatch = "Generation " + neat.getGeneration() + " | Batch " + batch;
			int fontWidth = g2d.getFontMetrics().stringWidth(generationAndBatch);
			g2d.drawString(generationAndBatch, AIBox.centerX-(fontWidth/2), AIBox.y-g2d.getFontMetrics().getHeight()/3);
			
			String specieNumber = "Number of species: " + neat.getNumberOfSpecies();
			g2d.drawString(specieNumber, GameplayBox.x+120, g2d.getFontMetrics().getHeight());
			
			String agentSpecies = "Species: " + worlds[currentWorldIndex].getPlayer().getAgent().getSpeciesNumber();
			g2d.drawString(agentSpecies, GameplayBox.x+120, 2*g2d.getFontMetrics().getHeight());
			
			String playersLeft = "Players left: " + (neatConfig.getPopulationSize()-numberOfDeadAgents);
			fontWidth = g2d.getFontMetrics().stringWidth(playersLeft);
			g2d.drawString(playersLeft, GameplayBox.endX-fontWidth, GameplayBox.y-g2d.getFontMetrics().getHeight()/3);
			
			String numOfAgents = "Population: " + neatConfig.getPopulationSize();
			fontWidth = g2d.getFontMetrics().stringWidth(numOfAgents);
			g2d.drawString(numOfAgents, GameplayBox.endX-fontWidth, g2d.getFontMetrics().getHeight());
			
			String threshold = "Compt. Thres: " + String.format("%.3f", neat.getCurrentCompatibilityThreshold());
			fontWidth = g2d.getFontMetrics().stringWidth(threshold);
			g2d.drawString(threshold, AIBox.x, g2d.getFontMetrics().getHeight());
			
			String populationFitness = "Previous Generation Population Fitness: " + String.format("%.3f", neat.getPopulationFitness());
			g2d.drawString(populationFitness, GameplayBox.x, GameplayBox.endY+g2d.getFontMetrics().getHeight());

			String fitness = "Fitness: " + String.format("%.5f", worlds[currentWorldIndex].getPlayer().getFitness());
			fontWidth = g2d.getFontMetrics().stringWidth(fitness);
			g2d.drawString(fitness, GameplayBox.endX-fontWidth, GameplayBox.endY+3*g2d.getFontMetrics().getHeight());
			
			String elitism = "Elitism: " + neatConfig.getElitism();
			fontWidth = g2d.getFontMetrics().stringWidth(elitism);
			g2d.drawString(elitism, GameplayBox.endX-fontWidth, GameplayBox.endY+g2d.getFontMetrics().getHeight());
			
			
			if (showBoxes) {
				g2d.setColor(new Color(1,0,0,0.15f));
				g2d.fill(GameplayBox.topBox);
				g2d.fill(GameplayBox.bottomBox);
				g2d.fill(GameplayBox.rightBox);
				g2d.fill(GameplayBox.leftBox);
				g2d.fill(GameplayBox.topRightCornerBox);
				g2d.fill(GameplayBox.topLeftCornerBox);
				g2d.fill(GameplayBox.bottomRightCornerBox);
				g2d.fill(GameplayBox.bottomLeftCornerBox);
			}
			
		}
		
		if (paused) {
			g2d.setColor(Color.RED);
			g2d.drawRect(1, 1, Window.width-3, Window.height-3);
		}
		
		if (Driver.AI && mouseInput.getPoint() != null) {
			if (currentGraphShown == GraphType.NODE)
				mouseInput.renderNodeValue(g2d, nodeGraph, mouseInput.getPoint());
			else mouseInput.renderNodeValue(g2d, rectangleGraph, mouseInput.getPoint());
		}
		
		g2d.dispose();
		bs.show();
		
	}
	
	public static Color randomColor() {
		Random random = new Random();
		return new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256),256);
	}
	
	private boolean checkIfAWorldIsActive() {
		
		try {
			for (int i = currentWorldIndex+1; i < endIndex; i++) {
				if (worlds[i].getHandler().isActive()) {
					currentWorldIndex = i;
					return true;
				}
			}
		
			for (int i = currentWorldIndex-1; i >= startIndex; i--) {
				if (worlds[i].getHandler().isActive()) {
					currentWorldIndex = i;
					return true;
				}
			}
		}catch(ArrayIndexOutOfBoundsException e) {
			return false;
		}
		return false;
	}
	
	private void newGeneration() {
				
		currentWorldIndex = 0;
		gen_counter++;
		seed_counter++;
			
		if (gen_counter%EVALUATE_X_GENERATION == 0) {
		
			if (Driver.VALIDATE)
				neat.reset(true);
			else neat.evolve(true);
			Agent agent = neat.getBest();

			for (int i = 0; i < worlds.length; i++)
				worlds[i].getPlayer().resetAccuFitness();

			GenomeFileHandler.saveGenome(agent.getGenome(), "C:\\Users\\taher\\eclipse-workspace\\Asteroids_NEAT\\Genomes",((neat.getGeneration()-1)+""));
		
			nodeGraph.addValue(neat.getGeneration()-1, neat.getPopulationFitness());
			rectangleGraph.addValue(neat.getGeneration()-1, neat.getPopulationFitness());
			
			gen_counter = 0;
		}
		
		batch = 1;
		startIndex = 0;
		endIndex = batchSize;
		
		for (int i = 0; i < worlds.length; i++) {
			if (seed_counter >= SINGLE_SEED_PERIOD) {
				seed = generateSeed();
				seed_counter = 0;
			}
			worlds[i].setSeed(seed);
			worlds[i].startAgain();
			worlds[i].getPlayer().startOver();
			worlds[i].getHandler().setIsActive(true);
		}
		
		numberOfDeadAgents = 0;
				
	}
	
	public static void printOutArray(double[] actions) {
		System.out.println(Arrays.toString(actions));
	}
	
	public long generateSeed() {
		return random.nextLong();
	}
	
	public Graph getNodeGraph() { return nodeGraph.getGraph(); }
	public Graph getRectangleGraph() { return rectangleGraph.getGraph(); }
	
}
