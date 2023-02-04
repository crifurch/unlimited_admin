package dev.crifurch.unlimitedadmin.modules.shop;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.providers.PluginFileProvider;
import dev.crifurch.unlimitedadmin.modules.shop.data.CommandTemplate;
import dev.crifurch.unlimitedadmin.modules.shop.data.CommandValueTemplate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CommandsShopConfig {

    private static final File f = PluginFileProvider.UnlimitedAdmin.getModuleFile(
            ModulesManager.SHOP, "donation_shop", "commands.yml");
    private static YamlConfiguration cfg;
    private static final List<CommandTemplate> templates = new ArrayList<>();

    public static void load() {
        reload(false);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void reload(boolean complete) {
        if (!complete) {
            f.getParentFile().mkdirs();
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cfg = YamlConfiguration.loadConfiguration(f);
            templates.clear();
            loadCommandsTemplate();
            return;
        }
        load();
    }

    public static List<String> getForTabCompletion(List<String> prevArg, int i) {
        final List<CommandTemplate> templates = getTemplates();

        templates.removeIf(template -> {
                    if (template.getLength() <= i) {
                        return true;
                    }
                    for (int j = 0; j < prevArg.size(); j++) {
                        final CommandValueTemplate template1 = template.getTemplate(j);
                        if (template1.equals(CommandValueTemplate.OTHER) && !template1.getValue().equals(prevArg.get(j))) {
                            return true;
                        }
                    }
                    return false;
                }
        );
        final List<CommandValueTemplate> collect = templates.stream().map(commandTemplate -> commandTemplate.getTemplate(i)).collect(Collectors.toList());
        ArrayList<String> result = new ArrayList<>();
        for (CommandValueTemplate it : collect) {
            if (it.equals(CommandValueTemplate.ONLINE_PLAYERS)) {
                result.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
            } else if (it.equals(CommandValueTemplate.PLAYER_NAME)) {
                result.addAll(UnlimitedAdmin.getInstance().getPlayersMapModule().getPlayers().stream().map(cachedPlayer -> cachedPlayer.name).collect(Collectors.toList()));
            } else {
                result.add(it.getValue());
            }
        }
        return result;
    }

    @Nullable
    public static CommandTemplate getCommandTemplate(List<String> args) {
        final List<CommandTemplate> templates = getTemplates();
        templates.removeIf(template -> {
                    if (template.getLength() != args.size()) {
                        return true;
                    }
                    for (int i = 0; i < template.getLength(); i++) {
                        final CommandValueTemplate template1 = template.getTemplate(i);
                        if (template1.equals(CommandValueTemplate.OTHER) && !template1.getValue().equals(args.get(i))) {
                            return true;
                        }
                    }
                    return false;
                }
        );
        if (templates.size() == 1) {
            return templates.get(0);
        }
        return null;
    }

    public static List<CommandTemplate> getTemplates() {
        return new ArrayList<>(templates);
    }

    private static void loadCommandsTemplate() {
        cfg.getKeys(false).forEach(key -> {
            UnlimitedAdmin.getInstance().getLogger().log(Level.WARNING, key);
            final ConfigurationSection command = cfg.getConfigurationSection(key);
            if (command == null) {
                return;
            }
            if (!command.contains("command") || !command.contains("price")) {
                return;
            }
            final String commandName = command.getString("command");
            final double price = command.getDouble("price");
            CommandTemplate.Mode mode = CommandTemplate.Mode.DONATE;
            if (command.contains("mode")) {
                String modes = command.getString("mode");
                assert modes != null;
                CommandTemplate.Mode.getMode(modes);
            }
            assert commandName != null;
            templates.add(new CommandTemplate(commandName, price, mode));
        });
    }

}
