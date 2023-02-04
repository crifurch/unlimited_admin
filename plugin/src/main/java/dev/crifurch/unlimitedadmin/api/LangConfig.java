package dev.crifurch.unlimitedadmin.api;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.utils.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public enum LangConfig {
    ERROR_WHILE_COMMAND("command.errorWhileExecute", "Error when executing command"),
    ONLY_FOR_PLAYER_COMMAND("command.onlyForUser", "Only player can run this command whit this arguments"),
    NO_SUCH_MODULE("module.noSuchModule", "No such module %s"),
    NO_SUCH_COMMAND("module.noSuchCommand", "No such command %s"),
    NO_SUCH_PLAYER("player.notFound", "No such player %s"),
    OFFLINE_PLAYER("player.isOffline", "Player is offline"),
    NO_PERMISSIONS("permissions.no", "No permissions to do this  %s"),
    NO_PERMISSIONS_USE_ON_OTHER("permissions.no_other", "No permissions to do this with other %s"),
    SERVER_IN_MAINTAIN_MODE("maintain.isActive", "Server in maintain mode now"),
    SERVER_NOT_IN_MAINTAIN_MODE("maintain.isNotActive", "Server in work mode now"),
    WORLD_LOCKED_FOR_ENTERING("world.lockedEntire", "World locked for entering"),
    NO_SUCH_WORLD("world.notFound", "No such world %s"),
    WORLD_IS_LOCKED("world.isLocked", "World %s is locked"),
    WORLD_IS_UNLOCKED("world.isUnlocked", "World %s is unlocked"),
    WORLD_CREATION_BUSY("world.creationBusy", "Can't create world now, another world creating now"),
    WORLD_CREATION_ERROR("world.creationError", "Error while creating world %s"),
    WORLD_DELETION_BUSY("world.deletionBusy", "Can't delete world now, another world deleting now"),
    WORLD_DELETION_ERROR("world.deletionError", "Error while deleting world %s"),
    WORLD_UNSUPPORTED_ENVIRONMENT("world.unsupportedEnvironment", "World environment %s not supported"),
    WORLD_CREATED("world.created", "World %s created"),
    WORLD_DELETED("world.deleted", "World %s deleted"),

    HOME_CREATED("home.created", "Home %s was created"),
    NO_SUCH_HOME("home.notFound", "Home %s not found"),
    HOME_DELETED("home.deleted", "Home %s was deleted"),

    SPAWN_CREATED("spawn.created", "Spawn was created"),
    NO_SUCH_SPAWN("spawn.notFound", "Spawn %s not found"),
    SPAWN_DELETED("spawn.deleted", "Spawn %s was deleted"),

    DONATION_AMOUNT("donation_amount", "Caps amount: %s"),

    CHAT_NOBODY_HEAR("chat.nobody_hear", "&4Nobody hear your message"),
    CHAT_NO_ONE_TO_ANSWER("chat.no_one_to_answer", "&4You have no one to answer"),

    NO_SUCH_CHANNEL("chat.no", "No such channel %s"),
    CHAT_MUTED_IN_CHANNEL("chat.muted", "You muted in channel %s"),
    CHAT_BAD_WORD_DETECTED("chat.bad_word", "Bad word detected in your message"),
    CHAT_ADS_DETECTED("chat.ads", "Ads detected in your message"),
    NO_SUCH_NOTIFICATION("notification.no", "No such notification %s"),
    NOTIFICATIONS_REMOVE_BEFORE_ADD("notification.remove_before_add", "Remove notification with name %s before add"),

    MAX_SAY_DELAY_ERROR("notification.max_say_delay_error", "Max say delay is %s"),
    ;


    private final String value;
    private final String path;
    private static YamlConfiguration cfg;
    private static final File f = FileUtils.getFileFromList(UnlimitedAdmin.getInstance().getDataFolder(), "lang.yml");

    LangConfig(String path, String val) {
        this.path = path;
        this.value = val;
    }

    public static String getTextDirectly(String path) {
        return cfg.getString(path);
    }

    public String getText(Object... args) {
        final String string = cfg.getString(path);
        if (string == null) {
            return value;
        }
        try {
            return String.format(string, args);
        } catch (Exception e) {
            return string;
        }
    }

    public String getText() {
        final String string = cfg.getString(path);
        if (string == null) {
            return value;
        }
        try {
            return String.format(string, "");
        } catch (Exception e) {
            try {
                return String.format(string);
            } catch (Exception e1) {
                return string;
            }
        }

    }

    @Override
    public String toString() {
        return getText();
    }

    public static void load() {
        reload(false);
        save();
    }

    public static void save() {
        for (LangConfig c : values()) {
            if (!cfg.contains(c.path)) {
                c.set(c.value, false);
            }
        }
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
