package org.cl.pdk.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class CommandRegistry {

    public static void registerCommands(Class<?> target, JavaPlugin plugin) {
        for (Method method : target.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Command.class)) {
                continue;
            }

            Command annotation = target.getAnnotation(Command.class);
            PluginCommand command = plugin.getCommand(annotation.command());

            if (command == null) {
                Bukkit.getLogger().severe(annotation.command() + " declared in " + target.getSimpleName() + " is not registered in the plugin.yml! Skipping registration...");
                continue;
            }

            command.setExecutor((sender, cmd, s, args) -> {
                boolean validSender = false;

                for (Class<? extends CommandSender> senderType : annotation.validSenders()) {
                    if (senderType.isAssignableFrom(sender.getClass())) {
                        validSender = true;
                        break;
                    }
                }

                if (!validSender) {
                    sender.sendMessage(ChatColor.RED + "You cannot run this command as a " + sender.getClass().getSimpleName() + "!");
                    return true;
                }

                try {
                    method.invoke(plugin, sender, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    sender.sendMessage(ChatColor.RED + "An error occurred while executing your command!");
                    Bukkit.getLogger().severe(ChatColor.RED + "Failed to invoke command handler for " + cmd.getName() + ":");
                    e.printStackTrace();
                }

                return true;
            });
        }
    }
}
