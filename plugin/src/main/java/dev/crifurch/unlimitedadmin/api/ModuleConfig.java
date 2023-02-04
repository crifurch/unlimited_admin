package dev.crifurch.unlimitedadmin.api;

import dev.crifurch.unlimitedadmin.api.interfaces.IConfig;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ModuleConfig {
    private final File configFile;
    private YamlConfiguration cfg;

    public ModuleConfig(File configFile) {
        this.configFile = configFile;
        load();
    }

    public boolean getBoolean(String path) {
        return cfg.getBoolean(path);
    }

    public double getDouble(String path) {
        return cfg.getDouble(path);
    }

    public int getInt(String path) {
        return cfg.getInt(path);
    }

    public String getString(String path) {
        return cfg.getString(path);
    }

    public List<String> getStringList(String path) {
        return cfg.getStringList(path);
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return cfg.getConfigurationSection(path);
    }

    public ConfigurationSection createConfigurationSection(String path) {
        return cfg.createSection(path);
    }

    public Location getLocation(String path) {
        return (Location) cfg.get(path);
    }

    public void load() {
        reload(false);
        save(Collections.emptyList());
    }

    public void load(List<IConfig> values) {
        reload(false);
        save(values);
    }

    public void save(List<IConfig> values) {
        StringBuilder header = new StringBuilder();
        for (IConfig c : values) {

            header.append(c.getPath()).append(": ").append(c.getDescription()).append(System.lineSeparator());

            if (!cfg.contains(c.getPath()) && !c.isOptional()) {
                set(c.getPath(), c.getDefaultValue(), false);
            }
        }
        cfg.options().header(header.toString());
        try {
            cfg.save(configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void set(IConfig config, Object value, boolean save) {
        cfg.set(config.getPath(), value);
        if (save) {
            try {
                cfg.save(configFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            reload(false);
        }
    }

    public void set(String path, Object value, boolean save) {
        cfg.set(path, value);
        if (save) {
            try {
                cfg.save(configFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            reload(false);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void reload(boolean complete) {
        if (!complete) {
            configFile.getParentFile().mkdirs();
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cfg = YamlConfiguration.loadConfiguration(configFile);
            return;
        }
        load();
    }
}
