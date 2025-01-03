package org.cl.pdk.duel.arena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.cl.pdk.duel.Settings;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public final class Arena<T extends Settings> implements Closeable {
    private final World world;
    private final File worldFile;
    private final T settings;

    private boolean closed;

    Arena(World world, File worldFile, T settings) {
        this.world = world;
        this.worldFile = worldFile;
        this.settings = settings;

        this.closed = false;
    }

    public World world() {
        return world;
    }

    public T settings() {
        return settings;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            throw new IOException("Map is already closed!");
        }

        this.closed = true;

        long start = System.currentTimeMillis();
        Bukkit.getLogger().info("Attempting to delete world " + world.getName() + "...");
        world.getPlayers().forEach(player -> player.kickPlayer(ChatColor.RED + "World Closing..."));

        Bukkit.unloadWorld(world, false);
        ArenaProvider.submit(worldFile::delete)
                .thenAccept(state -> {
                    long total = System.currentTimeMillis() - start;

                    if (state) {
                        Bukkit.getLogger().info("Successfully deleted " + world.getName() + " in " + total + "ms");
                    } else {
                        Bukkit.getLogger().severe("Failed to delete " + world.getName() + " (attempt took " + total + "ms)");
                    }
                });
    }
}
