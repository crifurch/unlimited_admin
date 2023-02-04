package dev.crifurch.unlimitedadmin.modules.playersmap;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModuleDefinition;
import dev.crifurch.unlimitedadmin.api.modules.AdminModule;
import dev.crifurch.unlimitedadmin.api.utils.FileUtils;
import dev.crifurch.unlimitedadmin.api.utils.PlayerUtils;
import dev.crifurch.unlimitedadmin.modules.playersmap.data.CachedPlayer;
import dev.crifurch.unlimitedadmin.modules.playersmap.data.PlayerFirstJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PlayersMapModule extends AdminModule implements Listener {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ArrayList<CachedPlayer> playerMap = new ArrayList<>();
    private final File mapFile;
    protected final File playerDataFolder;

    public PlayersMapModule(UnlimitedAdmin plugin) {
        mapFile = FileUtils.getFileFromList(plugin.getDataFolder(), "playerMap.csv");
        playerDataFolder = new File(plugin.getDataFolder(), "playersData");
        if (!playerDataFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            playerDataFolder.mkdir();
        }
    }

    @Override
    public @NotNull IModuleDefinition getDefinition() {
        return ModulesManager.PLAYERS_MAP;
    }

    @Override
    public void onEnable() {
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        playerMap.clear();
    }

    @Override
    public Collection<Listener> getListeners() {
        return Collections.singleton(this);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.MONITOR)
    public void onPlayerLogin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        LocalDateTime now = LocalDateTime.now();
        CachedPlayer setted = null;
        boolean isFirstJoin = false;
        for (CachedPlayer i : playerMap) {
            if (i.uuid.equals(player.getUniqueId().toString())) {
                setted = i;
                break;
            }
        }
        if (setted == null) {
            final CachedPlayer cachedPlayer = new CachedPlayer(player.getUniqueId().toString(), player.getName(), now);
            playerMap.add(cachedPlayer);
            isFirstJoin = true;
        } else {
            setted.lastSign = now;
        }
        playerMap.sort(Comparator.comparing(o -> ((CachedPlayer) o).lastSign).reversed());
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final World playerWorld = PlayerDataHelper.getPlayerWorld(player.getUniqueId());
        if (player.getWorld() != playerWorld && playerWorld != null) {
            final Location location = player.getLocation();
            location.setWorld(playerWorld);
            PlayerUtils.setLocation(player.getUniqueId(), location);
        }
        if (isFirstJoin) {
            Bukkit.getPluginManager().callEvent(new PlayerFirstJoinEvent(player));
        }
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        PlayerDataHelper.setPlayerWorld(player.getUniqueId(), player.getWorld());
    }


    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        PlayerDataHelper.setPlayerWorld(player.getUniqueId(), player.getWorld());
    }


    private void load() throws IOException {
        playerMap.clear();
        final BufferedReader fileReader = new BufferedReader(new FileReader(mapFile));
        String currentLine;
        LocalDateTime now = LocalDateTime.now();
        while ((currentLine = fileReader.readLine()) != null) {
            if (!currentLine.contains(",")) {
                break;
            }
            final String[] split = currentLine.split(",");
            //todo remove after servers migrate to new plugin
            final String text;
            if (split.length >= 3) {
                text = split[2];
            } else {
                text = dtf.format(now);
            }

            playerMap.add(new CachedPlayer(split[1], split[0], LocalDateTime.parse(text, dtf)));
        }
        fileReader.close();
        playerMap.sort(Comparator.comparing(o -> ((CachedPlayer) o).lastSign).reversed());
    }

    private void save() throws IOException {
        final BufferedWriter fileWriter = new BufferedWriter(new FileWriter(mapFile));
        final boolean[] isFirst = {true};
        playerMap.forEach(cachedPlayer -> {
            try {
                if (isFirst[0]) {
                    isFirst[0] = false;
                } else {
                    fileWriter.newLine();
                }
                fileWriter.write(cachedPlayer.name + "," + cachedPlayer.uuid + "," + dtf.format(cachedPlayer.lastSign));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fileWriter.close();
    }

    public Collection<CachedPlayer> getPlayers() {
        return new ArrayList<>(playerMap);
    }

    public Iterable<UUID> getPlayersUUID() {
        return playerMap.stream().map(cachedPlayer -> UUID.fromString(cachedPlayer.uuid)).collect(Collectors.toList());
    }

    @Nullable
    public UUID getPlayerUUID(String name) {
        for (CachedPlayer p : playerMap) {
            if (p.name.equals(name)) {
                return UUID.fromString(p.uuid);
            }
        }
        return null;
    }

    @Nullable
    public String getPlayerName(UUID id) {
        for (CachedPlayer p : playerMap) {
            if (p.uuid.equals(id.toString())) {
                return p.name;
            }
        }
        return null;
    }

    public boolean containsPlayer(UUID id) {
        for (CachedPlayer p : playerMap) {
            if (p.uuid.equals(id.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsPlayer(String nickname) {
        for (CachedPlayer p : playerMap) {
            if (p.name.equals(nickname)) {
                return true;
            }
        }
        return false;
    }


}
