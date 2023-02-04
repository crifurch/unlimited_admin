package dev.crifurch.unlimitedadmin.modules.chat.listeners;

import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatMessageListener implements Listener {
    final ChatModule module;

    public ChatMessageListener(ChatModule module) {
        this.module = module;
    }

    @EventHandler(ignoreCancelled = true, priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onChatMessage(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        module.broadcastMessage(ChatMessageSender.fromSender((Entity) event.getPlayer()), event.getMessage());
    }
}
