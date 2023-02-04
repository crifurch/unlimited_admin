package dev.crifurch.unlimitedadmin.modules.antiop;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.utils.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public enum AntiOPConfig {
    OP_LIST("op_list", Collections.<String>emptyList(), "The list of players nickname or uuid that can be op"),
    LOG("log", true, "Log the ops in file"),
    DELAY_BEFORE_COMMANDS("delay_before_commands", 0, "The delay in seconds before run commands, just for fun:)"),
    ADDITIONAL_RUN_COMMANDS("additional_run_commands", Collections.<String>emptyList(), "The list of commands that will be executed when a non-granted player is op"),

    ;

    private final Object value;
    private final String path;
    private final String description;
    private static YamlConfiguration cfg;
    private static final File f = FileUtils.getFileFromList(UnlimitedAdmin.getInstance().getDataFolder(), ModulesManager.ANTIOP.getName(), "config.yml");

    AntiOPConfig(String path, Object val, String description) {
        this.path = path;
        this.value = val;
        this.description = description;
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

    @NotNull
    public ConfigurationSection getConfigurationSection() {

        final ConfigurationSection configurationSection = cfg.getConfigurationSection(path);
        if (configurationSection == null) {
            return cfg.createSection(path);
        }
        return configurationSection;
    }

    public static void load() {
        reload(false);
        save();
    }

    public static void save() {
        StringBuilder header = new StringBuilder();
        for (AntiOPConfig c : values()) {
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
