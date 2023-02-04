package dev.crifurch.unlimitedadmin.modules.home.listeners;

import dev.crifurch.unlimitedadmin.api.utils.PlayerUtils;
import dev.crifurch.unlimitedadmin.modules.home.HomeModule;
import dev.crifurch.unlimitedadmin.modules.home.data.Home;
import dev.crifurch.unlimitedadmin.modules.spawn.events.RespawnTeleportEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class HomeDeathListener implements Listener {
    final HomeModule module;

    public HomeDeathListener(HomeModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerRespawnEvent event) {
        final List<Home> ownerHomes = module.getOwnerHomes(event.getPlayer().getUniqueId());
        if (!ownerHomes.isEmpty()) {
            final Home home = ownerHomes.get(0);
            final Location location = home.getLocation();
            if (location != null) {
                event.setRespawnLocation(location);
                PlayerUtils.setLocationDelayed(event.getPlayer(), location, 600);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(RespawnTeleportEvent event) {
        final List<Home> ownerHomes = module.getOwnerHomes(event.getPlayer().getUniqueId());
        if (!ownerHomes.isEmpty()) {
            event.setCancelled(true);
        }
    }
}
