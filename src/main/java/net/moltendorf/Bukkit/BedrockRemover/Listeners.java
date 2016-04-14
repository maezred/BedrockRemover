package net.moltendorf.Bukkit.BedrockRemover;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * Created by moltendorf on 15/07/02.
 *
 * @author moltendorf
 */
public class Listeners implements Listener {
	@EventHandler
	public void ChunkLoadEventHandler(final ChunkLoadEvent event) {
		if (event.isNewChunk()) {
			final Settings.WorldSettings worldSettings = Settings.getInstance().getWorld(event.getWorld().getName());

			if (worldSettings != null) {
				BedrockRemover.remove(worldSettings, event.getChunk());
			}
		}
	}
}
