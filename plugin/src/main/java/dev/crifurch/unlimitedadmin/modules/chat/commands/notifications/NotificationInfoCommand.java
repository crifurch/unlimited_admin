package dev.crifurch.unlimitedadmin.modules.chat.commands.notifications;

import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class NotificationInfoCommand implements ICommand {
    final ChatModule chatModule;

    public NotificationInfoCommand(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public @NotNull String getName() {
        return "infonotify";
    }

    @Override
    public String getUsageText() {
        return ICommand.super.getUsageText() + " <name>";
    }

    @Override
    public byte getMinArgsSize() {
        return 1;
    }

    @Override
    public byte getMaxArgsSize() {
        return 1;
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        sender.sendMessage(chatModule.getNotificationInfo(args.get(0)));
    }
}
