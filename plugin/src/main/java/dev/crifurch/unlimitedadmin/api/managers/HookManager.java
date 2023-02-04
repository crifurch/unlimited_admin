package dev.crifurch.unlimitedadmin.api.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class HookManager {

    public static boolean checkVault() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Vault");
        return plugin != null && plugin.isEnabled();
    }

    public static boolean checkPlaceholderAPI() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        return plugin != null && plugin.isEnabled();
    }

    public static boolean checkEssentials() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        return plugin != null && plugin.isEnabled();
    }

    public static boolean checkPurpur() {
        try {
            Class.forName("org.purpurmc.purpur.event.PlayerAFKEvent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


}