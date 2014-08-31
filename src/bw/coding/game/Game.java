package bw.coding.game;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

import javax.swing.JFrame;

import bw.coding.game.gfx.Screen;
import bw.coding.game.gfx.SpriteSheet;
import bw.coding.game.gui.gui;
import bw.coding.game.gui.guiHUD;
import bw.coding.game.gui.guiMainMenu;
import bw.coding.game.level.world;

public class Game extends Canvas implements Runnable, FocusListener {

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 240;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 2;
	public static final String NAME = "Game";
	public static final Dimension DIMENSIONS = new Dimension(WIDTH * SCALE,
			HEIGHT * SCALE);

	public JFrame frame;
	private Thread thread;

	private boolean running = false;
	public int ticks = 0;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
			BufferedImage.TYPE_INT_RGB);
	public int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer())
			.getData();
	private int[] colors = new int[6 * 6 * 6];

	public Screen screen;
	public InputHandler input;

	private world world;
	private gui gui;
	public guiHUD hud;

	public static boolean isApplet = false;
	public boolean isFocused = true;

	public boolean debug;

	public static boolean DEBUG = true;

	public static Game instance;
	public static String homeDir;

	public void init() {
		int index = 0;
		for (int r = 0; r < 6; r++) {
			for (int g = 0; g < 6; g++) {
				for (int b = 0; b < 6; b++) {
					int rr = (r * 255 / 5);
					int gg = (g * 255 / 5);
					int bb = (b * 255 / 5);

					colors[index++] = rr << 16 | gg << 8 | bb;
				}
			}
		}

		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet.png"));
		input = new InputHandler(this);

		showGui(new guiMainMenu(this, WIDTH, HEIGHT));
	}

	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 60D;

		int ticksPS = 0;
		int framesPS = 0;

		long lastTimer = System.currentTimeMillis();
		double delta = 0;

		init();

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;

			boolean shouldRender = true;

			while (delta >= 1) {
				ticksPS++;
				tick();
				delta -= 1;
				shouldRender = true;
			}

			try {
				Thread.sleep(5L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (shouldRender) {
				framesPS++;
				render();
			}

			if (System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
				System.out.println (framesPS + " frames, " + ticksPS
						+ " ticks");
				framesPS = 0;
				ticksPS = 0;
			}
		}
	}

	public void tick() {
		input.tick();
		ticks++;
		if (gui != null) {
			gui.tick(ticks);
			if (gui != null && !gui.pausesGame()) {
				if (world != null) {
					tickLevel();
				}
			}
		} else {
			if (world != null) {
				tickLevel();
			}
		}
	}

	public world getWorld() {
		return world;
	}

	public void setWorld(world world) {
		this.world = world;
	}

	private void tickLevel() {
		world.tick();
		if (hud != null) {
			hud.tick(ticks);
		}
	}

	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (gui != null) {
			gui.render();
			for (int y = 0; y < HEIGHT; y++) {
				for (int x = 0; x < WIDTH; x++) {
					int colorCode = gui.pixels[x + y * gui.width];
					pixels[x + y * WIDTH] = colorCode + (0xFF << 24);
				}
			}
		} else {
			if (world != null) {
				world.render(screen);

				for (int y = 0; y < screen.height; y++) {
					for (int x = 0; x < screen.width; x++) {
						int colorCode = screen.pixels[x + y * screen.width];
						if (colorCode < 255) {
							pixels[x + y * WIDTH] = colors[colorCode];
						}
					}
				}
				if (hud != null) {
					hud.pixels = pixels;
					hud.render();
				}
			}
		}

		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

		g.dispose();
		bs.show();
	}

	/**
	 * Opens a GUI to display.
	 * 
	 * @param gui
	 *            The GUI that should be displayed. Usually a new instance.
	 */
	public void showGui(gui gui) {
		if(this.gui != null) {
			input.removeListener(this.gui);
		}
		this.gui = null;
		this.gui = gui;
		input.addListener(this.gui);
	}

	/**
	 * Closes the currently displayed GUI.
	 * 
	 * @param gui The GUI that should be closed.
	 */
	public void hideGui(gui gui) {
		input.removeListener(gui);
		if (gui.getParentGui() != null) {
			showGui(gui.getParentGui());
		}
		if (this.gui == gui) {
			this.gui = null;
		}
	}

	/**
	 * Forces any menu to close. THIS IS ONLY USED IN THE LEVEL EDITOR.
	 */
	public void forceGuiClose() {
		if (gui != null) {
			gui.close();
		}
		gui = null;
	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this, Game.NAME + "_main");
		thread.start();
	}

	public synchronized void stop() {
		running = false;

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

	/**
	 * Sends a debug message to the console.
	 * 
	 * @param level
	 *            The debug level (what type of message, "INFO", "WARNING",
	 *            "ERROR")
	 * @param msg
	 *            The text to output.
	 */
