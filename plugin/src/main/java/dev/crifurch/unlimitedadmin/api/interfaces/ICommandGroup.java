package dev.crifurch.unlimitedadmin.api.interfaces;

import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandNotEnoughArgsException;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface ICommandGroup extends ICommand {
    @Override
    default String getUsageText() {
        return ICommand.super.getUsageText() + "<command>";
    }

    @NotNull
    Collection<ICommand> getCommands();

    @Override
    default byte getMinArgsSize() {
        return 1;
    }

    @Override
    @Nullable
    default List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        if (i == 0) {
            return getCommands().stream().map(ICommand::getName).collect(Collectors.toList());
        }
        if (i >= 1) {
            final ICommand command = getCommands().stream().filter(c -> c.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
            if (command != null) {
                List<String> collect = Arrays.stream(args).collect(Collectors.toList());
                return command.getTabCompletion(sender, collect.subList(1, collect.size()).toArray(new String[0]), i - 1);
            }
        }
        return ICommand.super.getTabCompletion(sender, args, i);
    }


    @Override
    default void onCommand(CommandSender sender, final CommandArguments args) throws NotifibleException {
        final Collection<ICommand> commands = getCommands();
        final ICommand command = commands.stream().filter(c -> c.getName().equalsIgnoreCase(args.get(0))).findFirst().orElse(null);
        if (command == null) {
            throw new CommandNotEnoughArgsException(getUsageText());
        }
        CommandArguments newArgs = args.cutFrom(1);
        try {
            command.assertArgsSize(newArgs);
        } catch (CommandNotEnoughArgsException e) {
            if (!shouldOverrideUsageText()) {
                throw e;
            }
            throw new CommandNotEnoughArgsException("/" + getName()
                    + " " + command.getUsageText().substring(1));
        }
        command.onCommand(sender, newArgs);
    }

    default boolean shouldOverrideUsageText() {
        return true;
    }
}
