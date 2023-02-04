package dev.crifurch.unlimitedadmin.modules.chat;

import dev.crifurch.unlimitedadmin.api.ModuleConfig;
import dev.crifurch.unlimitedadmin.api.interfaces.IConfig;
import dev.crifurch.unlimitedadmin.api.providers.PluginFileProvider;
import dev.crifurch.unlimitedadmin.modules.chat.data.Ignore;
import dev.crifurch.unlimitedadmin.modules.chat.data.Mute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

public enum ChatMuteConfig implements IConfig {
    MUTED_LIST("muted", null, "list of users and their muted chats"),
    IGNORE_LIST("ignored", null, "list of users and their irnored players");

    private static ModuleConfig config;
    private final Object value;
    private final String path;
    private final String description;

    ChatMuteConfig(String path, Object value, String description) {
        this.value = value;
        this.path = path;
        this.description = description;
    }

    public static void init(ChatModule module) {
        final File moduleConfigFile = PluginFileProvider.UnlimitedAdmin.getModuleConfigFile(module.getDefinition(), "mute");
        config = new ModuleConfig(moduleConfigFile);
        config.load(Arrays.asList(values()));
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

    @Nullable
    public static Mute getMuteForPlayer(UUID player) {
        final ConfigurationSection section = MUTED_LIST.getSection();
        if (section == null) {
            return null;
        }
        final ConfigurationSection configurationSection = section.getConfigurationSection(player.toString());
        if (configurationSection == null) return null;
        return Mute.fromConfigurationSection(configurationSection);
    }

    @Nullable
    public static Ignore getIgnoreForPlayer(UUID player) {
        final ConfigurationSection section = IGNORE_LIST.getSection();
        if (section == null) {
            return null;
        }
        final ConfigurationSection configurationSection = section.getConfigurationSection(player.toString());
        if (configurationSection == null) return null;
        return Ignore.fromConfigurationSection(configurationSection);
    }

    public static void saveMuted(Mute mute) {
        ConfigurationSection muteSection = MUTED_LIST.getSection();
        if (muteSection == null) {
            muteSection = config.createConfigurationSection(MUTED_LIST.getPath());
        }
        ConfigurationSection section = muteSection.createSection(mute.getUUID().toString());
        section = mute.toConfigurationSection(section);
        muteSection.set(mute.getUUID().toString(), section);
        config.set(MUTED_LIST.getPath(), muteSection, true);
    }

    public static void saveIgnored(Ignore ignore) {
        ConfigurationSection ignoreSection = IGNORE_LIST.getSection();
        if (ignoreSection == null) {
            ignoreSection = config.createConfigurationSection(IGNORE_LIST.getPath());
        }
        ConfigurationSection section = ignoreSection.createSection(ignore.getUUID().toString());
        section = ignore.toConfigurationSection(section);
        ignoreSection.set(ignore.getUUID().toString(), section);
        config.set(IGNORE_LIST.getPath(), ignoreSection, true);
    }

    public ConfigurationSection getSection() {
        return config.getConfigurationSection(getPath());
    }



}
