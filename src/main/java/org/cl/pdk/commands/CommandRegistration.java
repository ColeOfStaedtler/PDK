package org.cl.pdk.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class CommandRegistration {

    public static void unregisterCommand(String command, JavaPlugin plugin) {
        plugin.getCommand(command).setExecutor(null);
    }

    public static void registerCommands(Object instance, JavaPlugin plugin) {
        Class<?> target = instance.getClass();
        for (Method method : target.getMethods()) {
            Command annotation = method.getAnnotation(Command.class);

            if (annotation == null) {
                continue;
            }

            String rawCommand = annotation.command();
            PluginCommand command = plugin.getCommand(rawCommand);

            if (command == null) {
                Bukkit.getLogger().severe(rawCommand + " declared in " + target.getSimpleName() + " is not registered in the plugin.yml! Skipping registration...");
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
                    method.invoke(instance, sender, args);
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
