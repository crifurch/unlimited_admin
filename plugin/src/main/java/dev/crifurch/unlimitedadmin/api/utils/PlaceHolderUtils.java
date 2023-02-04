package dev.crifurch.unlimitedadmin.api.utils;

import dev.crifurch.unlimitedadmin.UnlimitedAdminConfig;
import dev.crifurch.unlimitedadmin.api.managers.HookManager;
import dev.crifurch.unlimitedadmin.integrations.permissions.PermissionsProvider;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceHolderUtils {
    static final Map<Integer, Pattern> PLACEHOLDER_PATTERN_CACHE = new HashMap<>();

    static {
        for (int i = 1; i < 10; i++) {
            PLACEHOLDER_PATTERN_CACHE.put(i, Pattern.compile("%" + i + "[a-zA-Z]+"));
        }
    }

    public static String replacePlayerPlaceholders(String format, @Nullable Entity... entities) {
        String result = format;

        if (entities != null && entities.length > 0) {
            for (int i = 0; i < entities.length; i++) {
                final Entity player = entities[i];
                result = makePlaceholdersUnderstand(result, i + 1);
                result = replacePlayerPlaceholder(result, player);
            }
        }
        result = replaceColors(result);

        return result;
    }

    private static String makePlaceholdersUnderstand(String placeholder, int i) {
        final Matcher matcher = PLACEHOLDER_PATTERN_CACHE.get(i).matcher(placeholder);
        while (matcher.find()) {
            final String group = matcher.group();
            final String newGroup = "%" + group.substring(("%" + i).length());
            placeholder = placeholder.replace(group, newGroup);
        }
        return placeholder;
    }

    public static String replacePlayerPlaceholder(String format, @Nullable Entity entity) {
        String result = format;
        if (HookManager.checkPlaceholderAPI() && entity instanceof Player) {
            try {
                final Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass("me.clip.placeholderapi.PlaceholderAPI");
                result = (String) aClass.getMethod("setPlaceholders", Player.class, String.class).invoke(null, entity, format);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        if ((HookManager.checkEssentials() || HookManager.checkPurpur()) && Config.AFK_PLACEHOLDER.getBoolean()) {
//            result = result.replace("%afk", "");
//        }
        Player player = null;
        if (entity instanceof Player) {
            player = (Player) entity;
        }
        result = result.replace("%displayname", getDisplayName(entity));
        result = result.replace("%prefix", player == null ? "" : PermissionsProvider.getInstance().getPrefix(player));
        result = result.replace("%suffix", player == null ? "" : PermissionsProvider.getInstance().getSuffix(player));
        result = result.replace("%player", entity == null ? "" : entity.getName());
        result = result.replace("%world", entity == null ? "" : entity.getWorld().getName());
        result = result.replace("%group", player == null ? "" : PermissionsProvider.getInstance().getGroupNames(player).length > 0 ? PermissionsProvider.getInstance().getGroupNames(player)[0] : "none");
        return result;
    }

    public static String replaceServerPlaceholder(String format, @NotNull CommandSender sender) {
        String result = format;
        //check if console
        if (sender instanceof ConsoleCommandSender) {
            result = result.replace("%displayname", UnlimitedAdminConfig.SERVER_NAME.getString());
        } else {
            result = result.replace("%displayname", sender.getName());
        }
        result = result.replace("%displayname", sender.getName());
        result = result.replace("%prefix", "");
        result = result.replace("%suffix", "");
        if (sender instanceof ConsoleCommandSender) {
            result = result.replace("%player", UnlimitedAdminConfig.SERVER_NAME.getString());
        } else {
            result = result.replace("%player", sender.getName());
        }
        result = result.replace("%world", "");
        result = result.replace("%group", "");
        return result;
    }

    @NotNull
    private static String getDisplayName(@Nullable Entity entity) {
        if (entity instanceof Player) {
            return ((Player) entity).getDisplayName();
        }
        if (entity == null) {
            return "";
        }
        final String customName = entity.getCustomName();
        if (customName != null) {
            return customName;
        }
        return entity.getName();
    }

    public static String replaceColors(String message) {
        message = RGBColors.translateCustomColorCodes(message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
