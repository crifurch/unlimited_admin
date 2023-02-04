package dev.crifurch.unlimitedadmin.modules.chat.commands.say;

import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.api.utils.PlaceHolderUtils;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModuleConfig;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.channels.NotificationsChatChannel;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SayCommand implements ICommand {
    final ChatModule chatModule;

    public SayCommand(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public String getUsageText() {
        return ICommand.super.getUsageText() + " <message>";
    }

    @Override
    public @NotNull String getName() {
        return "say";
    }

    @Override
    public byte getMinArgsSize() {
        return 1;
    }

    @Override
    public @Nullable List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        return ICommand.EMPTY_TAB_COMPLETIONS;
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        final ChatMessageSender chatMessageSender = ChatMessageSender.fromSender(sender);
        String message = String.join(" ", args);
        String prefix = chatMessageSender.replacePlaceholders(ChatModuleConfig.SAY_FORMAT.getString());
        if (ChatModuleConfig.ALLOW_PLAYER_USE_COLOR_CODES.getBoolean()) {
            message = PlaceHolderUtils.replaceColors(message);
        }
        message = prefix.replaceAll("%message", message);
        chatModule.broadcastMessage(chatMessageSender, NotificationsChatChannel.CHANNEL_PREFIX + message);
    }
}
