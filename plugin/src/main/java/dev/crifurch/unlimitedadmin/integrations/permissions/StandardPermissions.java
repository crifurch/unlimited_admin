package dev.crifurch.unlimitedadmin.integrations.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StandardPermissions implements PermissionPlugin {
    @Override
    public String getName() {
        return "Using Standard Permissions Manager";
    }

    @Override
    public String getPrefix(Player p) {
        return "";
    }

    @Override
    public String getSuffix(Player p) {
        return "";
    }

    @Override
    public String[] getGroupNames(Player p) {
        return new String[]{""};
    }


    @Override
    public PermissionStatus havePermission(Player p, String permission) {
        if (p.isOp()) {
            return PermissionStatus.PERMISSION_TRUE;
        }
        if (!p.isPermissionSet(permission)) {
            return PermissionStatus.PERMISSION_UNSET;
        }
        return p.hasPermission(permission) ? PermissionStatus.PERMISSION_TRUE
                : PermissionStatus.PERMISSION_FALSE;
    }

    @Override
    public PermissionStatus havePermission(CommandSender p, String permission) {
        if (p.isOp()) {
            return PermissionStatus.PERMISSION_TRUE;
        }
        if (p instanceof Player) {
            return havePermission((Player) p, permission);
        }
        return PermissionStatus.PERMISSION_FALSE;

    }

}
