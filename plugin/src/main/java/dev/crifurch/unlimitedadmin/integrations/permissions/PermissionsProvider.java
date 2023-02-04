package dev.crifurch.unlimitedadmin.integrations.permissions;

import dev.crifurch.unlimitedadmin.api.managers.HookManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionsProvider implements PermissionPlugin {

    private static PermissionPlugin handler;
    private static PermissionsProvider INSTANCE;

    public static PermissionsProvider getInstance() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    private static void load() {
        INSTANCE = new PermissionsProvider();
        if (HookManager.checkVault() && VaultPermissions.setupChat() && VaultPermissions.setupPermissions()) {
            handler = new VaultPermissions();
        } else {
            handler = new StandardPermissions();
        }
    }

    @Override
    public String getName() {
        return handler.getName();
    }

    @Override
    public String getPrefix(Player p) {
        return handler.getPrefix(p);
    }

    @Override
    public String getSuffix(Player p) {
        return handler.getSuffix(p);
    }

    @Override
    public String[] getGroupNames(Player p) {
        return handler.getGroupNames(p);
    }

    @Override
    public PermissionStatus havePermission(Player p, String permission) {
        return handler.havePermission(p, permission);
    }

    public PermissionStatus havePermission(CommandSender p, String permission) {
        return handler.havePermission(p, permission);
    }
}