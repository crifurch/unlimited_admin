package dev.crifurch.unlimitedadmin.api.utils;

import dev.crifurch.unlimitedadmin.modules.playersmap.PlayerDataHelper;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.ListTag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerUtils {

    @Nullable
    private static Player getOnlinePlayer(UUID player) {
        final Player player1 = Bukkit.getServer().getPlayer(player);
        if (player1 == null || !player1.isOnline())
            return null;
        return player1;
    }

    @Nullable
    private static NamedTag getPlayerData(UUID player) {
        File file = new File("world/playerdata/" + player.toString() + ".dat");
        try {
            return NBTUtil.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean savePlayerData(UUID player, NamedTag tag) {
        File file = new File("world/playerdata/" + player.toString() + ".dat");
        try {
            NBTUtil.write(tag, file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void setLocationDelayed(UUID player, Location location, int delay) {
        ScheduledExecutorService executor =
                Executors.newScheduledThreadPool(4);
        executor.schedule(
                () -> {
                    PlayerUtils.setLocation(player, location);
                },
                delay,
                TimeUnit.MILLISECONDS
        );
    }

    public static void setLocationDelayed(Player player, Location location, int delay) {
        ScheduledExecutorService executor =
                Executors.newScheduledThreadPool(4);
        executor.schedule(
                () -> {
                    PlayerUtils.setLocation(player, location);
                },
                delay,
                TimeUnit.MILLISECONDS
        );
    }

    public static boolean setLocation(Player player, Location location) {
        return setLocation(player.getUniqueId(), location);
    }

    public static boolean setLocation(UUID uuid, Location location) {
        Player player = getOnlinePlayer(uuid);
        if (player != null) {
            player.teleport(location);
            return true;
        }
        NamedTag tag = getPlayerData(uuid);

        final ListTag<DoubleTag> pos = (ListTag<DoubleTag>) ((CompoundTag) tag.getTag()).getListTag("Pos");
        pos.clear();
        pos.add(new DoubleTag(location.getX()));
        pos.add(new DoubleTag(location.getY()));
        pos.add(new DoubleTag(location.getZ()));

        final ListTag<FloatTag> rotation = (ListTag<FloatTag>) ((CompoundTag) tag.getTag()).getListTag("Rotation");
        rotation.clear();
        rotation.add(new FloatTag(location.getYaw()));
        rotation.add(new FloatTag(location.getPitch()));
        final boolean b = PlayerDataHelper.setPlayerWorld(uuid, location.getWorld());
        if (!b) {
            return false;
        }

        return savePlayerData(uuid, tag);
    }

    @Nullable
    public static Location getLocation(UUID uuid) {
        Player player = getOnlinePlayer(uuid);
        if (player != null) {
            return player.getLocation();
        }

        NamedTag tag = getPlayerData(uuid);
        final ListTag<DoubleTag> pos = (ListTag<DoubleTag>) ((CompoundTag) tag.getTag()).getListTag("Pos");
        final ListTag<FloatTag> rotation = (ListTag<FloatTag>) ((CompoundTag) tag.getTag()).getListTag("Rotation");
        final World playerWorld = PlayerDataHelper.getPlayerWorld(uuid);
        if (playerWorld == null) {
            return null;
        }
        return new Location(playerWorld, pos.get(0).asDouble(), pos.get(1).asDouble(),
                pos.get(2).asDouble(), rotation.get(0).asFloat(), rotation.get(1).asFloat());
    }

    public static double getMaxHealth(UUID player, boolean withModifiers) {
        Player _player = getOnlinePlayer(player);
        if (_player != null) {
            return Objects.requireNonNull(_player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        }
        NamedTag tag = getPlayerData(player);
        if (tag == null) {
            return 0;
        }
        final CompoundTag compoundTag = (CompoundTag) tag.getTag();
        final ListTag<CompoundTag> attributes = (ListTag<CompoundTag>) compoundTag.getListTag("Attributes");
        AtomicReference<Double> health = new AtomicReference<>((double) 0);
        attributes.forEach(entries -> {
            //todo check if this is the correct attribute
            if (entries.containsKey("Name") && entries.getString("Name").equals(Attribute.GENERIC_MAX_HEALTH.toString())) {
                health.updateAndGet(v -> v + entries.getDouble("Base"));
                if (withModifiers && entries.containsKey("Modifiers")) {
                    final ListTag<CompoundTag> modifiers = (ListTag<CompoundTag>) entries.getListTag("Modifiers");
                    modifiers.forEach(entries1 -> {
                        if (entries1.containsKey("Amount")) {
                            health.updateAndGet(v -> v + entries1.getDouble("Amount"));
                        }
                    });
                }
            }
        });
        return health.get();
    }

    public static boolean setHealth(UUID player, double health) {
        Player _player = getOnlinePlayer(player);
        if (_player != null) {
            _player.setHealth(Math.min(health,
                    Objects.requireNonNull(_player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue()));
            return true;
        }

        NamedTag tag = getPlayerData(player);
        if (tag == null) {
            return false;
        }
        ((CompoundTag) tag.getTag()).putDouble("Health", health);
        return savePlayerData(player, tag);
    }

    public static boolean setFood(UUID player, double food) {
        Player _player = getOnlinePlayer(player);
        if (_player != null) {
            _player.setFoodLevel((int) Math.min(food, 20));
            return true;
        }

        NamedTag tag = getPlayerData(player);
        if (tag == null) {
            return false;
        }
        ((CompoundTag) tag.getTag()).putDouble("foodLevel", food);
        return savePlayerData(player, tag);
    }

    public static boolean getFly(UUID player) {
        Player _player = getOnlinePlayer(player);
        if (_player != null) {
            return _player.getAllowFlight();
        }
        NamedTag tag = getPlayerData(player);
        if (tag == null) {
            return false;
        }
        final CompoundTag abilities = ((CompoundTag) tag.getTag()).getCompoundTag("abilities");
        return abilities.getByte("mayfly") == (byte) 1;
    }

    public static boolean setFly(UUID player, boolean fly) {
        Player _player = getOnlinePlayer(player);
        if (_player != null) {
            _player.setAllowFlight(fly);
            return true;
        }

        NamedTag tag = getPlayerData(player);
        if (tag == null) {
            return false;
        }
        final CompoundTag abilities = ((CompoundTag) tag.getTag()).getCompoundTag("abilities");
        abilities.putByte("mayfly", (byte) (fly ? 1 : 0));
        return savePlayerData(player, tag);
    }
}
