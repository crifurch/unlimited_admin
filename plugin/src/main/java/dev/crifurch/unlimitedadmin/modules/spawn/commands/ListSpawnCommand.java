package dev.crifurch.unlimitedadmin.modules.spawn.commands;

import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CollectionUtils;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.spawn.SpawnModule;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;

public class ListSpawnCommand implements ICommand {
    final SpawnModule module;

    public ListSpawnCommand(SpawnModule module) {
        this.module = module;
    }

    @Override
    public byte getMaxArgsSize() {
        return 0;
    }

    @Override
    public @NotNull String getName() {
        return "spawns";
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) {
        final Set<String> spawns = module.getSpawns();
        final String join = CollectionUtils.join(Arrays.asList(spawns.toArray()), "\n");
        sender.sendMessage(join);
    }
}

