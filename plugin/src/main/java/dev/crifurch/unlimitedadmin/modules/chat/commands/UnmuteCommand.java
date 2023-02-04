package dev.crifurch.unlimitedadmin.modules.chat.commands;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.data.Mute;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnmuteCommand implements ICommand {
    final ChatModule chatModule;

    public UnmuteCommand(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public @NotNull String getName() {
        return "unmute";
    }

    @Override
    public String getUsageText() {
        return ICommand.super.getUsageText() + " <player> [channel]";
    }

    @Override
    public byte getMaxArgsSize() {
        return 2;
    }

    @Override
    public byte getMinArgsSize() {
        return 1;
    }

    @Override
    public @Nullable List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        if (i == 1) {
            final List<String> notificationNames = new ArrayList<>(chatModule.getChannelsList());
            notificationNames.add(0, "*");
            return notificationNames;
        }
        return ICommand.super.getTabCompletion(sender, args, i);
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        String playerName = args.get(0);
        final UUID playerUUID = UnlimitedAdmin.getInstance().getPlayersMapModule().getPlayerUUID(playerName);
        if (playerUUID == null) {
            throw new NotifibleException(LangConfig.NO_SUCH_PLAYER.getText(playerName));
        }
        String channel = "*";
        if (args.count() == 2) {
            channel = args.get(1);
        }
        if (channel.equals("all")) {
            channel = "*";
        }
        if (!channel.equals("*") && !chatModule.getChannelsList().contains(channel)) {
            throw new NotifibleException(LangConfig.NO_SUCH_CHANNEL.getText(channel));
        }
        Mute mute = chatModule.getMute(playerUUID);
        if (mute == null) {
            return;
        }
        mute.addExceptChannel(channel);
        chatModule.setMute(mute);

    }
}
