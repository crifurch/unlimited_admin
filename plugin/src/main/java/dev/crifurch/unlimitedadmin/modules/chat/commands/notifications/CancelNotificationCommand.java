package dev.crifurch.unlimitedadmin.modules.chat.commands.notifications;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CancelNotificationCommand implements ICommand {
    private final ChatModule chatModule;

    public CancelNotificationCommand(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public @NotNull String getName() {
        return "cancelnotify";
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
        final boolean b = chatModule.cancelNotification(args.get(0));
        if (!b) throw new NotifibleException(LangConfig.NO_SUCH_NOTIFICATION.getText(args.get(0)));
    }
}
