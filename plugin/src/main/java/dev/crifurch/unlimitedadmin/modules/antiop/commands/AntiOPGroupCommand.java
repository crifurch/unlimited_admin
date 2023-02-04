package dev.crifurch.unlimitedadmin.modules.antiop.commands;

import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommandGroup;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.antiop.AntiOPModule;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AntiOPGroupCommand implements ICommandGroup {
    final AntiOPModule module;
    final List<ICommand> commands;

    public AntiOPGroupCommand(AntiOPModule module) {
        this.module = module;
        this.commands = Arrays.asList(
                new AntiOPAddCommand(),
                new AntiOPRemoveCommand(module),
                new AntiOPCheckCommand(module)
        );
    }

    @Override
    public @NotNull String getName() {
        return "antiop";
    }

    @Override
    public @NotNull Collection<ICommand> getCommands() {
        return commands;
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        assertSenderIsPlayer(sender);
        ICommandGroup.super.onCommand(sender, args);
    }
}
