package dev.crifurch.unlimitedadmin.modules.chat;

import dev.crifurch.unlimitedadmin.api.ModuleConfig;
import dev.crifurch.unlimitedadmin.api.interfaces.IConfig;
import dev.crifurch.unlimitedadmin.api.permissions.UnlimitedAdminPermissionsList;
import dev.crifurch.unlimitedadmin.api.providers.PluginFileProvider;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ChatModuleConfig implements IConfig {
    IS_GLOBAL_CHAT_WORLD_WIDE("chat.global.world-wide", true, "if true, global chat will be world wide, if false, global chat will be only in world where sender is"),
    IS_GLOBAL_CHAT_ENABLED("chat.global.enabled", true, "if true, global chat will be enabled, if false, global chat will be disabled"),
    GLOBAL_CHAT_FORMAT("chat.global.format", "%prefix%displayname%suffix: &e%message", "format of global chat"),
    GLOBAL_CHAT_PREFIX("chat.global.prefix", "!", "prefix of global chat"),
    IS_LOCAL_CHAT_ENABLED("chat.local.enabled", true, "if true, local chat will be enabled, if false, local chat will be disabled"),
    LOCAL_CHAT_FORMAT("chat.local.format", "%prefix%displayname%suffix: &f%message", "format of local chat"),
    LOCAL_CHAT_PREFIX("chat.local.prefix", "", "prefix of local chat"),
    LOCAL_CHAT_RADIUS("chat.local.radius", 50, "radius of local chat"),

    IS_SPY_CHAT_ENABLED("chat.spy.enabled", true, "if true ops and users with permission(permission: " + UnlimitedAdminPermissionsList.CHAT_SPY + ") can see all local and private messages"),
    SPY_CHAT_FORMAT("chat.spy.format", "&e[SPY] &f%message", "format of spy chat"),
    SPY_CHAT_SHOW_CHANNEL("chat.spy.show-channel", true, "if true, spy chat will show channel of message, if false"),

    IS_PRIVATE_CHAT_ENABLED("chat.private.enabled", true, "if true, private chat will be enabled, if false, private chat will be disabled"),

    PRIVATE_CHAT_FORMAT("chat.private.format", "&8[&f%prefix%displayname%suffix &8-> &f%2prefix%2displayname%2suffix&8]: &f%message", "format of private chat"),
    IS_LOG_CHAT_ENABLED("log.enabled", true, "if true, log chat will be enabled, if false, log chat will be disabled"),
    SHOW_NOBODY_HEAR_YOU_MESSAGE("show-nobody-hear-you-message", true, "if true, nobody hear you message will be shown"),
    ALLOW_PLAYER_USE_COLOR_CODES("allow-player-use-color-codes", true, "if true, players can use color codes in chat"),
    NOTIFICATIONS_ENABLED("notifications.enabled", true, "if true, notifications will be enabled\n" +
            "example of ads:\n" +
            "  notifications:\n" +
            "    messages: \n" +
            "      test:\n" +
            "        message: \"&eThis is test message\"\n" +
            "        interval: 10\n"),
    NOTIFICATION_MESSAGES("notifications.messages", null, "list of notifications messages", true),

    SAY_FORMAT("say.format", "&8[%player]: &f%message", "format of say command"),
    SAY_MAX_DELAY("say.max-delay", 1200, "max delay of saylater command"),
    BAD_WORDS_ENABLED("bad-words.enabled", true, "if true, bad words protection will be enabled"),
    BAD_WORDS_CHANNELS("bad-words.channels", Collections.singletonList("*"), "list of channels where bad words protection will be enabled"),
    ADS_ENABLED("ads.enabled", true, "if true, ads protection will be enabled"),
    ADS_CHANNELS("ads.channels", Collections.singletonList("*"), "list of channels where ads protection will be enabled"),
    ADS_DOMAINS_EXCEPT("ads.domains-except", Collections.singletonList("example.com"), "list of domains that will be ignored by ads protection"),
    ;

    private static ModuleConfig config;
    private final Object value;
    private final String path;
    private final String description;
    private final boolean optional;

    ChatModuleConfig(String path, Object val, String description) {
        this.path = path;
        this.value = val;
        this.description = description;
        this.optional = false;
    }

    ChatModuleConfig(String path, Object val, String description, boolean optional) {
        this.path = path;
        this.value = val;
        this.description = description;
        this.optional = optional;
    }

    public static void init(ChatModule module) {
        config = PluginFileProvider.UnlimitedAdmin.getModuleConfig(module.getDefinition());
        config.load(Arrays.asList(values()));
    }

    public int getInt() {
        return config.getInt(getPath());
    }

    public boolean getBoolean() {
        return config.getBoolean(getPath());
    }

    public String getString() {
        return config.getString(getPath());
    }

    public List<String> getStringList() {
        return config.getStringList(getPath());
    }

    public ConfigurationSection getSection() {
        return config.getConfigurationSection(getPath());
    }

    public ConfigurationSection createSection() {
        return config.createConfigurationSection(path);
    }

    public void saveSection(ConfigurationSection section) {
        config.set(path, section, true);
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