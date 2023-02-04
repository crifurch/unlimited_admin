package dev.crifurch.unlimitedadmin.modules.chat.implementations.channels;

import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.ILoggedChat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class NotificationsChatChannel implements ILoggedChat {

    public static final String CHANNEL_PREFIX = "!notification!";

    final ChatModule chatModule;

    public NotificationsChatChannel(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public @NotNull ChatModule getModule() {
        return chatModule;
    }

    @Override
    public @NotNull String getName() {
        return "notifications";
    }

    @Override
    public String getChannelPrefix() {
        return CHANNEL_PREFIX;
    }

    @Override
    public @Nullable String broadcast(@NotNull ChatMessageSender sender, @NotNull String message, @Nullable Consumer<String> sendMessageConsumer) {
        ILoggedChat.super.broadcast(sender, message, sendMessageConsumer);
        return null;
    }
}
