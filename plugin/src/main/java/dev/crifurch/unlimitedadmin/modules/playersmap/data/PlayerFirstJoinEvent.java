package dev.crifurch.unlimitedadmin.modules.playersmap.data;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerFirstJoinEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public PlayerFirstJoinEvent(@NotNull Player who) {
        super(who);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
