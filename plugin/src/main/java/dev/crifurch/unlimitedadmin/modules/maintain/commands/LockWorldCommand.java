package dev.crifurch.unlimitedadmin.modules.maintain.commands;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandErrorException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandNotEnoughArgsException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandOnlyForUserException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.maintain.MaintainModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LockWorldCommand implements ICommand {
    final MaintainModule module;

    public LockWorldCommand(MaintainModule module) {
        this.module = module;
    }

    @Override
    public String getUsageText() {
        return ICommand.super.getUsageText() + " <value>\n" +
                ICommand.super.getUsageText() + " <world> <value>";
    }

    @Override
    public @NotNull String getName() {
        return "lockworld";
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
    public @Nullable List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        if (i == 0) {
            return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
        }
        return Arrays.asList("1", "0", "true", "false");
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws CommandNotEnoughArgsException, CommandOnlyForUserException, CommandErrorException {
        World world;
        boolean lock;
        if (args.count() == getMinArgsSize()) {
            assertSenderIsPlayer(sender);
            world = ((Player) sender).getWorld();
            lock = Boolean.TRUE.equals(args.get(0, Boolean.class));
        } else {
            lock = Boolean.TRUE.equals(args.get(1, Boolean.class));
            world = args.get(0, World.class);
        }
        if (world == null) {
            throw new CommandErrorException(LangConfig.NO_SUCH_WORLD.getText());
        }
        module.setLockedWorld(world, lock);
        if (lock) {
            sender.sendMessage(LangConfig.WORLD_IS_LOCKED.getText(world.getName()));
        } else {
            sender.sendMessage(LangConfig.WORLD_IS_UNLOCKED.getText(world.getName()));
        }
    }
}
