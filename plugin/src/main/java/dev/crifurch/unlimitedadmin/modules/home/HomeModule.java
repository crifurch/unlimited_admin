package dev.crifurch.unlimitedadmin.modules.home;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.GlobalConstants;
import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModuleDefinition;
import dev.crifurch.unlimitedadmin.api.modules.RawModule;
import dev.crifurch.unlimitedadmin.api.providers.PluginFileProvider;
import dev.crifurch.unlimitedadmin.api.utils.PlayerUtils;
import dev.crifurch.unlimitedadmin.modules.home.commands.DelHomeCommand;
import dev.crifurch.unlimitedadmin.modules.home.commands.HomeCommand;
import dev.crifurch.unlimitedadmin.modules.home.commands.InviteCommand;
import dev.crifurch.unlimitedadmin.modules.home.commands.SetHomeCommand;
import dev.crifurch.unlimitedadmin.modules.home.data.Home;
import dev.crifurch.unlimitedadmin.modules.home.listeners.HomeDeathListener;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeModule extends RawModule {

    private static YamlConfiguration cfg;
    private static File f;
    final UnlimitedAdmin plugin;
    private final ArrayList<ICommand> commands = new ArrayList<>();
    private final ArrayList<Listener> listeners = new ArrayList<>();

    public HomeModule(UnlimitedAdmin plugin) {
        this.plugin = plugin;
        f = PluginFileProvider.UnlimitedAdmin.getModuleConfigFile(getDefinition(), "homes");
    }


    @Override
    public @NotNull IModuleDefinition getDefinition() {
        return ModulesManager.HOME;
    }

    @Override
    public void onEnable() {
        cfg = YamlConfiguration.loadConfiguration(f);
        HomeModuleConfig.init(this);
        commands.add(new SetHomeCommand(this));
        commands.add(new HomeCommand(this));
        commands.add(new InviteCommand(this));
        commands.add(new DelHomeCommand(this));

        if (HomeModuleConfig.PREFERS_TELEPORT_ON_DEATH.getBoolean()) {
            listeners.add(new HomeDeathListener(this));
        }
    }

    @Override
    public void onDisable() {
        try {
            cfg.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @NotNull Collection<ICommand> getCommands() {
        return commands;
    }

    @Override
    public @NotNull Collection<Listener> getListeners() {
        return listeners;
    }

    private void writeHomeToConfig(Home home) {
        final String id = home.getId();
        final ConfigurationSection section;
        if (!cfg.contains(id)) {
            section = cfg.createSection(id);
        } else {
            section = cfg.getConfigurationSection(id);
        }
        assert section != null;
        section.set("name", home.getName());
        section.set("location", home.getLocation());
        section.set("players", home.getPlayers());

        try {
            cfg.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteHome(@NotNull UUID uuid, @NotNull String name) {
        final String path = uuid + "@" + name;
        if (cfg.contains(path)) {
            cfg.set(path, null);
            try {
                cfg.save(f);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean addParticipant(UUID player, @Nullable UUID owner, String name) {
        final List<Home> homes = getOwnerHomes(owner);
        Home home = null;
        for (Home i : homes) {
            if (name.equals(i.getName())) {
                home = i;
            }
        }

        if (home == null) {
            return false;
        } else {
            home.addPlayer(player.toString());
            writeHomeToConfig(home);
        }

        return true;
    }

    public void setHome(@NotNull Player player, @NotNull String name, @NotNull Location position) {
        final Home home = new Home(player.getUniqueId() + "@" + name, new ArrayList<>(), name, position);
        home.addOwner(player.getUniqueId().toString());
        writeHomeToConfig(home);
    }

    public List<Home> getHomes() {
        final Map<String, Object> values = cfg.getValues(false);
        final List<String> keys = new ArrayList<>(values.keySet());
        List<Home> result = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            final ConfigurationSection configurationSection = cfg.getConfigurationSection(keys.get(i));
            assert configurationSection != null;
            final List<String> players = configurationSection.getStringList("players");
            final String name = configurationSection.getString("name");
            final Location location = configurationSection.getLocation("location");
            Home home = new Home(keys.get(i), players, name, location);
            result.add(home);
        }
        return result;
    }

    public List<Home> getHomes(UUID player) {
        final List<Home> homes = getHomes();
        homes.removeIf(home -> !home.isParticipant(player.toString()));
        return homes;
    }

    public List<Home> getOwnerHomes(UUID player) {
        final List<Home> homes = getHomes();
        homes.removeIf(home -> !home.isOwner(player.toString()));
        return homes;
    }

    @Nullable
    public Home getHome(UUID player, String name) {
        UUID playerUUID = player;
        if (name.contains(":")) {
            final String[] split = name.split(":");
            playerUUID = plugin.getPlayersMapModule().getPlayerUUID(split[0]);
            name = split[1];
        }
        List<Home> homes = getHomes(player);
        for (Home i : homes) {
            if (name.equals(i.getName())) {
                if (playerUUID != null && !i.isOwner(playerUUID.toString())) {
                    continue;
                }
                return i;
            }
        }

        if (playerUUID == null) {
            final UUID playerUUID1 = plugin.getPlayersMapModule().getPlayerUUID(name);
            if (playerUUID1 != null) {
                homes = getOwnerHomes(player);
                for (Home i : homes) {
                    if (name.equals(GlobalConstants.defaultEntryName) && i.isParticipant(Objects.requireNonNull(player).toString())) {
                        return i;
                    }
                }
            }
        }

        return null;
    }

    public boolean teleportPlayerToHome(@NotNull UUID player, @NotNull Home home) {
        if (home.isParticipant(player.toString())) {
            PlayerUtils.setLocation(player, home.getLocation());
            return true;
        }
        return false;
    }

    @Nullable
    public String parseHomeName(@NotNull Player owner, @NotNull String name) throws NotifibleException {
        if (name.contains(":")) {
            final String[] split = name.split(":");
            final UUID playerUUID = plugin.getPlayersMapModule().getPlayerUUID(split[0]);
            if (split.length != 2 || playerUUID == null) {
                throw new NotifibleException(LangConfig.NO_SUCH_HOME.getText(""));
            }
            return playerUUID + ":" + split[1];
        }
        return owner.getUniqueId() + ":" + name;
    }


}