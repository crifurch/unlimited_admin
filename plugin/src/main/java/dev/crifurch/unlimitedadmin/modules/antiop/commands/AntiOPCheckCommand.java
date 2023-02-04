package dev.crifurch.unlimitedadmin.modules.antiop.commands;

import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.antiop.AntiOPModule;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AntiOPCheckCommand implements ICommand {
    final AntiOPModule module;

    public AntiOPCheckCommand(AntiOPModule module) {
        this.module = module;
    }

    @Override
    public byte getMaxArgsSize() {
        return 0;
    }

    @Override
    public @NotNull String getName() {
        return "check";
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        module.checkOps(Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }
}
