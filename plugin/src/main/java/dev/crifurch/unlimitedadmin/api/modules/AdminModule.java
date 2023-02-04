package dev.crifurch.unlimitedadmin.api.modules;

import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommandGroup;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModule;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class AdminModule extends EnableStateProvider implements IModule, ICommandGroup {

    @Override
    public @NotNull String getName() {
        return IModule.super.getName();
    }

    @Override
    public @NotNull Collection<ICommand> getCommands() {
        return IModule.super.getCommands();
    }

}