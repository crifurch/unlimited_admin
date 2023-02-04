package dev.crifurch.unlimitedadmin.modules.playersmap;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class PlayerDataHelper {
    @Nullable
    private static File getPlayerDataFile(UUID player) {
        if (!UnlimitedAdmin.getInstance().getPlayersMapModule().containsPlayer(player)) {
            return null;
        }
        final File playerDataFile = new File(UnlimitedAdmin.getInstance().getPlayersMapModule().playerDataFolder, player.toString() + ".yml");
        if (!playerDataFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return playerDataFile;
    }

    @Nullable
    private static YamlConfiguration getPlayerData(UUID player) {
        final File playerDataFile = getPlayerDataFile(player);
        if (playerDataFile == null) {
            return null;
        }
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(playerDataFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return config;
    }

    public static boolean setPlayerWorld(UUID player, World world) {
        final YamlConfiguration playerData = getPlayerData(player);
        if (playerData == null) {
            return false;
        }
        playerData.set("world", world.getName());
        try {
            playerData.save(Objects.requireNonNull(getPlayerDataFile(player)));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Nullable
    public static World getPlayerWorld(UUID player) {
        final YamlConfiguration playerData = getPlayerData(player);
        if (playerData == null) {
            return null;
        }
        if (playerData.contains("world")) {
            return Bukkit.getWorld(Objects.requireNonNull(playerData.getString("world")));
        }
        return null;
    }
}
