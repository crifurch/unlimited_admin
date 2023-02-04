package dev.crifurch.unlimitedadmin.modules.chat.commands.privatemessages;

import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.channels.PrivateMessageChatChannel;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class MsgCommand implements ICommand {
    private final ChatModule chatModule;

    public MsgCommand(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public @NotNull String getName() {
        return "msg";
    }

    @Override
    public byte getMinArgsSize() {
        return 2;
    }

    @Override
    public @Nullable List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        return i < 1 ? null : Collections.emptyList();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        final String message = args.getMessage(1);
        chatModule.broadcastMessage(ChatMessageSender.fromSender(sender), PrivateMessageChatChannel.prepareMessage(message, args.get(0)));
    }


}
