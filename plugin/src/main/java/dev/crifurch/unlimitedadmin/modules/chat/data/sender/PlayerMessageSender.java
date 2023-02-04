package dev.crifurch.unlimitedadmin.modules.chat.data.sender;

import dev.crifurch.unlimitedadmin.api.utils.PlaceHolderUtils;
import dev.crifurch.unlimitedadmin.integrations.permissions.PermissionStatus;
import dev.crifurch.unlimitedadmin.integrations.permissions.PermissionsProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerMessageSender extends ChatMessageSender {
    private final Player player;

    public PlayerMessageSender(Player player) {
        this.player = player;
    }

    @Override
    public @NotNull String getName() {
        return player.getName();
    }

    @Override
    public @NotNull UUID getUUID() {
        return player.getUniqueId();
    }

    @Override
    public @NotNull PermissionStatus getPermissionStatus(String permission) {
        return PermissionsProvider.getInstance().havePermission(player, permission);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        player.sendMessage(message);
    }

    @Override
    public boolean sameWorld(@NotNull World world) {
        return player.getWorld() == world;
    }

    @Override
    public @NotNull String replacePlaceholders(@NotNull String message) {
        return PlaceHolderUtils.replacePlayerPlaceholders(message, player);
    }

    @Override
    public boolean sameAs(Object obj) {
        if (obj instanceof PlayerMessageSender) {
            return ((PlayerMessageSender) obj).player == player;
        }
        if (obj instanceof Player) {
            return obj == player;
        }
        return false;
    }

    @Override
    public boolean inDistance(Location location, double squaredDistance) {
        return player.getLocation().distanceSquared(location) <= squaredDistance;
    }
}
