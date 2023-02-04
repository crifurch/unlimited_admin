package dev.crifurch.unlimitedadmin;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.events.ExternalModuleInitEvent;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommandGroup;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModule;
import dev.crifurch.unlimitedadmin.api.managers.CustomCommandsManager;
import dev.crifurch.unlimitedadmin.api.utils.CommandExecutor;
import dev.crifurch.unlimitedadmin.modules.playersmap.PlayersMapModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class UnlimitedAdmin extends JavaPlugin {
    private static UnlimitedAdmin INSTANCE;
    private final List<IModule> modules = new ArrayList<>();

    public static UnlimitedAdmin getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        CustomCommandsManager.init(this);
        UnlimitedAdminConfig.load();
        LangConfig.load();
        Bukkit.getPluginManager().callEvent(new ExternalModuleInitEvent());
        ModulesManager.loadModules();
    }


    @Override
    public void onDisable() {
        ModulesManager.unloadModules();
    }


    @NotNull
    public PlayersMapModule getPlayersMapModule() {
        return (PlayersMapModule) Objects.requireNonNull(ModulesManager.getModule(ModulesManager.PLAYERS_MAP));
    }




    public static class UnlimitedAdminCommand implements ICommandGroup {
        private final ArrayList<ICommand> commands = new ArrayList<>();
        private boolean isRegistered = false;

        @Override
        public @NotNull String getName() {
            return "unlimitedadmin";
        }

        @Override
        public @Nullable List<String> getAliases() {
            return Arrays.asList("una", "unlimadmin");
        }

        public void addCommand(ICommand command) {
            register();
            if (commands.stream().anyMatch(c -> c.getName().equalsIgnoreCase(command.getName()))) {
                throw new IllegalArgumentException("Command with name " + command.getName() + " already exists!");
            }
            commands.add(command);
        }

        public void removeCommand(ICommand command) {
            commands.remove(command);
            if (commands.isEmpty()) {
                unregister();
            }
        }

        public void register() {
            if (isRegistered) {
                return;
            }
            isRegistered = true;
            CustomCommandsManager.getInstance().registerCommand(this);
            setCommandExecutor(INSTANCE, new CommandExecutor(INSTANCE, this));
        }

        public void unregister() {
            if (!isRegistered) {
                return;
            }
            commands.clear();
            isRegistered = false;
        }

        @Override
        public @NotNull Collection<ICommand> getCommands() {
            return commands;
        }
    }
}
