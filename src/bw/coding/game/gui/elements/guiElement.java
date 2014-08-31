package bw.coding.game.gui.elements;

import bw.coding.game.InputHandler.GameActionListener;
import bw.coding.game.gui.gui;

public abstract class guiElement implements GameActionListener{
		
		protected gui parent;
		protected int id;

		public guiElement(int elementId, gui gui) {
			this.parent = gui;
		}
			
		public abstract void render(gui gui, int x, int y, int color);
	}

