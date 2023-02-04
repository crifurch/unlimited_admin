package dev.crifurch.unlimitedadmin.modules.player_status.commands;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandErrorException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandOnlyForUserException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandOtherPermissionsException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.api.utils.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HealCommand implements ICommand {
    @Override
    public @NotNull String getName() {
        return "heal";
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws CommandOtherPermissionsException, CommandErrorException, CommandOnlyForUserException {
        UUID targetPlayer = null;
        if (args.isNotEmpty()) {
            assertOtherPermission(sender);
            targetPlayer = UnlimitedAdmin.getInstance().getPlayersMapModule().getPlayerUUID(args.get(0));
            if (targetPlayer == null) {
                throw new CommandErrorException(LangConfig.NO_SUCH_PLAYER.getText());
            }
        }
        if (targetPlayer == null) {
            assertSenderIsPlayer(sender);
            targetPlayer = ((Player) sender).getUniqueId();
        }

        if (!PlayerUtils.setHealth(targetPlayer, PlayerUtils.getMaxHealth(targetPlayer, true))) {
            throw new CommandErrorException();
        }
    }
}
