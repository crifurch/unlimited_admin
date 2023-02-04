package dev.crifurch.unlimitedadmin.api.managers;

import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomCommandsManager {
    private static CustomCommandsManager INSTANCE;
    private static Plugin plugin;

    public static CustomCommandsManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomCommandsManager();
        }
        return INSTANCE;
    }

    public static void init(Plugin plugin) {
        CustomCommandsManager.plugin = plugin;
    }

    private static PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);

            command = c.newInstance(name, plugin);
        } catch (SecurityException | InvocationTargetException | IllegalArgumentException
                 | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return command;
    }

    public boolean unregisterCommand(String name) {
        final Command command = getCommandMap().getCommand(name);
        if (command != null) {
            command.unregister(getCommandMap());
            return true;
        }
        return false;
    }

    public PluginCommand registerCommand(String... aliases) {
        PluginCommand command = getCommand(aliases[0], plugin);
        final boolean unregisterCommand = unregisterCommand(aliases[0]);
        if (unregisterCommand) {
            plugin.getLogger().info("Unregistered command " + aliases[0] + " it can be from another plugin or minecraft");
        }
        command.setAliases(Arrays.asList(aliases));
        getCommandMap().register(plugin.getDescription().getName(), command);
        return command;
    }

    public PluginCommand registerCommand(ICommand command) {
        List<String> aliases = new ArrayList<>(Collections.singletonList(command.getName()));
        if (command.getAliases() != null) {
            aliases.addAll(command.getAliases());
        }
        final PluginCommand pluginCommand = registerCommand(aliases.toArray(new String[0]));
        if (pluginCommand != null) {
            plugin.getLogger().info("Success register UnlimitedAdmin command " + command.getName());
        }
        return pluginCommand;
    }

    private static CommandMap getCommandMap() {
        CommandMap commandMap = null;

        try {
            Field f = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getPluginManager());

        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return commandMap;
    }
}
