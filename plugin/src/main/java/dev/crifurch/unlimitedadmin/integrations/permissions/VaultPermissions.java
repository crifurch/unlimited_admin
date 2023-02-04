package dev.crifurch.unlimitedadmin.integrations.permissions;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPermissions implements PermissionPlugin {

    private static Chat chat = null;
    private static Permission permission = null;

    @Override
    public String getPrefix(Player p) {
        return chat.getPlayerPrefix(p.getWorld().getName(), p);

//        StringBuilder finalPrefix = new StringBuilder();
//        int i = 0;
//        for (String group : chat.getPlayerGroups(p)) {
//            String groupPrefix = chat.getGroupPrefix(p.getWorld(), group);
//            if (groupPrefix != null && !groupPrefix.isEmpty()) {
//                if (i > 1) {
//                    finalPrefix.append(" ");
//                }
//                finalPrefix.append(groupPrefix);
//                i++;
//            }
//        }
//        return finalPrefix.toString();
    }

    @Override
    public String getSuffix(Player p) {
        return chat.getPlayerSuffix(p.getWorld().getName(), p);
//        StringBuilder finalSuffix = new StringBuilder();
//        int i = 0;
//        for (String group : chat.getPlayerGroups(p)) {
//            String groupSuffix = chat.getGroupSuffix(p.getWorld(), group);
//            if (groupSuffix != null && !groupSuffix.isEmpty()) {
//                if (i > 1) {
//                    finalSuffix.append(" ");
//                }
//                i++;
//                finalSuffix.append(groupSuffix);
//            }
//        }
//        return finalSuffix.toString();
    }

    @Override
    public String[] getGroupNames(Player p) {
        return chat.getPlayerGroups(p);
    }



    @Override
    public String getName() {
        return chat.getName();
    }

    protected static boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
            return chat.isEnabled();
        }
        return false;
    }

    protected static boolean setupPermissions() {
        RegisteredServiceProvider<Permission> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (chatProvider != null) {
            permission = chatProvider.getProvider();
            return permission.isEnabled();
        }
        return false;
    }

    @Override
    public PermissionStatus havePermission(Player p, String permission) {
        if (p.isOp()) {
            return PermissionStatus.PERMISSION_TRUE;
        }
        return p.isPermissionSet(permission) ? (VaultPermissions.permission.playerHas(p, permission) ?
                PermissionStatus.PERMISSION_TRUE :
                PermissionStatus.PERMISSION_FALSE
        )
                : PermissionStatus.PERMISSION_UNSET;
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