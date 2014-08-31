package bw.coding.game.level;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import bw.coding.game.Game;
import bw.coding.game.Tag;
import bw.coding.game.entities.Entity;
import bw.coding.game.entity.entityLoader;
import bw.coding.game.gfx.Screen;
import bw.coding.game.level.tiles.Tile;

public class Level implements NBTCapable {

	private byte[] tiles;
	private byte[] meta;
	private byte[] overlay;
	private int width;
	private int height;
	private String name;
	private List<Entity> entities = new ArrayList<Entity>();

	public Level(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.tiles = new byte[width * height];
	}

	public Level(Tag tag) {
		this.name = "LEVEL";
		this.loadFromNBT(tag);
	}

	public void tick() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).tick();
		}

		for (Tile t : Tile.tiles) {
			if (t == null) {
				break;
			}
			t.tick();
		}

		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			for (Entity e2 : doCollision(e)) {
				e.onCollide(e2);
			}
		}
	}

	public Entity[] doCollision(Entity e) {
		List<Entity> ents = new ArrayList<Entity>();
		for (int j = 0; j < entities.size(); j++) {
			Entity e1 = entities.get(j);
			if (e != e1 && e.doesCollideWith(e1)) {
				e.onCollide(e1);
			}
		}
		Entity[] res = new Entity[ents.size()];
		for (int i = 0; i < ents.size(); i++) {
			res[i] = ents.get(i);
		}
		return res;
	}

	public int getUniqueEntityId() {
		int id = 0;
		boolean found = false;
		while (!found) {
			boolean match = false;
			for (Entity e : entities) {
				if (e.getId() == id) {
					match = true;
					break;
				}
			}
			if (!match) {
				found = true;
			} else {
				id++;
			}
		}
		return id;
	}

	public void generateLevel() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x * y % 10 < 5) {
					tiles[x + y * width] = Tile.GRASS.getId();
				} else {
					tiles[x + y * width] = Tile.STONE.getId();
				}
			}
		}
	}

	public void renderTiles(Screen screen, int xOffset, int yOffset) {
		if (xOffset < 0)
			xOffset = 0;
		if (xOffset > ((width << 3) - screen.width))
			xOffset = (width << 3) - screen.width;
		if (yOffset < 0)
			yOffset = 0;
		if (yOffset > ((height << 3) - screen.height))
			yOffset = (height << 3) - screen.height;

		screen.setOffset(xOffset, yOffset);

		for (int y = (yOffset >> 3); y < (yOffset + screen.height >> 3) + 1; y++) {
			for (int x = (xOffset >> 3); x < (xOffset + screen.width >> 3) + 1; x++) {
				getTile(x, y).render(screen, this, x * 8, y * 8);
			}
		}
	}

	public void renderEntities(Screen screen) {
		for (int renderLayer = 1; renderLayer <= 3; renderLayer++) {
			for (Entity e : entities) {
				if (e.getRenderLayer() == renderLayer) {
					e.render(screen);
				}
			}
		}
	}

	public boolean removeEntity(Entity entity) {
		entity.atEntityRemoved(this);
		return entities.remove(entity);
	}

	public Entity[] getEntityWithin(int x1, int y1, int x2, int y2) {
		ArrayList<Entity> match = new ArrayList<Entity>();
		for (Entity e : entities) {
			if ((e.x >= x1 && e.x < x2) && (e.y >= y1 && e.y < y2)) {
				match.add(e);
			}
		}
		Entity[] result = new Entity[match.size()];
		int i = 0;
		for (Entity e : match) {
			result[i] = e;
			i++;
		}
		return result;
	}

	public byte[] getTileIdArray() {
		return tiles;
	}

	public void setTiles(byte[] t) {
		if (t.length == width * height) {
			for (int i = 0; i < t.length; i++) {
				tiles[i] = t[i];
	}
		}
	}

	public void setTile(byte id, int x, int y) {
		tiles[x + y * width] = id;
	}

	public Tile getTile(int x, int y) {
		if (0 > x || x >= width || 0 > y || y >= height)
			return Tile.VOID;
		return Tile.tiles[tiles[x + y * width]];
	}

	public void addEntity(Entity eintity) {
		entities.add(eintity);
	}

	public int getWidthInTiles() {
		return width;
	}

	public int getHeightInTiles() {
		return height;
	}

	public String getName() {
		return name;
	}

	@Override
	public void loadFromNBT(Tag tag) {
		this.name = tag.findTagByName("NAME").getValue().toString();
		this.width = (int) tag.findTagByName("WIDTH").getValue();
		this.height = (int) tag.findTagByName("HEIGHT").getValue();
		this.tiles = (byte[]) tag.findTagByName("TILES").getValue();
		// this.meta = (byte[]) tag.findTagByName("META").getValue();
		// this.overlay = (byte[]) tag.findTagByName("OVERLAY").getValue();
		if (tiles.length != width * height) {
		}
		// if (meta.length != width * height) {
		// Game.debug(Game.DebugLevel.WARNING, "Meta data corrupted!");
		// Game.debug(Game.DebugLevel.ERROR, "Error while loading level \""
		// + name + "\"!");
		// }
		// if (overlay.length != width * height) {
		// Game.debug(Game.DebugLevel.WARNING, "Overlay data corrupted!");
		// Game.debug(Game.DebugLevel.ERROR, "Error while loading level \""
		// + name + "\"!");
		// }
		Tag ents = tag.findTagByName("ENTITIES");
		for (Tag t : (Tag[]) ents.getValue()) {
			System.out.println("Loading " + t.getName());
			if (t.getType() == Tag.Type.TAG_Compound) {
				this.addEntity(entityLoader.loadEntity(this, t));
			}
		}
	}

	@Override
	public Tag saveToNBT(Tag tag) {
		tag.addTag(new Tag(Tag.Type.TAG_String, "NAME", this.getName()));
		tag.addTag(new Tag(Tag.Type.TAG_Int, "WIDTH", this.getWidthInTiles()));
		tag.addTag(new Tag(Tag.Type.TAG_Int, "HEIGHT", this.getHeightInTiles()));
		tag.addTag(new Tag(Tag.Type.TAG_Byte_Array, "TILES", this
				.getTileIdArray()));
		// tag.addTag(new Tag(Tag.Type.TAG_Byte_Array, "META", this.meta));
		// tag.addTag(new Tag(Tag.Type.TAG_Byte_Array, "OVERLAY",
		// this.overlay));
		Tag ents = new Tag(Tag.Type.TAG_Compound, "ENTITIES",
				new Tag[] { new Tag(Tag.Type.TAG_Int, "dump", 0) });
		for (int i = 0; i < entities.size(); i++) {
			// Tag e = new Tag(Tag.Type.TAG_Compound, "ENTITY_" +
			// entities.get(i).getId(), new Tag[] {new Tag(Tag.Type.TAG_Int,
			// "dump", 0)});
			ents.addTag(entityLoader.saveObject(entities.get(i)));
			// ents.addTag(e);
		}
		ents.addTag(new Tag(Tag.Type.TAG_End, null, null));
		tag.addTag(ents);
		tag.addTag(new Tag(Tag.Type.TAG_End, null, null));
		return tag;
	}
}