package dev.crifurch.unlimitedadmin.modules.chat.commands.say;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.api.utils.PlaceHolderUtils;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModuleConfig;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SayLaterCommand implements ICommand {
    final ChatModule chatModule;

    public SayLaterCommand(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public String getUsageText() {
        return ICommand.super.getUsageText() + " <seconds> <message>";
    }

    @Override
    public @NotNull String getName() {
        return "saylater";
    }

    @Override
    public byte getMinArgsSize() {
        return 2;
    }

    @Override
    public @Nullable List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        if (i == 0) {
            return Collections.singletonList("<seconsd>");
        }
        return ICommand.EMPTY_TAB_COMPLETIONS;
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        int seconds = (int) Double.parseDouble(args.get(0));
        if (seconds > ChatModuleConfig.SAY_MAX_DELAY.getInt()) {
            throw new NotifibleException(LangConfig.MAX_SAY_DELAY_ERROR.getText(ChatModuleConfig.SAY_MAX_DELAY.getInt() + ""));
        }
        String message = args.getMessage(1);
        final ChatMessageSender chatMessageSender = ChatMessageSender.fromSender(sender);
        String prefix = chatMessageSender.replacePlaceholders(ChatModuleConfig.SAY_FORMAT.getString());
        if (ChatModuleConfig.ALLOW_PLAYER_USE_COLOR_CODES.getBoolean()) {
            message = PlaceHolderUtils.replaceColors(message);
        }
        message = prefix.replaceAll("%message", message);
        final Collection<String> notificationNames = chatModule.getNotificationNames();
        String name = "saylater";
        int i = 0;
        while (notificationNames.contains(name)) {
            name = "saylater" + i;
            i++;
        }
        if (!chatModule.addDelayedNotification(name, message, seconds)) {
            throw new RuntimeException("Could not add notification");
        }
    }

}
