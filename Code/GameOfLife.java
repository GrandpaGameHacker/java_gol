import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Random;

/*
 * This is an Applet based simulation of game of life
 * Updated Tue 20 Sep, 11:46 AM 2016
 * Version 1.1
 * Category: Cellular Automata, Mathematics
 * Author: SamsonPianoFingers
 */

public class GameOfLife extends Applet implements MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = 1L;

	// Get fullscreen size
	private static final Dimension gridSize = Toolkit.getDefaultToolkit().getScreenSize();

	private static final int cellColor = Color.GREEN.getRGB();
	private boolean[][] cellAlive;
	private int modeState = 0;

	private Button step, reset, randomize, play, mode;
	private Random rand = new Random();
	private BufferedImage buffer;
	private Graphics bufferGraphics;
	public RenderThread rThread;
	private boolean[] survivalRules, birthingRules;
	int fps = 30;

	@Override
	public void init() {
		survivalRules = new boolean[9];
		birthingRules = new boolean[9];
		processRule("23/3");

		// Double buffer
		buffer = new BufferedImage(gridSize.width, gridSize.height, BufferedImage.TYPE_4BYTE_ABGR);
		bufferGraphics = buffer.getGraphics();

		// Push all buttons to the left.
		FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
		setLayout(fl);

		cellAlive = new boolean[gridSize.width][gridSize.height];
		rThread = new RenderThread(this, fps);

		// Button steps into next frame
		step = new Button("Step");

		// Button resets game state
		reset = new Button("Reset");

		// Button randomizes game state (cellAlive[][])
		randomize = new Button("Random");

		// Stops and starts render thread
		play = new Button("Play");

		// Changes the brush
		mode = new Button("Brush: Pixel    ");

		// For user drawing
		addMouseListener(this);
		addMouseMotionListener(this);

		// Changing brush type with scroll.
		addMouseWheelListener(this);

		// Anonymous listeners for buttons
		mode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modePressed();
			}
		});

		step.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stepPressed();
			}

		});

		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetPressed();
			}
		});

		randomize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				randomPressed();
			}
		});

		play.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				playPressed();
			}

		});

		// Add all controls to the gui
		add(step);
		add(reset);
		add(randomize);
		add(play);
		add(mode);

		// Finalise GUI
		doLayout();
		setSize(gridSize.width, gridSize.height);
	}

	/*
	 * Overide update to remove flicker because this prevents update from
	 * clearing the screen
	 */
	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void paint(Graphics g) {

		// Clear last frame
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.fillRect(0, 0, gridSize.width, gridSize.height);

		// Draw next frame
		for (int x = 1; x < gridSize.width - 1; x++) {
			for (int y = 1; y < gridSize.height - 1; y++) {
				if (cellAlive[x][y]) {
					// Set pixel green if [x][y] is alive
					buffer.setRGB(x, y, cellColor);
				}
			}
		}
		g.drawImage(buffer, 0, 0, null);
	}

	public void step() {

		// Count all 8 neighbours for each cell, saved to an array
		// neighbours[x][y]
		int[][] neighbours = new int[gridSize.width][gridSize.height];
		for (int x = 1; x < gridSize.width - 1; x++) {
			for (int y = 1; y < gridSize.height - 1; y++) {
				int n = 0;
				if (cellAlive[x - 1][y - 1]) {
					n += 1;
				}
				if (cellAlive[x - 1][y]) {
					n += 1;
				}
				if (cellAlive[x - 1][y + 1]) {
					n += 1;
				}
				if (cellAlive[x][y - 1]) {
					n += 1;
				}
				if (cellAlive[x][y + 1]) {
					n += 1;
				}
				if (cellAlive[x + 1][y - 1]) {
					n += 1;
				}
				if (cellAlive[x + 1][y]) {
					n += 1;
				}
				if (cellAlive[x + 1][y + 1]) {
					n += 1;
				}
				neighbours[x][y] = n;
			}
		}

		// Calculate next frame using rules from rules state
		for (int x = 1; x < gridSize.width - 1; x++) {
			for (int y = 1; y < gridSize.height - 1; y++) {
				for (int q = 0; q < 9; q++) {
					if(!survivalRules[q] && cellAlive[x][y] && neighbours[x][y] == q)
					{
						cellAlive[x][y] = false;
						continue;
					}							
					else if(survivalRules[q] && cellAlive[x][y] && neighbours[x][y] != q)
					{
						cellAlive[x][y] = true;
					}
					
					else if(birthingRules[q] && !cellAlive[x][y] && neighbours[x][y] == q){
						cellAlive[x][y] = true;
					}
				}

			}

		}

	}

	public void reset() {
		// Kills all cells
		for (int x = 1; x < gridSize.width - 1; x++) {
			for (int y = 1; y < gridSize.height - 1; y++) {
				cellAlive[x][y] = false;
			}
		}
	}

	public void randomize() {
		// Randomize all cells using Random.nextBoolean();
		for (int x = 1; x < gridSize.width - 1; x++) {
			for (int y = 1; y < gridSize.height - 1; y++) {
				cellAlive[x][y] = rand.nextBoolean();
			}
		}
	}
	
	/**
	 * Description
	 * @param rule A String containing a rule in this format "012345678/12345678"
	 */
	
	public void processRule(String rule) {
		resetRules();
		if (rule.contains("/")) {

			String[] rules = rule.split("\\/");
			for (char a : rules[0].toCharArray()) {
				int n = Integer.parseInt(String.valueOf(a));
				if (n < 9)
				{
					survivalRules[n] = true;
				}
			}

			for (char a : rules[1].toCharArray())
			{
				int n = Integer.parseInt(String.valueOf(a));
				if (n < 9)
				{
					birthingRules[n] = true;
				}
			}
		}
	}

	public void resetRules() {
		for (int i = 0; i < 9; i++) {
			survivalRules[i] = false;
			birthingRules[i] = false;
		}
	}
	
	public void randomRule() {
		int limit1 = Math.abs(rand.nextInt(99999)+1);
		int limit2 = Math.abs(rand.nextInt(99999)+1);
		
		int a = Math.abs(rand.nextInt(99999999)/limit1);
		int b = Math.abs(rand.nextInt(99999999)/limit2);
		String rule = String.format("%d/%d", a,b);
		rule = rule.replaceAll("[0]", "");
		processRule(rule);
	}

	public void playPressed() {
		// When pressed, If Render thread is not playing, create new thread.
		if (!rThread.isPlaying) {
			play.setLabel("Stop");
			rThread = new RenderThread(this, fps);
			rThread.start();
		}
		// When pressed, If Render thread is playing, kill it
		else {
			play.setLabel("Play");
			rThread.isPlaying = false;
		}
	}

	private void randomPressed() {
		/*
		 * To avoid overwriting the array during calculation in Render thread
		 * pause game, then apply randomization, this is the format in all
		 * button functions.
		 */
		if (rThread.isPlaying) {
			playPressed();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			reset();
			randomize();
			repaint();
			return;
		}

		randomize();
		repaint();
	}

	private void resetPressed() {
		if (rThread.isPlaying) {
			playPressed();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			reset();
			repaint();
			return;
		}
		reset();
		repaint();

	}

	private void stepPressed() {
		if (rThread.isPlaying) {
			playPressed();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			step();
			repaint();
			return;
		}
		step();
		repaint();
	}

	private void modePressed() {
		if (modeState == 4) {
			// Reset state when reach max brush
			modeState = 0;
		} else {
			modeState++;
		}
		// Label the button to the current state.
		switch (modeState) {
		case 0:
			mode.setLabel("Brush: Pixel");
			break;
		case 1:
			mode.setLabel("Brush: Glider");
			break;
		case 2:
			mode.setLabel("Brush: Square");
			break;
		case 3:
			mode.setLabel("Brush: Random");
			break;
		case 4:
			mode.setLabel("Brush: Invert");
			break;
		}
	}

	private void drawCells(int x, int y, boolean state) {
		// Prevent out of bounds exception
		if (x > 0 && y > 0 && x < gridSize.width - 1 && y < gridSize.height - 1) {
			try {
				/*
				 * Selects the brush according to the brush state (integer) and
				 * draws with that brush.
				 */
				switch (modeState) {
				case 0:
					// Pixel draw
					cellAlive[x][y] = state;
					break;
				case 1:
					// Glider
					cellAlive[x][y - 1] = state;
					cellAlive[x + 1][y] = state;
					cellAlive[x - 1][y + 1] = state;
					cellAlive[x][y + 1] = state;
					cellAlive[x + 1][y + 1] = state;
					break;
				case 2:
					// Square
					cellAlive[x - 1][y - 1] = state;
					cellAlive[x - 1][y] = state;
					cellAlive[x - 1][y + 1] = state;
					cellAlive[x][y - 1] = state;
					cellAlive[x][y] = state;
					cellAlive[x][y + 1] = state;
					cellAlive[x + 1][y - 1] = state;
					cellAlive[x + 1][y] = state;
					cellAlive[x + 1][y + 1] = state;
					break;
				case 3:
					// Random square using Random object
					cellAlive[x - 1][y - 1] = rand.nextBoolean();
					cellAlive[x - 1][y] = rand.nextBoolean();
					cellAlive[x - 1][y + 1] = rand.nextBoolean();
					cellAlive[x][y - 1] = rand.nextBoolean();
					cellAlive[x][y] = rand.nextBoolean();
					cellAlive[x][y + 1] = rand.nextBoolean();
					cellAlive[x + 1][y - 1] = rand.nextBoolean();
					cellAlive[x + 1][y] = rand.nextBoolean();
					cellAlive[x + 1][y + 1] = rand.nextBoolean();

					break;
				case 4:
					/*
					 * Invert brush: preserving game state during calculation
					 * stops an unintended drawing bug when dragging the brush.
					 */
					boolean[] savedState = new boolean[9];
					savedState[0] = !cellAlive[x - 1][y - 1];
					savedState[1] = !cellAlive[x - 1][y];
					savedState[2] = !cellAlive[x - 1][y + 1];
					savedState[3] = !cellAlive[x][y - 1];
					savedState[4] = !cellAlive[x][y];
					savedState[5] = !cellAlive[x][y + 1];
					savedState[6] = !cellAlive[x + 1][y - 1];
					savedState[7] = !cellAlive[x + 1][y];
					savedState[8] = !cellAlive[x + 1][y + 1];

					cellAlive[x - 1][y - 1] = savedState[0];
					cellAlive[x - 1][y] = savedState[1];
					cellAlive[x - 1][y + 1] = savedState[2];
					cellAlive[x][y - 1] = savedState[3];
					cellAlive[x][y] = savedState[4];
					cellAlive[x][y + 1] = savedState[5];
					cellAlive[x + 1][y - 1] = savedState[6];
					cellAlive[x + 1][y] = savedState[7];
					cellAlive[x + 1][y + 1] = savedState[8];
				}

			} catch (Exception e) {
				// Catch any out of bounds bugs (debugging)
				System.err.println(e.getMessage());
			}
			repaint();
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		int button = e.getButton();
		// If button was left mouse click, draw.
		if (button == MouseEvent.BUTTON1) {
			drawCells(e.getX(), e.getY(), true);
		}
		// If button was right mouse click, erase.
		if (button == MouseEvent.BUTTON3) {
			drawCells(e.getX(), e.getY(), false);
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int exModifier = e.getModifiersEx();
		// If mouse button dragged was left, draw
		if (exModifier == MouseEvent.BUTTON1_DOWN_MASK) {
			drawCells(e.getX(), e.getY(), true);
		}
		// If mouse button dragged was right, erase
		if (exModifier == MouseEvent.BUTTON3_DOWN_MASK) {
			drawCells(e.getX(), e.getY(), false);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// Scroll through brushes
		randomRule();
	}

	// Ignore Below. Just empty @Overrides

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

}
