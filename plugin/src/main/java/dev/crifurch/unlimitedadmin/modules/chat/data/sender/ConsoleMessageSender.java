package dev.crifurch.unlimitedadmin.modules.chat.data.sender;

import dev.crifurch.unlimitedadmin.UnlimitedAdminConfig;
import dev.crifurch.unlimitedadmin.api.utils.PlaceHolderUtils;
import dev.crifurch.unlimitedadmin.integrations.permissions.PermissionStatus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ConsoleMessageSender extends ChatMessageSender {
    private final ConsoleCommandSender consoleCommandSender;

    public ConsoleMessageSender(ConsoleCommandSender consoleCommandSender) {
        if (consoleCommandSender == null) {
            this.consoleCommandSender = Bukkit.getServer().getConsoleSender();
        } else {
            this.consoleCommandSender = consoleCommandSender;
        }
    }

    @Override
    public @NotNull String getName() {
        return UnlimitedAdminConfig.SERVER_NAME.getString();
    }

    @Override
    public @NotNull UUID getUUID() {
        return UUID.fromString("00000000-0000-0000-0000-000000000000");
    }

    @Override
    public @NotNull PermissionStatus getPermissionStatus(String permission) {
        return PermissionStatus.PERMISSION_TRUE;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        consoleCommandSender.sendMessage(message);
    }

    @Override
    public boolean sameWorld(@NotNull World world) {
        return true;
    }

    @Override
    public @NotNull String replacePlaceholders(@NotNull String message) {
        return PlaceHolderUtils.replaceServerPlaceholder(message, consoleCommandSender);
    }

    @Override
    public boolean sameAs(Object obj) {
        if (obj instanceof ConsoleMessageSender) {
            return ((ConsoleMessageSender) obj).consoleCommandSender == consoleCommandSender;
        }
        if (obj instanceof ConsoleCommandSender) {
            return obj == consoleCommandSender;
        }
        return false;
    }

    @Override
    public boolean inDistance(Location location, double squaredDistance) {
        return false;
    }
}

