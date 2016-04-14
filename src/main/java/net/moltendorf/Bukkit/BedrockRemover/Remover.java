package net.moltendorf.Bukkit.BedrockRemover;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by moltendorf on 15/07/04.
 *
 * @author moltendorf
 */
public class Remover extends BukkitRunnable {
	private static int tasks = 0;

	private final Settings.WorldSettings worldSettings;
	private final World                  world;

	private final int multiplier;
	private final int speed;

	private final int startX;
	private final int startZ;

	private int ring = 0;
	private int side = 0;
	private int x    = 0;
	private int z    = 0;

	private int runs = 0;

	private int hit     = 0;
	private int missed  = 0;
	private int _missed = 0;

	public Remover(final Settings.WorldSettings worldSettings) {
		this.worldSettings = worldSettings;
		world = BedrockRemover.getInstance().getServer().getWorld(worldSettings.name);

		final Settings settings = Settings.getInstance();

		multiplier = settings.getMultiplier();
		speed = settings.getSpeed()*multiplier;

		startX = worldSettings.getStartX();
		startZ = worldSettings.getStartZ();

		ring = worldSettings.getRing();
		side = worldSettings.getSide();
		x = worldSettings.getX();
		z = worldSettings.getZ();

		++tasks;
	}

	@Override
	public void run() {
		final Runtime runtime = Runtime.getRuntime();

		final long   totalMemory = runtime.totalMemory();
		final long   maxMemory   = runtime.maxMemory();
		final long   freeMemory  = maxMemory - totalMemory + runtime.freeMemory();
		final double freePercent = (double)freeMemory/maxMemory;

		if (freePercent < .2) {
			return;
		}

		int       i = 0;
		final int j = speed/tasks;

		if (ring == 0) {
			if (world.loadChunk(startX, startZ, false)) {
				BedrockRemover.remove(worldSettings, world.getChunkAt(startX, startZ));

				i += multiplier;
			} else {
				++i;
				++missed;
			}

			++ring;

			x = startX - ring;
			z = startZ - ring;
		}

	outer:
		do {
			if (side == 0) {
				for (int maxX = startX + ring; x < maxX; ++x) {
					if (i >= j) {
						break outer;
					}

					if (world.loadChunk(x, z, false)) {
						BedrockRemover.remove(worldSettings, world.getChunkAt(x, z));

						i += multiplier;
						++hit;
					} else {
						++i;
						++missed;
						++_missed;
					}
				}

				++side;
			}

			if (side == 1) {
				for (int maxZ = startZ + ring; z < maxZ; ++z) {
					if (i >= j) {
						break outer;
					}

					if (world.loadChunk(x, z, false)) {
						BedrockRemover.remove(worldSettings, world.getChunkAt(x, z));

						i += multiplier;
						++hit;
					} else {
						++i;
						++missed;
						++_missed;
					}
				}

				++side;
			}

			if (side == 2) {
				for (int minX = startX - ring; x > minX; --x) {
					if (i >= j) {
						break outer;
					}

					if (world.loadChunk(x, z, false)) {
						BedrockRemover.remove(worldSettings, world.getChunkAt(x, z));

						i += multiplier;
						++hit;
					} else {
						++i;
						++missed;
						++_missed;
					}
				}

				++side;
			}

			if (side == 3) {
				for (int minZ = startZ - ring; z > minZ; --z) {
					if (i >= j) {
						break outer;
					}

					if (world.loadChunk(x, z, false)) {
						BedrockRemover.remove(worldSettings, world.getChunkAt(x, z));

						i += multiplier;
						++hit;
					} else {
						++i;
						++missed;
						++_missed;
					}
				}
			}

			if (ring*8 == _missed) {
				cancel();

				worldSettings.done();

				final String message = "Completed processing blocks in " + worldSettings.name + ".";
				BedrockRemover.getInstance().getLogger().info(message);

				return;
			}

			_missed = 0;

			++ring;
			side = 0;

			x = startX - ring;
			z = startZ - ring;

			if (i >= j) {
				break;
			}
		} while (true);

		if (runs++ > 200) {
			final String message = "Processed " + hit + " (+" + missed + " empty) chunks in " + worldSettings.name + ".";
			BedrockRemover.getInstance().getLogger().info(message);

			runs = 0;
			hit = 0;
			missed = 0;
		}

		worldSettings.setProgress(ring, side, x, z);
	}
}
