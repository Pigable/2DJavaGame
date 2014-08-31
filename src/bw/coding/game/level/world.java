package bw.coding.game.level;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import bw.coding.game.Game;
import bw.coding.game.Tag;
import bw.coding.game.entities.Player;
import bw.coding.game.gfx.Screen;

public class world implements NBTCapable{
	public static String WORLD_DIR = Game.homeDir + "saves" + File.separator;
	public static int VERSION = 1;
	private Level level;
	private Level[] levels;
	private Player player;
	private Game game;
	private long timeStartThisSesson;
	private long timePlayed;
	private Tag worldTag;
	private String name;
	private String path;

	public world(String name) {
		this.name = name;
	}

	public world(Game game, String path) {
		this.game = game;
		File f = new File(path);
		if (!f.exists()) {
			
			return;
		}
		try {
			worldTag = Tag.readFrom(new FileInputStream(f));
			loadFromNBT(worldTag);
			loadAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		timeStartThisSesson = System.currentTimeMillis();
	}

	public world(File file) {
		this(null, file);
	}

	public world(Game game, File file) {
		this.game = game;
		try {
			worldTag = Tag.readFrom(new FileInputStream(file));
			loadFromNBT(worldTag);
			loadAll();
		} catch (IOException e) {
		}
		this.path = file.getAbsolutePath();
	}

	public void tick() {
				}

	public void render(Screen screen) {
		if (level != null) {
			int xOffset = 0;
			int yOffset = 0;
			if (player != null) {
				xOffset = player.x - (screen.width / 2);
				yOffset = player.y - (screen.height / 2);
			}

			level.renderTiles(screen, xOffset, yOffset);
			level.renderEntities(screen);
			player.render(screen);
		}
	}

	public void setLevel(String name) {
		if (this.level != null) {
			this.writeToFile();
		}
		if (levels != null) {
			for (Level l : levels) {
				if (l.getName().equalsIgnoreCase(name)) {
					this.level = l;
				}
			}
		} else {
			this.level = new Level(worldTag.findTagByName("LEVELS")
					.findTagByName(name.toUpperCase()));
		}
	}

	public void addLevel(Level level) {
		if (levels == null) {
			levels = new Level[0];
		}
		Level[] cache = levels.clone();
		levels = new Level[cache.length + 1];
		for (int i = 0; i < cache.length; i++) {
			levels[i] = cache[i];
		}
		levels[levels.length - 1] = level;
	}

	public Game getGame() {
		return game;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String[] getLevelNameList() {
		String[] names = null;
		if (levels == null) {
			if (worldTag != null) {
				Tag[] levels = (Tag[]) worldTag.findTagByName("LEVELS")
						.getValue();
				names = new String[levels.length];
				for (int i = 0; i < levels.length; i++) {
					names[i] = levels[i].findTagByName("NAME").getValue()
							.toString();
				}
			}
		} else {
			names = new String[levels.length];
			for (int i = 0; i < levels.length; i++) {
				names[i] = levels[i].getName();
			}
		}
		return names;
	}

	public Level[] getLevelList() {
		if (levels != null) {
			return levels;
		}
		return new Level[] { level };
	}

	public Level getLevel() {
		return level;
	}

	public void loadAll() {
		long ms = System.currentTimeMillis();
		Tag[] levelTags = (Tag[]) worldTag.findTagByName("LEVELS").getValue();
		levels = new Level[levelTags.length - 1];
		for (int i = 0; i < levelTags.length; i++) {
			if (levelTags[i].getType() == Tag.Type.TAG_Compound) {
				levels[i] = new Level(levelTags[i]);
			}
		}
	}

	public void unloadAll() {
		levels = null;
		System.gc();

	}

	public void writeToFile() {
		if (path == null) {
			return;
		}
		Tag tag = saveToNBT(null);
		File file = new File(path);
		// File path = file.getParentFile();
		// if(!path.exists()) {
		// Game.debug(Game.DebugLevel.INFO, "Save file directory created.");
		// path.mkdir();
		// }
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			tag.writeTo(new FileOutputStream(file));
			this.timeStartThisSesson = System.currentTimeMillis();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadFromNBT(Tag tag) {
		this.name = tag.findTagByName("NAME").getValue().toString();
		this.timePlayed = (long) tag.findTagByName("TIME_PLAYED").getValue();
		String startLevel = tag.findTagByName("STARTLEVEL").getValue()
				.toString().toUpperCase();
		level = new Level(tag.findTagByName("LEVELS").findTagByName(startLevel));
	}

	@Override
	public Tag saveToNBT(Tag notused) {
		timePlayed += (System.currentTimeMillis() - this.timeStartThisSesson);
		Tag tag = new Tag(Tag.Type.TAG_Compound, new StringBuilder()
				.append("WORLD_")
				.append((this.name).toUpperCase().replaceAll(" ", "_"))
				.toString(), new Tag[1]);
		tag.addTag(new Tag(Tag.Type.TAG_String, "NAME", this.name));
		tag.addTag(new Tag(Tag.Type.TAG_Long, "TIME_PLAYED", this.timePlayed));
		tag.addTag(new Tag(Tag.Type.TAG_String, "STARTLEVEL", level.getName()));
		// tag.addTag(game.level.saveToNBT(null));
		Tag levels = new Tag(Tag.Type.TAG_Compound, "LEVELS",
				new Tag[] { new Tag(Tag.Type.TAG_Int, "dump", 0) });
		// Tag levels = new Tag("LEVELS", Tag.Type.TAG_Compound);
		if (this.levels != null) {
			for (Level l : this.levels) {
				if (l != null) {
					levels.addTag(l.saveToNBT(new Tag(Tag.Type.TAG_Compound, l
							.getName().toUpperCase(), new Tag[] { new Tag(
							Tag.Type.TAG_Int, "dump", 0) })));
				}
			}
		} else {
			levels.addTag(level.saveToNBT(null));
		}
		levels.addTag(new Tag(Tag.Type.TAG_End, null, null));
		tag.addTag(levels);
		tag.addTag(new Tag(Tag.Type.TAG_End, null, null));
		return tag;
	}
}


