package dev.crifurch.unlimitedadmin.modules.spawn.listeners;

import dev.crifurch.unlimitedadmin.api.GlobalConstants;
import dev.crifurch.unlimitedadmin.api.utils.PlayerUtils;
import dev.crifurch.unlimitedadmin.modules.spawn.SpawnModule;
import dev.crifurch.unlimitedadmin.modules.spawn.events.RespawnTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnDeathListener implements Listener {
    final SpawnModule module;

    public SpawnDeathListener(SpawnModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerRespawnEvent event) {
        final Location spawnLocation = module.getSpawnLocation(GlobalConstants.defaultEntryName);
        if (spawnLocation != null) {
            final RespawnTeleportEvent event1 = new RespawnTeleportEvent(event.getPlayer());
            Bukkit.getPluginManager().callEvent(event1);
            if(!event1.isCancelled()) {
                event.setRespawnLocation(spawnLocation);
                PlayerUtils.setLocationDelayed(event.getPlayer(), spawnLocation, 600);
            }
        }
    }
}
