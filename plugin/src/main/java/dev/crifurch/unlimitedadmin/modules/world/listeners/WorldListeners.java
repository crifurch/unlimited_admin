package dev.crifurch.unlimitedadmin.modules.world.listeners;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.modules.world.WorldsModule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListeners implements Listener {
    final UnlimitedAdmin plugin;
    final WorldsModule manager;

    public WorldListeners(UnlimitedAdmin plugin, WorldsModule manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void loadWorld(WorldLoadEvent event) {
        World world = event.getWorld();
        final String s = manager.loadWorld(world.getName());
        if (s != null) {
            plugin.getLogger().info(s);
        }
    }

    @EventHandler
    public void initWorld(WorldInitEvent event) {
        World world = event.getWorld();
        //todo get per world
        world.setKeepSpawnInMemory(false);
    }
}
