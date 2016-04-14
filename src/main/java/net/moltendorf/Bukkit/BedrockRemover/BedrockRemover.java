package net.moltendorf.Bukkit.BedrockRemover;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by moltendorf on 15/06/28.
 *
 * @author moltendorf
 */
public class BedrockRemover extends JavaPlugin {
	// Main instance.
	private static BedrockRemover instance = null;

	protected static BedrockRemover getInstance() {
		return instance;
	}

	// Variable data.
	protected Settings settings = null;

	@Override
	public void onEnable() {
		instance = this;

		// Construct new settings.
		settings = new Settings();

		if (settings.isEnabled()) {
			final Server server = getServer();

			// Start plugin.
			server.getPluginManager().registerEvents(new Listeners(), this);

			for (final World world : server.getWorlds()) {
				final Settings.WorldSettings worldSettings = settings.getWorld(world.getName());

				if (worldSettings != null && !worldSettings.isDone()) {
					(new Remover(worldSettings)).runTaskTimer(this, 0L, 1L);
				}
			}
		}
	}

	@Override
	public void onDisable() {
		settings.save();

		instance = null;
	}

	protected static void remove(final Settings.WorldSettings worldSettings, final Chunk chunk) {
		for (final Integer y : worldSettings.layers) {
			for (int x = 0; x < 16; ++x) {
				for (int z = 0; z < 16; ++z) {
					final Block block = chunk.getBlock(x, y, z);

					if (worldSettings.blocks.contains(block.getType())) {
						block.setType(Material.AIR, false);
					}
				}
			}
		}
	}
}
