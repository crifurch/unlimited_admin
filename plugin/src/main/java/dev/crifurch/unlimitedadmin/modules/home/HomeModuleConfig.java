package dev.crifurch.unlimitedadmin.modules.home;

import dev.crifurch.unlimitedadmin.api.ModuleConfig;
import dev.crifurch.unlimitedadmin.api.interfaces.IConfig;
import dev.crifurch.unlimitedadmin.api.providers.PluginFileProvider;

import java.util.Arrays;

public enum HomeModuleConfig implements IConfig {
    HOME_LIMIT("groups.default", -1, "how many homes player in group can contains: 0 - no homes, -1 - unlimited"),

    PREFERS_TELEPORT_ON_DEATH("prefer_teleport_on_death", true, "if player died and respawned, he teleport to home");
    private final Object value;
    private final String path;
    private final String description;
    private final boolean optional;
    private static ModuleConfig config;

    public static void init(HomeModule module) {
        config = PluginFileProvider.UnlimitedAdmin.getModuleConfig(module.getDefinition());
        config.load(Arrays.asList(values()));
    }

    HomeModuleConfig(String path, Object val, String description) {
        this.path = path;
        this.value = val;
        this.description = description;
        this.optional = false;
    }

    HomeModuleConfig(String path, Object val, String description, boolean optional) {
        this.path = path;
        this.value = val;
        this.description = description;
        this.optional = optional;
    }

    public int getInt() {
        return config.getInt(getPath());
    }

    public boolean getBoolean() {
        return config.getBoolean(getPath());
    }


    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Object getDefaultValue() {
        return value;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }
}
