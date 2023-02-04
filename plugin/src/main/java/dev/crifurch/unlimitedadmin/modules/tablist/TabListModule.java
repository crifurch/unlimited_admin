package dev.crifurch.unlimitedadmin.modules.tablist;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModuleDefinition;
import dev.crifurch.unlimitedadmin.api.modules.AdminModule;
import dev.crifurch.unlimitedadmin.api.utils.PlaceHolderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

// todo: support icons
public class TabListModule extends AdminModule implements Listener {
    public TabListModule() {
    }

    @Override
    public @NotNull IModuleDefinition getDefinition() {
        return ModulesManager.TABLIST;
    }


    @Override
    public void onEnable() {
        TabListConfig.load();
        Bukkit.getOnlinePlayers().forEach(this::updateTabList);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public Collection<Listener> getListeners() {
        return Collections.singletonList(this);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        updateTabList(player);
    }

    private void updateTabList(Player player) {
        final String formatedText = PlaceHolderUtils.replacePlayerPlaceholders(
                TabListConfig.TABLIST_FORMAT.getString());
        player.setPlayerListName(formatedText);
    }

}
