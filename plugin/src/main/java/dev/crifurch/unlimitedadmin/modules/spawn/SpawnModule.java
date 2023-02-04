package dev.crifurch.unlimitedadmin.modules.spawn;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModuleDefinition;
import dev.crifurch.unlimitedadmin.api.modules.RawModule;
import dev.crifurch.unlimitedadmin.api.providers.PluginFileProvider;
import dev.crifurch.unlimitedadmin.api.utils.PlayerUtils;
import dev.crifurch.unlimitedadmin.modules.spawn.commands.DelSpawnCommand;
import dev.crifurch.unlimitedadmin.modules.spawn.commands.ListSpawnCommand;
import dev.crifurch.unlimitedadmin.modules.spawn.commands.SetSpawnCommand;
import dev.crifurch.unlimitedadmin.modules.spawn.commands.SpawnCommand;
import dev.crifurch.unlimitedadmin.modules.spawn.listeners.SpawnDeathListener;
import dev.crifurch.unlimitedadmin.modules.spawn.listeners.SpawnLoginListener;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SpawnModule extends RawModule {

    private static YamlConfiguration cfg;
    private static File f;
    final UnlimitedAdmin plugin;
    private final List<ICommand> commands = new ArrayList<>();
    private final List<Listener> listeners = new ArrayList<>();

    public SpawnModule(UnlimitedAdmin plugin) {
        this.plugin = plugin;
        f = PluginFileProvider.UnlimitedAdmin.getModuleConfigFile(getDefinition(), "spawns");


    }

    @Override
    public @NotNull IModuleDefinition getDefinition() {
        return ModulesManager.SPAWN;
    }

    @Override
    public void onEnable() {
        cfg = YamlConfiguration.loadConfiguration(f);
        SpawnModuleConfig.load();
        commands.add(new SetSpawnCommand(this));
        commands.add(new SpawnCommand(this));
        commands.add(new DelSpawnCommand(this));
        commands.add(new ListSpawnCommand(this));

        if (SpawnModuleConfig.PREFER_TELEPORT_ON_DEATH.getBoolean()) {
            listeners.add(new SpawnDeathListener(this));
        }
        if (SpawnModuleConfig.PREFERRED_FIRST_SPAWN.getBoolean()) {
            listeners.add(new SpawnLoginListener(this));
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public @NotNull List<ICommand> getCommands() {
        return commands;
    }

    @Override
    public List<Listener> getListeners() {
        return listeners;
    }

    public void setSpawn(@NotNull String name, @NotNull Location position) {
        if (!cfg.contains(name)) {
            cfg.createSection(name);
        }
        cfg.set(name, position);
        try {
            cfg.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteSpawn(@NotNull String name) {
        if (cfg.contains(name)) {
            cfg.set(name, null);
        }
        try {
            cfg.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getSpawns() {
        return cfg.getValues(false).keySet();
    }

    public Location getSpawnLocation(String name) {
        return (Location) cfg.get(name);
    }

    public boolean teleportPlayerToSpawn(@NotNull UUID player, @NotNull String spawnName) {
        if (cfg.contains(spawnName)) {
            PlayerUtils.setLocation(player, getSpawnLocation(spawnName));
            return true;
        }
        return false;
    }

}
