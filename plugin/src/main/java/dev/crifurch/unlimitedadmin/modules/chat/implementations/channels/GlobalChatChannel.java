package dev.crifurch.unlimitedadmin.modules.chat.implementations.channels;

import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModuleConfig;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.ILoggedChat;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.ISpiedChat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class GlobalChatChannel implements ILoggedChat, ISpiedChat {

    final ChatModule chatModule;

    public GlobalChatChannel(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public @NotNull ChatModule getModule() {
        return chatModule;
    }

    @Override
    public @NotNull String getName() {
        return "global";
    }

    @Override
    public String getChannelPrefix() {
        return ChatModuleConfig.GLOBAL_CHAT_PREFIX.getString();
    }


    @Override
    public @NotNull List<Player> getTargetPlayers(@NotNull ChatMessageSender sender, @Nullable List<String> filteredNicknames) {
        List<Player> targetPlayers = ILoggedChat.super.getTargetPlayers(sender, filteredNicknames);
        if (!ChatModuleConfig.IS_GLOBAL_CHAT_WORLD_WIDE.getBoolean()) {
            targetPlayers = targetPlayers.stream().filter(targetPlayer -> !sender.sameWorld(targetPlayer.getWorld()))
                    .collect(Collectors.toList());
        }
        return targetPlayers;
    }

    @Override
    public @NotNull String getFormat() {
        return ChatModuleConfig.GLOBAL_CHAT_FORMAT.getString();
    }
}

