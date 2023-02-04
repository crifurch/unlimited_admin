package dev.crifurch.unlimitedadmin.modules.player_status;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModuleDefinition;
import dev.crifurch.unlimitedadmin.api.modules.RawModule;
import dev.crifurch.unlimitedadmin.modules.player_status.commands.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatusModule extends RawModule {

    private final List<ICommand> commands = new ArrayList<>();
    final UnlimitedAdmin plugin;

    public PlayerStatusModule(UnlimitedAdmin plugin) {
        this.plugin = plugin;

    }

    @Override
    public @NotNull IModuleDefinition getDefinition() {
        return ModulesManager.PLAYER_STATUS;
    }

    @Override
    public void onEnable() {
        commands.add(new HealCommand());
        commands.add(new HealAllCommand());
        commands.add(new KillCommand());
        commands.add(new KillAllCommand());
        commands.add(new FeedCommand());
        commands.add(new FeedAllCommand());
        commands.add(new FlyCommand());
        commands.add(new GetPosCommand());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public @NotNull List<ICommand> getCommands() {
        return commands;
    }


}