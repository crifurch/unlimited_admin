package dev.crifurch.unlimitedadmin.api.interfaces.module;

import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public interface IModule {
    @NotNull
    IModuleDefinition getDefinition();

    default String getName() {
        return getDefinition().getName();
    }

    @NotNull
    default Collection<ICommand> getCommands() {
        return Collections.emptyList();
    }

    default Collection<Listener> getListeners() {
        return Collections.emptyList();
    }

    /**
     * only for modules that extends ICommand
     **/
    default boolean addAsCommandIfCommandsEmpty() {
        return false;
    }
}
