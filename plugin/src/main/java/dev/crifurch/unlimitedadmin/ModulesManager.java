package dev.crifurch.unlimitedadmin;

import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommandGroup;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModule;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModuleDefinition;
import dev.crifurch.unlimitedadmin.api.managers.CustomCommandsManager;
import dev.crifurch.unlimitedadmin.api.modules.AdminModule;
import dev.crifurch.unlimitedadmin.api.modules.EnableStateProvider;
import dev.crifurch.unlimitedadmin.api.modules.RawModule;
import dev.crifurch.unlimitedadmin.api.utils.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Supplier;

public enum ModulesManager implements IModuleDefinition {
    PLAYERS_MAP("players_map", "*.playersmap.PlayersMapModule", () -> true),
    WORLDS("worlds", "*.world.WorldsModule", UnlimitedAdminConfig.WORLDS_MODULE_ENABLED::getBoolean),
    MAINTAIN("maintain", "*.maintain.MaintainModule", UnlimitedAdminConfig.MAINTAIN_MODULE_ENABLED::getBoolean),
    TABLIST("tablist", "*.tablist.TabListModule", UnlimitedAdminConfig.TABLIST_MODULE_ENABLED::getBoolean),
    TELEPORT("teleport", "*.teleporting.TeleportingModule", UnlimitedAdminConfig.TELEPORT_MODULE_ENABLED::getBoolean),
    SPAWN("spawn", "*.spawn.SpawnModule", UnlimitedAdminConfig.SPAWN_MODULE_ENABLED::getBoolean),
    HOME("home", "*.home.HomeModule", UnlimitedAdminConfig.HOME_MODULE_ENABLED::getBoolean),
    PLAYER_STATUS("player_status", "*.player_status.PlayerStatusModule", UnlimitedAdminConfig.PLAYER_STATUS_MODULE_ENABLED::getBoolean),
    SHOP("shop", "*.shop.ShopModule", UnlimitedAdminConfig.SHOP_MODULE_ENABLED::getBoolean),
    CHAT("chat", "*.chat.ChatModule", UnlimitedAdminConfig.CHAT_MODULE_ENABLED::getBoolean),
    ANTIOP("antiop", "*.antiop.AntiOPModule", UnlimitedAdminConfig.ANTIOP_MODULE_ENABLED::getBoolean),

    ;
    private static final Collection<IModuleDefinition> externalValues = new ArrayList<>();
    private static final Map<IModuleDefinition, IModule> loadedModules = new HashMap<>();
    private static final UnlimitedAdmin.UnlimitedAdminCommand command = new UnlimitedAdmin.UnlimitedAdminCommand();
    final String name;
    final String classPath;
    final Supplier<@NotNull Boolean> enabled;

    ModulesManager(@NotNull String name, @NotNull String classPath, @NotNull Supplier<@NotNull Boolean> enabled) {
        this.name = name;
        this.classPath = classPath;
        this.enabled = enabled;
    }

    public static void addExternalModule(IModuleDefinition module) {
        if (!loadedModules.isEmpty()) {
            throw new IllegalStateException("You can't add external modules after loading");
        }
        if (externalValues.stream().anyMatch(m -> m.getName().equals(module.getName()))) {
            throw new IllegalArgumentException("Module with name " + module.getName() + " already exists");
        }
        externalValues.add(module);
    }

    @Nullable
    public static IModule getModule(IModuleDefinition module) {
        return loadedModules.get(module);
    }

    public static void loadModules() {
        if (!loadedModules.isEmpty()) {
            throw new IllegalStateException("Modules already loaded, use " + ModulesManager.class.getSimpleName() + ".reloadModules() instead");
        }
        final Collection<IModuleDefinition> modules = new ArrayList<>(values().length + externalValues.size());
        modules.addAll(Arrays.asList(values()));
        modules.addAll(externalValues);
        for (IModuleDefinition module : modules) {
            if (!module.isEnabled()) {
                continue;
            }
            try {
                enableModule(module);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void unloadModules() {
        if (loadedModules.isEmpty()) {
            throw new IllegalStateException("Modules not loaded");
        }
        final ArrayList<IModuleDefinition> iModuleDefinitions = new ArrayList<>(loadedModules.keySet());
        for (IModuleDefinition module : iModuleDefinitions) {
            try {
                disableModule(module);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        externalValues.clear();
    }

    public static void reloadModules() {
        unloadModules();
        loadModules();
    }

    private static Object tryGetModuleConstructor(Class<?> clazz) {
        final Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();

        try {
            final Class<?>[] parameterTypes = declaredConstructors[0].getParameterTypes();
            if (parameterTypes.length == 0) {
                return declaredConstructors[0].newInstance();
            } else {
                return declaredConstructors[0].newInstance(JavaPlugin.getPlugin(UnlimitedAdmin.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
        return null;
    }

    @Nullable
    private static IModule loadModule(IModuleDefinition module) {
        if (loadedModules.containsKey(module)) {
            throw new IllegalStateException("Module " + module.getName() + " already loaded");
        }
        String classPath = module.getClassPath();
        if (module.getClassPath().startsWith("*")) {
            classPath = "dev.crifurch.unlimitedadmin.modules" + classPath.substring(1);
        }
        try {
            Class<?> clazz = Class.forName(classPath);
            Object instance = tryGetModuleConstructor(clazz);
            if (instance == null) {
                throw new IllegalStateException("Module " + classPath + " doesn't have a valid constructor");
            }
            if (!(instance instanceof IModule)) {
                throw new IllegalStateException("Class " + classPath + " is not instance of " + IModule.class.getSimpleName());
            }
            IModule moduleInstance = (IModule) instance;
            loadedModules.put(module, moduleInstance);
            return moduleInstance;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void enableModule(IModuleDefinition module) {
        if (loadedModules.containsKey(module)) {
            throw new IllegalStateException("Module " + module.getName() + " already loaded");
        }
        final IModule iModule = loadModule(module);
        if (iModule == null) {
            return;
        }
        ((EnableStateProvider) iModule).onEnable();
        final Collection<ICommand> commands = iModule.getCommands();
        final UnlimitedAdmin unlimitedAdmin = JavaPlugin.getPlugin(UnlimitedAdmin.class);
        if (iModule instanceof RawModule) {
            if (iModule instanceof ICommandGroup) {
                final PluginCommand pluginCommand = CustomCommandsManager.getInstance().registerCommand(((ICommandGroup) iModule));
                pluginCommand.setExecutor(new CommandExecutor(unlimitedAdmin, (ICommandGroup) iModule));
            } else {
                for (ICommand command : commands) {
                    final PluginCommand pluginCommand = CustomCommandsManager.getInstance().registerCommand(command);
                    pluginCommand.setExecutor(new CommandExecutor(unlimitedAdmin, command));
                }
            }

        } else if (iModule instanceof AdminModule) {
            if (!commands.isEmpty() || iModule.addAsCommandIfCommandsEmpty()) {
                command.addCommand((ICommand) iModule);
            }
        } else {
            throw new IllegalStateException("Module " + module.getName() + " is not instance of "
                    + AdminModule.class.getSimpleName() + " or " + RawModule.class.getSimpleName());
        }
        final Collection<Listener> listeners = iModule.getListeners();
        for (Listener listener : listeners) {
            unlimitedAdmin.getServer().getPluginManager().registerEvents(listener, unlimitedAdmin);
        }

    }

    private static void disableModule(IModuleDefinition module) {
        final IModule iModule = loadedModules.get(module);
        if (iModule == null) {
            throw new IllegalStateException("Module " + module.getName() + " is not loaded");
        }
        loadedModules.remove(module);
        if (iModule instanceof RawModule) {
            if (iModule instanceof ICommandGroup) {
                CustomCommandsManager.getInstance().unregisterCommand(iModule.getName());
            } else {
                for (ICommand command : iModule.getCommands()) {
                    CustomCommandsManager.getInstance().unregisterCommand(command.getName());
                }
            }
        } else if (iModule instanceof AdminModule) {
            command.removeCommand((ICommand) iModule);
        }
        final Collection<Listener> listeners = iModule.getListeners();
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
        try {
            iModule.getCommands().clear();
        } catch (Exception e) {
            // ignore
        }
        try {
            iModule.getListeners().clear();
        } catch (Exception e) {
            // ignore
        }
        ((EnableStateProvider) iModule).onDisable();
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getClassPath() {
        return classPath;
    }

    @Override
    public boolean isEnabled() {
        return enabled.get();
    }

    @Override
    public @Nullable IModule getModule() {
        return loadedModules.get(this);
    }


}
