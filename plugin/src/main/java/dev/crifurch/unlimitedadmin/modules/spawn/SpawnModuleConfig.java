package dev.crifurch.unlimitedadmin.modules.spawn;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.api.providers.PluginFileProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public enum SpawnModuleConfig {
    MULTISPAWNS("multispawns", true, "can server have spawns with names", false),
    SPAWN_LIMIT("spawns_count", -1, "how many spawns server can contains: 0 - no spawns, -1 - unlimited", false),

    PREFERRED_FIRST_SPAWN("teleport_first_spawn", true, "if player first joined teleport to main spawn", false),

    PREFER_TELEPORT_ON_DEATH("prefer_teleport_on_death", true, "if player died and respawned, he teleport to spawn", false);


    private final Object value;
    private final String path;
    private final String description;
    private final boolean optional;
    private static YamlConfiguration cfg;
    private static final File f = PluginFileProvider.UnlimitedAdmin.getModuleConfigFile(ModulesManager.SPAWN);

    SpawnModuleConfig(String path, Object val, String description, boolean optional) {
        this.path = path;
        this.value = val;
        this.description = description;
        this.optional = optional;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public Object getDefaultValue() {
        return value;
    }

    public boolean getBoolean() {
        return cfg.getBoolean(path);
    }

    public double getDouble() {
        return cfg.getDouble(path);
    }

    public int getInt() {
        return cfg.getInt(path);
    }

    public String getString() {
        return cfg.getString(path);
    }

    public List<String> getStringList() {
        return cfg.getStringList(path);
    }

    public ConfigurationSection getConfigurationSection() {
        return cfg.getConfigurationSection(path);
    }

    public static void load() {
        reload(false);
        save();
    }

    public static void save() {
        StringBuilder header = new StringBuilder();
        for (SpawnModuleConfig c : values()) {
            header.append(c.getPath()).append(": ").append(c.getDescription()).append(System.lineSeparator());
            if (!cfg.contains(c.getPath())) {
                c.set(c.getDefaultValue(), false);
            }
        }
        cfg.options().header(header.toString());
        try {
            cfg.save(f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void set(Object value, boolean save) {
        cfg.set(path, value);
        if (save) {
            try {
                cfg.save(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            reload(false);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void reload(boolean complete) {
        if (!complete) {
            f.getParentFile().mkdirs();
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cfg = YamlConfiguration.loadConfiguration(f);
            return;
        }
        load();
    }
}
