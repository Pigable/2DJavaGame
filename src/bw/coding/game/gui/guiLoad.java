package bw.coding.game.gui;

import bw.coding.game.Game;
import bw.coding.game.InputHandler.InputEvent;
import bw.coding.game.InputHandler.InputEventType;
import bw.coding.game.gui.elements.FontRenderer;
import bw.coding.game.gui.elements.worldPreview;
import bw.coding.game.level.world;

import java.io.File;

public class guiLoad extends gui {

	worldPreview[] worlds;
	File[] worldFiles;
	int selectedEntry = 0;

	public guiLoad(Game game, int width, int height) {
		super(game, width, height);
		File p = new File(world.WORLD_DIR);
		File[] fs = p.listFiles();
		int valid = 0;
		for (File f : fs) {
			if (f.getName().endsWith(".dat")) {
				valid++;
			}
		}
		worldFiles = new File[valid];
		for (int i = 0; i < valid; i++) {
			worldFiles[i] = fs[i];
		}
		if (worldFiles != null) {
			worlds = new worldPreview[worldFiles.length];
			for (int i = 0; i < worldFiles.length; i++) {
				worlds[i] = new worldPreview(worldFiles[i].getAbsolutePath());
			}
		}
	}

	@Override
	public void actionPerformed(InputEvent event) {
		super.actionPerformed(event);

		if (event.key.id == input.down.id
				&& event.type == InputEventType.PRESSED) {
			selectedEntry++;
		}
		if (event.key.id == input.up.id && event.type == InputEventType.PRESSED) {
			selectedEntry--;
		}
		if (selectedEntry < 0) {
			selectedEntry = 0;
		}
		if (selectedEntry >= worlds.length) {
			selectedEntry = worlds.length - 1;
		}
		if (event.key.id == input.action.id
				&& event.type == InputEventType.PRESSED) {
			game.setWorld(new world(game, worldFiles[selectedEntry]));
			close();
		}
	}

	@Override
	public void render() {
		this.drawDefaultBackground();
		FontRenderer.drawCenteredString("Load", this, this.width / 2 + 1, 5,
				522, 2);

		int slotPosX = 20;
		int slotPosY = 30;
		int slotHeight = 27;
		int slotWidth = 160;

		if (worlds != null) {
			for (int i = 0; i < worlds.length; i++) {
				int frameColor = 0xa0a0a0;
				int fontColor = 333;

				if (i == selectedEntry) {
					frameColor = 0xdd5555;
					fontColor = 555;
				}

				this.drawRect(slotPosX, slotPosY + i * (slotHeight + 3),
						slotWidth, slotHeight, frameColor);

				FontRenderer.drawString("Name: " + worlds[i].name,this,
						slotPosX + 2, slotPosY + i * (slotHeight + 3) + 1,
						fontColor, 1);
				FontRenderer.drawString("Time played: " + worlds[i].timePlayed,
						this, slotPosX + 2,
						slotPosY + i * (slotHeight + 3) + 9, fontColor, 1);
				FontRenderer.drawString("Player name: " + worlds[i].playerName,
						this, slotPosX + 2, slotPosY + i * (slotHeight + 3)
								+ 17, fontColor, 1);
			}
		}
	}

	@Override
	public void tick(int ticks) {

	}

	@Override
	public void guiActionPerformed(int elementId, int action) {

	}
}

