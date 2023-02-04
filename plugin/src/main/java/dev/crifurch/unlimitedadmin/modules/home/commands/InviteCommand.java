package dev.crifurch.unlimitedadmin.modules.home.commands;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.GlobalConstants;
import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandErrorException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandNotEnoughArgsException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandOnlyForUserException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.home.HomeModule;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InviteCommand implements ICommand {
    final HomeModule module;

    public InviteCommand(HomeModule module) {
        this.module = module;
    }

    @Override
    public String getUsageText() {
        return getName() + " <player_name> [home name]";
    }

    @Override
    public byte getMaxArgsSize() {
        return 1;
    }

    @Override
    public byte getMinArgsSize() {
        return 1;
    }

    @Override
    public @NotNull String getName() {
        return "homeinvite";
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws CommandOnlyForUserException,
            CommandNotEnoughArgsException,
            CommandErrorException {
        assertSenderIsPlayer(sender);
        String name = GlobalConstants.defaultEntryName;
        UUID playerToAdd = args.getPlayerUUID(0);
        if (playerToAdd == null) {
            throw new CommandErrorException(LangConfig.NO_SUCH_PLAYER.getText());
        }
        if (args.count() > 1) {
            name = args.get(1);
        }
        if (module.addParticipant(playerToAdd, ((Player) sender).getUniqueId(), name)) {
            sender.sendMessage("Player successful invited");
            String finalName = name;
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (playerToAdd.equals(player.getUniqueId())) {
                    player.sendMessage("You invited to " +
                            UnlimitedAdmin.getInstance().getPlayersMapModule().getPlayerName(((Player) sender).getUniqueId()) +
                            "(" + finalName + ") home");
                }
                    }
            );
        } else {
            sender.sendMessage("Player not invited");
        }
    }
}
