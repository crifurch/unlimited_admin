package dev.crifurch.unlimitedadmin.modules.home.data;

import dev.crifurch.unlimitedadmin.api.GlobalConstants;
import org.bukkit.Location;

import java.util.List;

public class Home {
    private final List<String> players;
    private final String id;
    private final String name;
    private final Location location;

    public Home(String id, List<String> player, String name, Location location) {
        this.id = id;
        this.players = player;
        this.name = name;
        this.location = location;
    }

    public List<String> getPlayers() {
        return players;
    }

    private String getPlayerAsOwner(String player) {
        return GlobalConstants.defaultOwnerPrefix + player;
    }

    public void addPlayer(String player) {
        if (players.contains(player)) {
            return;
        }
        players.add(player);
    }

    public void addOwner(String player) {
        players.remove(player);
        players.add(getPlayerAsOwner(player));
    }

    public boolean isOwner(String player) {
        final String asOwner = getPlayerAsOwner(player);
        for (String i : players) {
            if (i.equals(asOwner)) {
                return true;
            }
        }
        return false;
    }

    public boolean isParticipant(String player) {
        final String asOwner = getPlayerAsOwner(player);
        for (String i : players) {
            if (i.equals(asOwner) || i.equals(player)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public String getUUID() {
        return id.split("@")[0];
    }

    public String getId() {
        return id;
    }

}
