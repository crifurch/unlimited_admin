package dev.crifurch.unlimitedadmin.modules.chat.data.sender;

import dev.crifurch.unlimitedadmin.integrations.permissions.PermissionStatus;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class ChatMessageSender {
    public static ChatMessageSender fromSender(Entity entity) {
        if (entity instanceof Player) {
            return new PlayerMessageSender((Player) entity);
        }
        if (entity == null) {
            return new ConsoleMessageSender(null);
        }
        return new EntityMessageSender(entity);
    }

    public static ChatMessageSender fromSender(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            return new PlayerMessageSender((Player) commandSender);
        }
        return new ConsoleMessageSender(null);
    }

    @NotNull
    public abstract String getName();

    @NotNull
    public abstract UUID getUUID();

    @NotNull
    public abstract PermissionStatus getPermissionStatus(String permission);

    public abstract void sendMessage(@NotNull String message);

    public abstract boolean sameWorld(@NotNull World world);

    @NotNull
    public abstract String replacePlaceholders(@NotNull String message);

    public abstract boolean sameAs(Object obj);

    public abstract boolean inDistance(Location location, double squaredDistance);
}
