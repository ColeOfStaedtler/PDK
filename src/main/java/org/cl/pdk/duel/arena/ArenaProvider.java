package org.cl.pdk.duel.arena;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.cl.pdk.duel.Settings;
import org.cl.pdk.misc.TypeToken;
import org.cl.pdk.world.EmptyChunkGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ArenaProvider<T extends Settings> {
    private static final String WORLD_NAME = "world";
    private static final String SETTINGS_NAME = "settings.json";

    private static final ExecutorService IO_SERVICE = Executors.newSingleThreadExecutor();

    private final String name;
    private final File worldFile;
    private final T settings;

    static <T> CompletableFuture<T> submit(Callable<T> task) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        IO_SERVICE.submit(() -> completableFuture.complete(task.call()));

        return completableFuture;
    }

    public static <T extends Settings> ArenaProvider<T> of(TypeToken<T> token, String directory, Gson gson) throws IOException {
        String name = new File(directory).getName();
        File worldFile = new File(directory + File.separator + WORLD_NAME);
        File settingsFile = new File(directory + File.separator + SETTINGS_NAME);

        T settings;
        try (JsonReader reader = new JsonReader(new FileReader(settingsFile))) {
            settings = gson.fromJson(reader, token.type());
        }

        return new ArenaProvider<>(name, worldFile, settings);
    }

    private ArenaProvider(String name, File worldFile, T settings) {
        this.name = name;
        this.worldFile = worldFile;
        this.settings = settings;
    }

    public String name() {
        return name;
    }

    public void open(String temporaryName, JavaPlugin plugin, Consumer<Arena<T>> mapConsumer) {
        submit(() -> {
            File dest = new File(Bukkit.getWorldContainer(), temporaryName);
            Files.copy(worldFile, dest);

            return dest;
        }).thenAccept(file -> new BukkitRunnable() {
            @Override
            public void run() {
                mapConsumer.accept(new Arena<>(
                        new WorldCreator(temporaryName)
                                .type(WorldType.FLAT)
                                .generator(new EmptyChunkGenerator())
                                .createWorld(),
                        file,
                        settings
                ));
            }
        }.runTask(plugin));
    }
}
