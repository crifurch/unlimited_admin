package dev.crifurch.unlimitedadmin.modules.teleporting;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModuleDefinition;
import dev.crifurch.unlimitedadmin.api.modules.RawModule;
import dev.crifurch.unlimitedadmin.modules.teleporting.commands.TpCommand;
import dev.crifurch.unlimitedadmin.modules.teleporting.commands.TpPosCommand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TeleportingModule extends RawModule {

    private final ArrayList<ICommand> commands = new ArrayList<>();
    final UnlimitedAdmin plugin;

    public TeleportingModule(UnlimitedAdmin plugin) {
        this.plugin = plugin;

    }

    @Override
    public @NotNull IModuleDefinition getDefinition() {
        return ModulesManager.TELEPORT;
    }


    @Override
    public void onEnable() {
        commands.add(new TpCommand(plugin));
        commands.add(new TpPosCommand(plugin));
    }

    @Override
    public void onDisable() {

    }

    @Override
    public @NotNull List<ICommand> getCommands() {
        return commands;
    }
}
