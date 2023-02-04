package dev.crifurch.unlimitedadmin.api.interfaces;

import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandNotEnoughArgsException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandOnlyForUserException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandOtherPermissionsException;
import dev.crifurch.unlimitedadmin.api.permissions.AdditionalPermissions;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.api.utils.CommandExecutor;
import dev.crifurch.unlimitedadmin.integrations.permissions.PermissionStatus;
import dev.crifurch.unlimitedadmin.integrations.permissions.PermissionsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface ICommand extends ICommandDataProvider {

    default String getUsageText() {
        return "/" + getName();
    }


    void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException;

    default void setCommandExecutor(JavaPlugin plugin, CommandExecutor executor) {
        final PluginCommand command = plugin.getCommand(getName());
        assert command != null;
        command.setExecutor(executor);
    }

    default void setTabCompleter(JavaPlugin plugin, TabCompleter completer) {
        final PluginCommand command = plugin.getCommand(getName());
        assert command != null;
        command.setTabCompleter(completer);
    }

    default TabCompleter getTabCompleter(JavaPlugin plugin) {
        final PluginCommand command = plugin.getCommand(getName());
        assert command != null;
        return command.getTabCompleter();
    }

    default void setPermissionMessage(JavaPlugin plugin, String message) {
        final PluginCommand command = plugin.getCommand(getName());
        assert command != null;
        command.setPermissionMessage(message);
    }

    default void setPermission(JavaPlugin plugin, String permission) {
        final PluginCommand command = plugin.getCommand(getName());
        assert command != null;
        command.setPermission(permission);
    }

    default void assertArgsSize(CommandArguments args) throws CommandNotEnoughArgsException {
        if (args.count() < getMinArgsSize()) {
            throw new CommandNotEnoughArgsException(getUsageText());
        }
    }

    default void assertSenderIsPlayer(CommandSender sender) throws CommandOnlyForUserException {
        if (!(sender instanceof Player)) {
            throw new CommandOnlyForUserException();
        }
    }

    default void assertOtherPermission(CommandSender sender) throws CommandOtherPermissionsException {
        if (PermissionsProvider.getInstance().havePermission(sender,
                AdditionalPermissions.OTHER.getPermissionForCommand(this)) != PermissionStatus.PERMISSION_TRUE) {
            throw new CommandOtherPermissionsException();
        }
    }
}
