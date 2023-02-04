package dev.crifurch.unlimitedadmin.modules.spawn.commands;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.GlobalConstants;
import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandErrorException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.permissions.AdditionalPermissions;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.integrations.permissions.PermissionStatus;
import dev.crifurch.unlimitedadmin.integrations.permissions.PermissionsProvider;
import dev.crifurch.unlimitedadmin.modules.spawn.SpawnModule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SpawnCommand implements ICommand {
    final SpawnModule module;

    public SpawnCommand(SpawnModule module) {
        this.module = module;
    }

    @Override
    public String getUsageText() {
        return ICommand.super.getUsageText() + "[spawn] [player]";
    }

    @Override
    public @NotNull String getName() {
        return "spawn";
    }

    @Override
    public byte getMaxArgsSize() {
        return 2;
    }

    @Override
    public @Nullable List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        final PermissionsProvider instance = PermissionsProvider.getInstance();
        if (i == 0) {
            final List<String> spawns = new ArrayList<>(module.getSpawns());
            spawns.removeIf(s -> !instance.havePermission(sender, getCommandPermission() + "." + s).isPermittedOrUnset());
            return spawns;
        }
        if (i == 1 && instance.havePermission(sender, AdditionalPermissions.OTHER.getPermissionForCommand(this)) == PermissionStatus.PERMISSION_TRUE) {
            return null;
        }
        return Collections.emptyList();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        String spawnName = GlobalConstants.defaultEntryName;
        UUID playerUuid = null;
        if (args.isNotEmpty()) {
            spawnName = args.get(0);
        }
        final PermissionsProvider instance = PermissionsProvider.getInstance();
        if (args.count() > 1) {
            assertOtherPermission(sender);
            UUID player = UnlimitedAdmin.getInstance().getPlayersMapModule().getPlayerUUID(args.get(1));
            if (player == null) {
                throw new CommandErrorException(LangConfig.NO_SUCH_PLAYER.getText());
            }
            playerUuid = player;
        }

        if (playerUuid == null) {
            assertSenderIsPlayer(sender);
            playerUuid = ((Player) sender).getUniqueId();
        }


        final boolean permittedOrUnset = instance.havePermission(sender, getCommandPermission() + "." + spawnName).isPermittedOrUnset();
        if (permittedOrUnset && !module.teleportPlayerToSpawn(playerUuid, spawnName)) {
            throw new CommandErrorException(LangConfig.NO_SUCH_SPAWN.getText(spawnName));
        }
    }
}
