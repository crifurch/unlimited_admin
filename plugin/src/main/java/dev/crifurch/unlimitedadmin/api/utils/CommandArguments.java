package dev.crifurch.unlimitedadmin.api.utils;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandArguments implements Iterable<String> {
    private final ArrayList<String> args;

    public CommandArguments(Collection<String> args) {
        this.args = new ArrayList<>(args);
    }

    public CommandArguments(String... args) {
        this.args = new ArrayList<>();
        Collections.addAll(this.args, args);
    }

    public String last() {
        if (count() == 0) {
            return null;
        }
        return args.get(args.size() - 1);
    }

    public String get(int index) {
        if (count() <= index) {
            return null;
        }
        return args.get(index);
    }


    @Nullable
    public <T> T get(int index, Class<T> type) {
        if (index < 0 || args.size() <= index) {
            return null;
        }
        String arg = get(index);
        try {
            if (type == String.class) {
                return type.cast(arg);
            }
            if (type == Integer.class) {
                return type.cast(Integer.valueOf(arg));
            }
            if (type == Double.class) {
                return type.cast(Double.valueOf(arg));
            }
            if (type == Float.class) {
                return type.cast(Float.valueOf(arg));
            }
            if (type == Long.class) {
                return type.cast(Long.valueOf(arg));
            }
            if (type == Short.class) {
                return type.cast(Short.valueOf(arg));
            }
            if (type == Byte.class) {
                return type.cast(Byte.valueOf(arg));
            }
            if (type == Boolean.class) {
                return type.cast(arg.equals("true") || arg.equals("1"));
            }
            if (type == Player.class) {
                return type.cast(Bukkit.getPlayer(arg));
            }
            if (type == UUID.class) {
                return type.cast(UUID.fromString(arg));
            }
            if (type == World.class) {
                return type.cast(Bukkit.getWorld(arg));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }


    @Nullable
    public Player getOnlinePlayer(int index) {
        return get(index, Player.class);
    }

    @Nullable
    public UUID getPlayerUUID(int index) {
        final UUID uuid = get(index, UUID.class);
        if (uuid == null) {
            UnlimitedAdmin plugin = JavaPlugin.getPlugin(UnlimitedAdmin.class);
            return plugin.getPlayersMapModule().getPlayerUUID(get(index));
        }
        return uuid;
    }

    public String getPlayerNickName(int index) {
        final UUID uuid = getPlayerUUID(index);
        if (uuid == null) {
            return get(index);
        }
        return JavaPlugin.getPlugin(UnlimitedAdmin.class).getPlayersMapModule().getPlayerName(uuid);
    }

    public String getMessage(int startIndex) {
        return String.join(" ", args.subList(startIndex, args.size()));
    }

    public int count() {
        return args.size();
    }

    public CommandArguments cut(int from, int to) {
        return new CommandArguments(args.subList(Math.max(from, 0), Math.min(to, count())));
    }

    public CommandArguments cut(int length) {
        return cut(0, length);
    }

    public CommandArguments cutFrom(int from) {
        return cut(from, count());
    }

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return args.iterator();
    }

    public boolean isEmpty() {
        return args.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }
}
