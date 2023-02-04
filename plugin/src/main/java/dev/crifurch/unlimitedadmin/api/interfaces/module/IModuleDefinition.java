package dev.crifurch.unlimitedadmin.api.interfaces.module;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IModuleDefinition {
    @NotNull
    String getName();

    @NotNull
    String getClassPath();

    boolean isEnabled();

    @Nullable
    IModule getModule();
}
