package dev.crifurch.unlimitedadmin.modules.chat.commands.notifications;

import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DeleteNotificationCommand implements ICommand {
    final ChatModule chatModule;

    public DeleteNotificationCommand(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public @NotNull String getName() {
        return "delnotify";
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
    public List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        return new ArrayList<>(chatModule.getNotificationNames());
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        chatModule.deleteNotification(args.get(0));
    }
}
