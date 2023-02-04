package dev.crifurch.unlimitedadmin.modules.spawn.commands;

import dev.crifurch.unlimitedadmin.api.GlobalConstants;
import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandPermissionException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.spawn.SpawnModule;
import dev.crifurch.unlimitedadmin.modules.spawn.SpawnModuleConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class SetSpawnCommand implements ICommand {
    final SpawnModule module;

    public SetSpawnCommand(SpawnModule module) {
        this.module = module;
    }

    @Override
    public @NotNull String getName() {
        return "setspawn";
    }

    @Override
    public byte getMaxArgsSize() {
        return 1;
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        assertSenderIsPlayer(sender);
        String name = GlobalConstants.defaultEntryName;
        if (args.isNotEmpty()) {
            name = args.get(0);
        }
        int allowedSpawns = SpawnModuleConfig.SPAWN_LIMIT.getInt();
        if (allowedSpawns < 0) {
            allowedSpawns = Integer.MAX_VALUE;
        }
        if (allowedSpawns == 0) {
            throw new CommandPermissionException();
        }
        final Set<String> spawns = module.getSpawns();
        if (!spawns.contains(name)) {
            if (spawns.size() >= allowedSpawns) {
                throw new CommandPermissionException();
            }
        }
        module.setSpawn(name, ((Player) sender).getLocation());
        sender.sendMessage(LangConfig.SPAWN_CREATED.getText(name));
    }
}
