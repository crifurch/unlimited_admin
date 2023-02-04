package dev.crifurch.unlimitedadmin.modules.antiop.commands;

import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.antiop.AntiOPConfig;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AntiOPAddCommand implements ICommand {
    @Override
    public @NotNull String getName() {
        return "add";
    }

    @Override
    public String getUsageText() {
        return "/add <uuid|nickname>";
    }

    @Override
    public byte getMinArgsSize() {
        return 1;
    }

    @Override
    public byte getMaxArgsSize() {
        return 1;
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        final String name = args.get(0);
        final List<String> ops = new ArrayList<>(AntiOPConfig.OP_LIST.getStringList());
        if (ops.contains(name)) {
            return;
        }
        ops.add(name);
        AntiOPConfig.OP_LIST.set(ops, true);
    }

}
