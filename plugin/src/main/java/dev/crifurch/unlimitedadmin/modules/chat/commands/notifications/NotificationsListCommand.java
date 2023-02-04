package dev.crifurch.unlimitedadmin.modules.chat.commands.notifications;

import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class NotificationsListCommand implements ICommand {
    final ChatModule chatModule;

    public NotificationsListCommand(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public @NotNull String getName() {
        return "listnotifies";
    }

    @Override
    public byte getMaxArgsSize() {
        return 0;
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        sender.sendMessage(String.join(", ", chatModule.getNotificationNames()));
    }
}
