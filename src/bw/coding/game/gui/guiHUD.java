package bw.coding.game.gui;

import java.awt.event.InputEvent;

import bw.coding.game.Game;
import bw.coding.game.InputHandler;
import bw.coding.game.entities.Player;
import bw.coding.game.gfx.Colours;
import bw.coding.game.gui.elements.GuiRenderer;

public class guiHUD extends gui {

	private Player player;

	public guiHUD(Game game, Player player, int width, int height) {
		super(game, width, height);
		this.player = player;
	}

	public void actionPerformed(InputEvent event) {

	}

	public void render() {
		for (int i = 0; i < player.getMaxHealth(); i++) {
			int colour;
			if (i < player.getHealth()) {
				colour = Colours.get(-1, -1, 400, 400);
				if (!player.shouldDisplay())
					colour = Colours.get(-1, -1, 555, 400);
			} else {
				colour = Colours.get(-1, -1, 222, 222);
				if (!player.shouldDisplay())
					colour = Colours.get(-1, -1, 555, 222);
			}

			GuiRenderer.render(this, font, 2 + i * 16, 2, 32, colour, 0x00, 2);
		}
	}

	public void tick(int ticks) {

	}

	public void guiActionPerformed(int elementId, int action) {

	}

	@Override
	public void actionPerformed(bw.coding.game.InputHandler.InputEvent event) {
		// TODO Auto-generated method stub

	}
}
