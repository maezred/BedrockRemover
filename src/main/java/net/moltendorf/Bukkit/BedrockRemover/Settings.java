package net.moltendorf.Bukkit.BedrockRemover;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by moltendorf on 15/06/28.
 *
 * @author moltendorf
 */
public class Settings {
	protected static class WorldSettings {
		final public ConfigurationSection worldSection;
		final public String               name;
		final public List<Integer>        layers;
		final public Set<Material> blocks = new HashSet<>();

		public boolean isDone() {
			return done;
		}

		private boolean done = true;

		public int getStartZ() {
			return startZ;
		}

		public int getStartX() {
			return startX;
		}

		final private int startX;
		final private int startZ;

		public int getSide() {
			return side;
		}

		public int getRing() {
			return ring;
		}

		public int getX() {
			return x;
		}

		public int getZ() {
			return z;
		}

		private int side = 0;
		private int ring = 0;
		private int x    = 0;
		private int z    = 0;

		private WorldSettings(final ConfigurationSection worldSection) {
			this.worldSection = worldSection;

			name = worldSection.getName();

			layers = worldSection.getIntegerList("layers");

			for (final String block : worldSection.getStringList("blocks")) {
				final Material material = Material.getMaterial(block);

				if (material != null) {
					blocks.add(material);
				}
			}

			done = worldSection.getBoolean("done", done);

			startX = worldSection.getInt("startX", 0);
			startZ = worldSection.getInt("startZ", 0);

			ring = worldSection.getInt("ring", ring);
			side = worldSection.getInt("side", side);
			x = worldSection.getInt("x", startX - ring);
			z = worldSection.getInt("z", startZ - ring);
		}

		protected void setProgress(final int ring, final int side, final int x, final int z) {
			if (done) {
				done = false;

				worldSection.set("done", false);
			}

			this.ring = ring;
			this.x = x;
			this.z = z;
			this.side = side;

			worldSection.set("ring", ring);
			worldSection.set("side", side);
			worldSection.set("x", x);
			worldSection.set("z", z);

			getInstance().dirty = true;
		}

		protected void done() {
			if (!done) {
				done = true;
				ring = 0;
				side = 0;
				x = 0;
				z = 0;

				worldSection.set("done", true);
				worldSection.set("ring", null);
				worldSection.set("side", null);
				worldSection.set("x", null);
				worldSection.set("z", null);

				getInstance().dirty = true;
			}
		}
	}

	protected static Settings getInstance() {
		return BedrockRemover.getInstance().settings;
	}

	final private FileConfiguration config;
	private       boolean           dirty;

	public boolean isEnabled() {
		return enabled;
	}

	private boolean enabled = true; // Whether or not the plugin is enabled at all; interface mode.

	public int getSpeed() {
		return speed;
	}

	private int speed = 100;

	public int getMultiplier() {
		return multiplier;
	}

	private int multiplier = 100;

	public WorldSettings getWorld(final String world) {
		return worlds.get(world);
	}

	final private Map<String, WorldSettings> worlds = new HashMap<>();

	public Settings() {
		final BedrockRemover instance = BedrockRemover.getInstance();
		final Logger         log      = instance.getLogger();

		// Make sure the default configuration is saved.
		instance.saveDefaultConfig();

		config = instance.getConfig();

		if (config.isBoolean("enabled")) {
			enabled = config.getBoolean("enabled", enabled);
		} else {
			set("enabled", enabled);
		}

		if (config.isInt("speed")) {
			speed = config.getInt("speed", speed);
		} else {
			set("speed", speed);
		}

		if (config.isInt("multiplier")) {
			multiplier = config.getInt("multiplier", multiplier);
		} else {
			set("multiplier", multiplier);
		}

		final ConfigurationSection worldsSection = config.getConfigurationSection("worlds");

		for (final String world : worldsSection.getKeys(false)) {
			worlds.put(world, new WorldSettings(worldsSection.getConfigurationSection(world)));
		}

		save();
	}

	protected void save() {
		if (dirty) {
			BedrockRemover.getInstance().saveConfig();
			dirty = false;
		}
	}

	private void set(final String path, final Object value) {
		config.set(path, value);
		dirty = true;
	}
}
