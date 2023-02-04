package dev.crifurch.unlimitedadmin.modules.spawn.commands;

import dev.crifurch.unlimitedadmin.api.GlobalConstants;
import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandErrorException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.spawn.SpawnModule;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DelSpawnCommand implements ICommand {
    final SpawnModule module;

    public DelSpawnCommand(SpawnModule module) {
        this.module = module;
    }

    @Override
    public @NotNull String getName() {
        return "delspawn";
    }

    @Override
    public byte getMaxArgsSize() {
        return 1;
    }

    @Override
    public @Nullable List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        return new ArrayList<>(module.getSpawns());
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws CommandErrorException {
        String name = GlobalConstants.defaultEntryName;
        if (args.isNotEmpty()) {
            name = args.get(0);
        }
        final Set<String> spawns = module.getSpawns();
        if (!spawns.contains(name)) {
            throw new CommandErrorException(LangConfig.NO_SUCH_SPAWN.getText(name));
        }
        module.deleteSpawn(name);
        sender.sendMessage(LangConfig.SPAWN_DELETED.getText(name));
    }
}
