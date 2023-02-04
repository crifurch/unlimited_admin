package dev.crifurch.unlimitedadmin.modules.teleporting.commands;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandErrorException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.api.utils.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TpCommand implements ICommand {
    private final UnlimitedAdmin plugin;


    public TpCommand(UnlimitedAdmin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsageText() {
        return ICommand.super.getUsageText() + " <player> [player_to]";
    }

    @Override
    public byte getMinArgsSize() {
        return 1;
    }

    @Override
    public byte getMaxArgsSize() {
        return 2;
    }

    @Override
    public @NotNull String getName() {
        return "tp";
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        UUID targetPlayer = null;
        UUID tpTo;
        int indexTpTo = 0;
        if (args.count() > 1) {
            assertOtherPermission(sender);
            indexTpTo = 1;
            targetPlayer = plugin.getPlayersMapModule().getPlayerUUID(args.get(0));
            if (targetPlayer == null) {
                throw new CommandErrorException(LangConfig.NO_SUCH_PLAYER.getText(args.get(0)));
            }
        }
        tpTo = plugin.getPlayersMapModule().getPlayerUUID(args.get(indexTpTo));
        if (tpTo == null) {
            throw new CommandErrorException(LangConfig.NO_SUCH_PLAYER.getText(args.get(indexTpTo)));
        }
        if (targetPlayer == null) {
            assertSenderIsPlayer(sender);
            targetPlayer = ((Player) sender).getUniqueId();
        }

        if (!PlayerUtils.setLocation(targetPlayer, PlayerUtils.getLocation(tpTo))) {
            throw new CommandErrorException();
        }
    }
}
