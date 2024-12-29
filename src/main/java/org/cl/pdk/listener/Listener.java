package org.cl.pdk.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public interface Listener extends org.bukkit.event.Listener {

    default void register(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    default void unregister()  {
        HandlerList.unregisterAll(this);
    }
}
