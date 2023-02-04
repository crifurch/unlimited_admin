package dev.crifurch.unlimitedadmin.modules.spawn.listeners;

import dev.crifurch.unlimitedadmin.api.GlobalConstants;
import dev.crifurch.unlimitedadmin.api.utils.PlayerUtils;
import dev.crifurch.unlimitedadmin.modules.playersmap.data.PlayerFirstJoinEvent;
import dev.crifurch.unlimitedadmin.modules.spawn.SpawnModule;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SpawnLoginListener implements Listener {
    final SpawnModule module;

    public SpawnLoginListener(SpawnModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerFirstJoinEvent event) {
        final Location spawnLocation = module.getSpawnLocation(GlobalConstants.defaultEntryName);
        if (spawnLocation != null) {
            PlayerUtils.setLocationDelayed(event.getPlayer(), spawnLocation, 400);
        }
    }
}
