package dev.crifurch.unlimitedadmin.modules.world.commands;

import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CollectionUtils;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.world.WorldsModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand implements ICommand {
    private final WorldsModule manager;

    public ListCommand(WorldsModule manager) {
        this.manager = manager;
    }

    @Override
    public @NotNull String getName() {
        return "list";
    }

    @Override
    public @Nullable List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        if (i == 0) {
            return Arrays.asList("custom", "standard", "all");
        }
        return ICommand.super.getTabCompletion(sender, args, i);
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) {
        int wide = 1;
        if (args.isNotEmpty()) {
            if ("custom".equalsIgnoreCase(args.get(0))) {
                wide = -1;
            } else if ("standard".equalsIgnoreCase(args.get(0))) {
                wide = 0;
            }
        }
        List<String> worlds = new ArrayList<>();
        if (wide >= 0) {
            worlds.addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
        }
        if (wide != 0) {
            worlds.addAll(manager.getWorlds());
        }
        worlds = CollectionUtils.removeDuplicates(worlds);
        Collections.sort(worlds);

        StringBuilder result = new StringBuilder();
        worlds.forEach(e -> {
            result.append(e);
            result.append("\n");
        });
        sender.sendMessage(result.toString());
    }


}
