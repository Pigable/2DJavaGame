package bw.coding.game.gui;

import javax.swing.text.html.Option;

import bw.coding.game.Game;
import bw.coding.game.InputHandler.InputEvent;
import bw.coding.game.gui.elements.FontRenderer;
import bw.coding.game.gui.elements.chooseList;

public class guiMainMenu extends gui {

	public chooseList list;
	private String splash = "Unnamed Test Game v1.0";

	public guiMainMenu(Game game, int width, int height) {
		super(game, width, height);
		this.pauseGame = true;
		list = new chooseList(0, this);
		list.setMaximumDisplayed(10);
		list.addOption(list.new Option(0, "Create"));
		list.addOption(list.new Option(1, "Load"));
		list.addOption(list.new Option(2, "Options"));
		list.addOption(list.new Option(3, "Exit"));
	}

	public void render() {
		this.drawDefaultBackground();
		FontRenderer.drawCenteredString("Main Menu", this, width / 2 + 1, 5,
				225, 2);
		list.render(this, 10, 30, 225);
		FontRenderer.drawString(splash, this, 2, height - 10, 000, 1);
	}

	public void tick(int ticks) {

	}

	public void guiActionPerformed(int elementId, int action) {
		if (elementId == list.getId()) {
			switch (action) {
			case 0:
				close();
				break;
			case 1:
				close();
				game.showGui(new guiLoad(game, Game.WIDTH, Game.HEIGHT).setParent(this));
				break;
			case 2:
				splash = "No options, yet!";
				break;
			case 3:
				System.out.println("The game has been quit!");
				System.exit(0);
				break;
			}
		}
	}

	public void actionPerformed(InputEvent event) {
		list.actionPerformed(event);
	}
}
