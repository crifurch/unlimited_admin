package dev.crifurch.unlimitedadmin.modules.chat.implementations.utilchannels;

import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.IChatChanel;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.ISubhandlerChannel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class DublicateChildWrapper implements IChatChanel {
    private final IChatChanel child;
    private final ISubhandlerChannel dublicator;

    final ChatModule chatModule;

    public DublicateChildWrapper(IChatChanel child, @Nullable ISubhandlerChannel dublicator, ChatModule chatModule) {
        this.child = child;
        this.dublicator = dublicator;
        this.chatModule = chatModule;
    }

    @Override
    public @NotNull ChatModule getModule() {
        return chatModule;
    }

    public IChatChanel getChild() {
        return child;
    }

    @Override
    public @NotNull String getName() {
        return child.getName();
    }

    @Override
    public String getChannelPrefix() {
        return child.getChannelPrefix();
    }

    @Override
    public @Nullable String broadcast(@NotNull ChatMessageSender sender, @NotNull String message, @Nullable Consumer<String> sendMessageConsumer) {
        return child.broadcast(sender, message, s -> {
            if (sendMessageConsumer != null) {
                sendMessageConsumer.accept(s);
            }
            if (dublicator != null) {
                dublicator.onSubhandlerMessage(sender, child, s);
            }
        });
    }

    @Override
    public @NotNull String getFormat() {
        return child.getFormat();
    }

    @Override
    public @NotNull List<Player> getTargetPlayers(@NotNull ChatMessageSender sender, @Nullable List<String> filteredNicknames) {
        return child.getTargetPlayers(sender, filteredNicknames);
    }

    @Override
    public String formatMessage(@NotNull ChatMessageSender sender, @NotNull String message) {
        return child.formatMessage(sender, message);
    }

}
