package dev.crifurch.unlimitedadmin.modules.chat.implementations.channels;

import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModuleConfig;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.PlayerMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.ILoggedChat;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.ISpiedChat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class LocalChatChannel implements ILoggedChat, ISpiedChat {

    final ChatModule chatModule;

    public LocalChatChannel(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public @NotNull ChatModule getModule() {
        return chatModule;
    }

    @Override
    public @NotNull String getName() {
        return "local";
    }

    @Override
    public @Nullable String getChannelPrefix() {
        return ChatModuleConfig.LOCAL_CHAT_PREFIX.getString();
    }

    @Override
    public @NotNull List<Player> getTargetPlayers(@NotNull ChatMessageSender sender, @Nullable List<String> filteredNicknames) {
        List<Player> targetPlayers = ILoggedChat.super.getTargetPlayers(sender, filteredNicknames);
        if (sender instanceof PlayerMessageSender) {
            final int squaredRadius = ChatModuleConfig.LOCAL_CHAT_RADIUS.getInt() * ChatModuleConfig.LOCAL_CHAT_RADIUS.getInt();
            targetPlayers = targetPlayers.stream().filter(targetPlayer -> {
                if (sender.sameAs(targetPlayer)) {
                    return true;
                }
                if (!sender.sameWorld(targetPlayer.getWorld())) {
                    return false;
                }
                return sender.inDistance(targetPlayer.getLocation(), squaredRadius);
            }).collect(Collectors.toList());
        }
        return targetPlayers;
    }


    @Override
    public @NotNull String getFormat() {
        return ChatModuleConfig.LOCAL_CHAT_FORMAT.getString();
    }
}
