package dev.crifurch.unlimitedadmin.modules.world.commands;

import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandOnlyForUserException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.world.WorldsModule;
import dev.crifurch.unlimitedadmin.modules.world.gui.WorldManagerGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class GuiCommand implements ICommand {

    private final WorldsModule manager;

    public GuiCommand(WorldsModule manager) {
        this.manager = manager;
    }

    @Override
    public @NotNull String getName() {
        return "gui";
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws CommandOnlyForUserException {
        if (sender.isOp() || sender.hasPermission("unlimitedadmin.worlds.gui")) {
            if (!(sender instanceof HumanEntity)) {
                throw new CommandOnlyForUserException();
            } else {
                new WorldManagerGui(manager).openInventory((HumanEntity) sender);
            }
        }
    }
}
