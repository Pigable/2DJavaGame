package bw.coding.game.gui;

import bw.coding.game.Game;
import bw.coding.game.InputHandler;
import bw.coding.game.InputHandler.GameActionListener;
import bw.coding.game.InputHandler.InputEvent;
import bw.coding.game.InputHandler.InputEventType;
import bw.coding.game.gfx.SpriteSheet;

public abstract class gui implements GameActionListener {

	protected gui parentGui;
	protected Game game;
	public InputHandler input;
	private static int DEFAULT_BKG_COLOR = 0x555555;
	public SpriteSheet font;

	public int[] pixels;
	public int[] bkgPixels;
	public int width;
	public int height;

	protected boolean pauseGame;
	protected boolean closeOnEscape = true;

	public gui(Game game, int width, int height) {
		this.game = game;
		this.input = game.input;
		this.font = new SpriteSheet("/sprite_sheet.png");
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
		if (game.getWorld() != null && game.screen.pixels.length == pixels.length) {
			bkgPixels = game.pixels.clone();
		}
	}

	public abstract void render();

	public abstract void tick(int ticks);

	public abstract void guiActionPerformed(int elementId, int action);

	public void setParentGui(gui gui) {
		this.parentGui = gui;
	}


	public boolean pausesGame() {
		return pauseGame;
	}
	public void drawDefaultBackground() {
			for (int i = 0; i < pixels.length; i++) {
				pixels[i] = DEFAULT_BKG_COLOR;
			}
	}

	
	


	public void drawRect(int xPos, int yPos, int width, int height, int colour) {
		if (xPos > this.width)
			xPos = this.width - 1;
		if (yPos > this.height)
			yPos = this.height - 1;
		if (xPos + width > this.width)
			width = this.width - xPos;
		if (yPos + height > this.height)
			height = this.height - yPos;
		width -= 1;
		height -= 1;
		for (int x = xPos; x < xPos + width; x++) {
			pixels[x + yPos * this.width] = colour;
		}
		for (int y = yPos; y < yPos + height; y++) {
			pixels[xPos + y * this.width] = colour;
		}
		for (int x = xPos; x < xPos + width; x++) {
			pixels[x + (yPos + height) * this.width] = colour;
		}
		for (int y = yPos; y < yPos + height; y++) {
			pixels[(xPos + width) + y * this.width] = colour;
		}
	}

	public void fillRect(int xPos, int yPos, int width, int height, int color) {
		if (xPos > this.width)
			xPos = this.width;
		if (yPos > this.height)
			yPos = this.height;
		if (xPos + width > this.width)
			width = this.width - xPos;
		if (yPos + height > this.height)
			height = this.height - yPos;
		for (int y = yPos; y < yPos + height; y++) {
			for (int x = xPos; x < xPos + width; x++) {
				pixels[x + y * this.width] = color;
			}
		}
	}

	
	@Override
	public void actionPerformed(InputEvent event) {
		if (event.key.id == input.esc.id
				&& event.type == InputEventType.PRESSED && closeOnEscape) {
			last();
		}
	}

	
	public gui setParent(gui gui) {
		this.parentGui = gui;
		return this;
	}

	public void last() {
		game.hideGui(this);
	}
	
	public void close() {
		this.parentGui = null;
		game.hideGui(this);
	}

	public gui getParentGui() {
		return parentGui;
	}
}