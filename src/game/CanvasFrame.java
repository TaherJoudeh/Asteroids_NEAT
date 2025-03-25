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
import neat.Agent;
import neat.GenomeFileHandler;
import neat.GenomeVisualizer;
import neat.NEATConfig;
import neat.NEATConfig.CONNECTIVITY;
import neat.NEATConfig.DISTRIBUTION;
import neat.NEATConfig.FITNESS_CRITERION;
import neat.NEATConfig.SELECTION_TYPE;
import neat.NEATConfig.SPECIES_FITNESS_FUNCTION;
import neat.NEATConfigBuilder;
import neat.Neat;
import perceptron_config.ActivationConfig;
import perceptron_config.ActivationConfig.ACTIVATION_FUNCTION;
import perceptron_config.ActivationConfigBuilder;
import perceptron_config.AggregationConfig;
import perceptron_config.AggregationConfig.AGGREGATION_FUNCTION;
import perceptron_config.AggregationConfigBuilder;

public class CanvasFrame extends Canvas implements Runnable {

	private static final long serialVersionUID = -7762560848690684678L;
	private Thread thisThread;
	private boolean isRunning;
		
	private int batchSize = Driver.VALIDATE ? 1 : 50;
	
	public static World[] worlds;
	private NEATConfig neatConfig;
	private GenomeVisualizer genomeVisualizer;
	private Neat neat;
	
	private KeyInput keyInput;
	private MouseInput mouseInput;
	
	public static int startIndex;
	public static int endIndex;
	
	public static int currentWorldIndex = 0;
	public static int batch = 1;
	
	public static boolean showHitBox = false, showBoxes = false,
			paused = false, recievingInputs = true, rendering = true, showGraph = false,
			showDisabled = false;
	public static GraphType currentGraphShown = GraphType.NODE;
	
	private GraphHandler nodeGraph, rectangleGraph;
	
	public static int FRAME_RATE = 120;
	
	public static int numberOfDeadAgents = 0;
	
	public CanvasFrame() {
		
		setFocusTraversalKeysEnabled(false);
		requestFocusInWindow();
		
		init();
		
		addKeyListener(keyInput);
		addMouseMotionListener(mouseInput);
		
	}
	
	private void init() {
		thisThread = new Thread(this);
		
		initAI();
		endIndex = batchSize;
		
		thisThread.setPriority(Thread.MAX_PRIORITY);
		
		if (Driver.AI) {
			if (Driver.VALIDATE) {
				worlds = new World[1];
			} else worlds = new World[neatConfig.getPopulationSize()];
		} else worlds = new World[1];

		long generatedSeed = generateSeed();
		
		Agent[] agents = new Agent[neatConfig.getPopulationSize()];
		for (int i = 0; i < worlds.length; i++)
			worlds[i] = new World(generatedSeed);
		
		currentWorldIndex = 0;
		
		if (Driver.AI) {
			neat.insertAgents(agents);
			for (int i = 0; i < worlds.length; i++)
				worlds[i].getPlayer().setAgent(agents[i]);
			if (Driver.VALIDATE)
				worlds[0].getPlayer().getAgent().setGenome(GenomeFileHandler.loadGenome("C:\\My Stuff\\Programming\\AI\\Evelutionary\\Asteroids\\genome.neat"));
			nodeGraph = new GraphHandler(GraphType.NODE);
			rectangleGraph = new GraphHandler(GraphType.RECTANGLE);
		}
		
		keyInput = new KeyInput(worlds,worlds[0],this);
		if (Driver.AI)
			mouseInput = new MouseInput(worlds);
	}
	
	private void initAI() {
		genomeVisualizer = GenomeVisualizer.defaultGenomeColors();
		AggregationConfig agC = new AggregationConfigBuilder()
				.addAggregationFunction(AGGREGATION_FUNCTION.SUM)
//				.addAggregationFunction(AGGREGATION_FUNCTION.MEAN)
//				.addAggregationFunction(AGGREGATION_FUNCTION.MAX)
				.build();
		ActivationConfig acC = new ActivationConfigBuilder()
				.addActivationFunction(ACTIVATION_FUNCTION.SIGMOID)
//				.addActivationFunction(ACTIVATION_FUNCTION.SIGMOID)
//				.setStepActivationThreshold(0.5)
				.setLinearActivationThreshold(0)
//				.setSigmoidActivationThreshold(0.7)
				.build();
		neatConfig = new NEATConfigBuilder(agC,acC)
				.setActivationDefault(ACTIVATION_FUNCTION.RELU)
				.setStartingActivationFunctionForHiddenNodes(ACTIVATION_FUNCTION.RELU)
				.setStartingActivationFunctionForOutputNodes(ACTIVATION_FUNCTION.STEP)
				.setStartingAggregationFunction(AGGREGATION_FUNCTION.SUM)
				.setFeedForward(false)
				.setPopulationSize(300)
//				.setPopulationSize(1)
				.setNumberOfInputs(11)
				.setNumberOfOutputs(4)
//				.setStartingHiddenNodes(8)
				.setFitnessCriterion(FITNESS_CRITERION.MAX)
				.setFitnessTermination(true)
				.setFitnessThreshold(1000000)
				.setBiasInitType(DISTRIBUTION.NORMAL)
				.setBiasMaxValue(3)
				.setBiasMinValue(-3)
				.setBiasAdjustingRate(0.7)
				.setBiasRandomizingRate(0.1)
				.setBiasMutationPower(0.1)
				.setBiasInitStdev(1)
				.setWeightInitType(DISTRIBUTION.NORMAL)
				.setWeightMaxValue(3)
				.setWeightMinValue(-3)
				.setWeightAdjustingRate(0.8)
				.setWeightRandomizingRate(0.1)
				.setWeightMutationPower(0.1)
				.setWeightInitStdev(1)
				.setResponseMaxValue(3)
				.setResponseMinValue(-3)
				.setResponseInitType(DISTRIBUTION.NORMAL)
				.setResponseAdjustingRate(0.7)
				.setResponseRandomizingRate(0.1)
				.setResponseMutationPower(0.1)
				.setCompatibilityExcessCoefficient(1)
				.setCompatibilityDisjointCoefficient(1)
				.setCompatibilityWeightCoefficient(0.4)
				.setCompatibilityThreshold(3)
				.setCompatabilityThresholdAdjustingFactor(0.1)
				.setDynamicCompatabilityThreshold(false)
				.setTargetNumberOfSpecies(20)
				.setEnabledMutationRate(0.05)
				.setEnabledRateToEnabled(-0.05)
				.setInitConnectivity(CONNECTIVITY.PARTIAL_NO_DIRECT)
				.setProbConnectInit(0.6)
				.setSurvivalThreshold(0.2)
				.setSelectionType(SELECTION_TYPE.ROULETTE_WHEEL)
//				.setTournamentSize(3)
				.setStagnation(25)
				.setSpeciesFitnessFunction(SPECIES_FITNESS_FUNCTION.MEAN)
				.setSpeciesElitism(2)
				.setElitism(1)
				.setMaxNumberOfHiddenNodes(32)
				.setProbAddConnection(0.1)
				.setProbDeleteConnection(0.02)
				.setProbAddNode(0.05)
				.setProbDeleteNode(0.01)
				.setProbRecurrentConnection(0.2)
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
	
	/*
	@Override
	public void run() {
		long lastime = System.nanoTime();
		double AmountOfTicks = 120;
		double ns = 1000000000 / AmountOfTicks;
		double delta = 0;
		int frames = 0;
		double time = System.currentTimeMillis();
		
		while(isRunning == true) {
			long now = System.nanoTime();
			delta += (now - lastime) / ns;
			lastime = now;
			
			if(delta >= 1) {
				update();
				
//				try {
//					if (rendering)
//						Thread.sleep(3L);
//				} catch (InterruptedException e) { e.printStackTrace(); }
				
				render();
				frames++;
				delta--;
				if(System.currentTimeMillis() - time >= 1000) {
//					System.out.println("fps:" + frames);
					time += 1000;
					frames = 0;
				}
			}
		}
	}
	*/
	
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
	
	private synchronized void update() {
		if (recievingInputs && !Driver.AI)
			keyInput.update();
		
		nodeGraph.update();
		rectangleGraph.update();
		
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
		
		// Render stars
//		renderStars(g2d);
		
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
			
			String threshold = "Compt. Thres: " + String.format("%.3f", neatConfig.getCompatabilityThreshold());
			fontWidth = g2d.getFontMetrics().stringWidth(threshold);
			g2d.drawString(threshold, AIBox.x, g2d.getFontMetrics().getHeight());
			
			String populationFitness = "Previous Generation Population Fitness: " + String.format("%.3f", neat.getPopulationFitness());
			g2d.drawString(populationFitness, GameplayBox.x, GameplayBox.endY+g2d.getFontMetrics().getHeight());

			String fitness = "Fitness: " + String.format("%.5f", worlds[currentWorldIndex].getPlayer().getAgent().getFitness());
			fontWidth = g2d.getFontMetrics().stringWidth(fitness);
			g2d.drawString(fitness, GameplayBox.endX-fontWidth, GameplayBox.endY+3*g2d.getFontMetrics().getHeight());
			
//			String highscore = "Highscore: " + String.format("%.3f", Client.highscore);
//			g2d.drawString(highscore, GameplayBox.x, Window.height-g2d.getFontMetrics().getHeight());
			
			/*
			String geneticColor = "Genetic Colors: " + Genome.geneticColors;
			fontWidth = g2d.getFontMetrics().stringWidth(geneticColor);
			g2d.drawString(geneticColor, GameplayBox.endX-fontWidth, GameplayBox.endY+g2d.getFontMetrics().getHeight());
			*/
			
			String elitism = "Elitism: " + neatConfig.getElitism();
			fontWidth = g2d.getFontMetrics().stringWidth(elitism);
			g2d.drawString(elitism, GameplayBox.endX-fontWidth, GameplayBox.endY+g2d.getFontMetrics().getHeight());
			
//			String batchString = "Batch: " + batch;
//			fontWidth = g2d.getFontMetrics().stringWidth(batchString);
//			g2d.drawString(batchString, GameplayBox.centerX-fontWidth/2, GameplayBox.y-g2d.getFontMetrics().getHeight()/3);
			
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
				
		neat.evolve(true);
		
		if (neat.isFoundSolution()) {
			Agent agent = neat.getBest();
			agent.setFitness(0);
			GenomeFileHandler.saveGenome(agent.getGenome(), "C:\\My Stuff\\Programming\\AI\\Evelutionary\\Asteroids","genome");
			System.exit(0);
		}
		
		nodeGraph.addValue(neat.getGeneration()-1, neat.getPopulationFitness());
		rectangleGraph.addValue(neat.getGeneration()-1, neat.getPopulationFitness());
		
		batch = 1;
		startIndex = 0;
		endIndex = batchSize;
		
		long seed = generateSeed();
		for (int i = 0; i < worlds.length; i++) {
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
		return (long)(Math.random()-1)*10000;
	}
	
	public Graph getNodeGraph() { return nodeGraph.getGraph(); }
	public Graph getRectangleGraph() { return rectangleGraph.getGraph(); }
	
}
