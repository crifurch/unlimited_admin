package dev.crifurch.unlimitedadmin.api.providers;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.ModuleConfig;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModuleDefinition;
import dev.crifurch.unlimitedadmin.api.utils.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class PluginFileProvider {

    public static final PluginFileProvider UnlimitedAdmin = new PluginFileProvider(JavaPlugin.getProvidingPlugin(UnlimitedAdmin.class));
    private final JavaPlugin plugin;

    public PluginFileProvider(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    public File getModuleConfigFile(IModuleDefinition module) {
        return getModuleConfigFile(module, "config");
    }

    public File getModuleConfigFile(IModuleDefinition module, String name) {
        return getModuleFile(module, name + ".yml");
    }

    public File getModuleFile(IModuleDefinition module, String... path) {
        ArrayList<String> folders = new ArrayList<>();
        folders.add(module.getName());
        folders.addAll(Arrays.asList(path));
        return FileUtils.getFileFromList(getModuleFolder(module), folders.toArray(new String[0]));
    }

    public File getModuleFolder(IModuleDefinition module, String... subFolders) {
        ArrayList<String> folders = new ArrayList<>();
        folders.add(module.getName());
        folders.addAll(Arrays.asList(subFolders));
        return FileUtils.getFolderFromList(plugin.getDataFolder(), folders.toArray(new String[0]));
    }

    public ModuleConfig getModuleConfig(IModuleDefinition module) {
        return new ModuleConfig(getModuleConfigFile(module));
    }

    public File getPluginFolder() {
        return plugin.getDataFolder();
    }

    public File getPluginConfigFile(String name) {
        return FileUtils.getFileFromList(plugin.getDataFolder(), name + ".yml");
    }

    public File getPluginConfigFile() {
        return getPluginFile("config");
    }


    public File getPluginFile(String... path) {
        return FileUtils.getFileFromList(plugin.getDataFolder(), path);
    }

    public File getWorldsFolder() {
        return plugin.getServer().getWorldContainer();
    }

    public File getServerFolder() {
        return new File(".");
    }
}
