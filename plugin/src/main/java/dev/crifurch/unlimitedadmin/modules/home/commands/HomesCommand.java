package dev.crifurch.unlimitedadmin.modules.home.commands;

import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandNotEnoughArgsException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandOnlyForUserException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.home.HomeModule;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HomesCommand implements ICommand {
    protected final HomeModule module;

    public HomesCommand(HomeModule module) {
        this.module = module;
    }

    @Override
    public @NotNull String getName() {
        return "homes";
    }

    @Override
    public byte getMaxArgsSize() {
        return 0;
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws CommandNotEnoughArgsException, CommandOnlyForUserException {
    }
}
