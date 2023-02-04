package dev.crifurch.unlimitedadmin.modules.antiop;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModuleDefinition;
import dev.crifurch.unlimitedadmin.api.modules.RawModule;
import dev.crifurch.unlimitedadmin.api.providers.PluginFileProvider;
import dev.crifurch.unlimitedadmin.api.providers.ServerDataProvider;
import dev.crifurch.unlimitedadmin.api.utils.PlaceHolderUtils;
import dev.crifurch.unlimitedadmin.modules.antiop.commands.AntiOPGroupCommand;
import dev.crifurch.unlimitedadmin.modules.antiop.commands.AntiOpDeopCommand;
import dev.crifurch.unlimitedadmin.modules.antiop.commands.AntiOpOpCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AntiOPModule extends RawModule implements Listener {
    final UnlimitedAdmin plugin;
    private File logFile;

    private Collection<ICommand> commands;

    public AntiOPModule(UnlimitedAdmin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull IModuleDefinition getDefinition() {
        return ModulesManager.ANTIOP;
    }


    @Override
    public void onEnable() {
        AntiOPConfig.load();
        commands = Arrays.asList(
                new AntiOPGroupCommand(this),
                new AntiOpDeopCommand(),
                new AntiOpOpCommand()
        );
    }

    @Override
    public void onDisable() {
    }

    @Override
    public @NotNull Collection<ICommand> getCommands() {
        return commands;
    }

    @Override
    public Collection<Listener> getListeners() {
        return Collections.singletonList(this);
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        checkOps(event.getPlayer());
    }

    public void checkOps(Player... players) {
        for (Player player : players) {
            if (player.isOp()) {
                final List<String> stringList = AntiOPConfig.OP_LIST.getStringList();
                if (!(stringList.contains(player.getName())
                        || stringList.contains(player.getUniqueId().toString()))) {
                    deOP(player);
                }
            }
        }
    }

    private void deOP(Player player) {
        if (player.isOp()) {
            ServerDataProvider.setOP(player.getName(), false);
        }
        if (AntiOPConfig.LOG.getBoolean()) {
            logOp(player);
        }
        final int delay = AntiOPConfig.DELAY_BEFORE_COMMANDS.getInt();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (String command : AntiOPConfig.ADDITIONAL_RUN_COMMANDS.getStringList()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceHolderUtils.replacePlayerPlaceholders(command, player));
            }
        }, delay * 20L);

    }

    private void logOp(Player player) {
        if (logFile == null) {
            logFile = PluginFileProvider.UnlimitedAdmin.getModuleFile(getDefinition(), "antiop.log");
        }
        try {
            final OutputStreamWriter out = new OutputStreamWriter(Files.newOutputStream(logFile.toPath(), StandardOpenOption.APPEND), StandardCharsets.UTF_8);
            BufferedWriter output = new BufferedWriter(out);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("[dd/MM/yyyy HH:mm:ss]");
            LocalDateTime now = LocalDateTime.now();
            output.append(dtf.format(now))
                    .append(" ")
                    .append(player.getName())
                    .append(" tried to be op")
                    .append(System.lineSeparator());
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}