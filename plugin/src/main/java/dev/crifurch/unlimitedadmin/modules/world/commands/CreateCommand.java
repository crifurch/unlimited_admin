package dev.crifurch.unlimitedadmin.modules.world.commands;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandErrorException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.world.WorldsModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public class CreateCommand implements ICommand {
    private final WorldsModule manager;
    private static final Map<String, World.Environment> supportedEnvironment;
    private boolean isBusy = false;

    static {
        supportedEnvironment = new HashMap<>();
        supportedEnvironment.put("NORMAL", World.Environment.NORMAL);
        supportedEnvironment.put("END", World.Environment.THE_END);
        supportedEnvironment.put("NETHER", World.Environment.NETHER);
    }

    @Override
    public byte getMinArgsSize() {
        return 2;
    }

    @Override
    public String getUsageText() {
        return ("Usage /una " + manager.getName() + " create <name> <NORMAL|END|NETHER> ");
    }

    public CreateCommand(WorldsModule manager) {
        this.manager = manager;
    }

    @Override
    public @NotNull String getName() {
        return "create";
    }

    @Override
    public List<String> getPermissions() {
        return null;
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        if (isBusy) {
            throw new CommandErrorException(LangConfig.WORLD_CREATION_BUSY.getText());
        }
        final String envString = args.get(1).toUpperCase();
        World.Environment env = supportedEnvironment.get(envString);
        if (env == null) {
            throw new CommandErrorException(LangConfig.WORLD_UNSUPPORTED_ENVIRONMENT.getText(envString));
        }

        isBusy = true;
        try {
            final String error = manager.createWorld(args.get(0).toLowerCase(Locale.ROOT), env, WorldType.NORMAL);
            if (error != null) {
                throw new CommandErrorException(error);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, e.toString());
            isBusy = false;
            throw new CommandErrorException(LangConfig.WORLD_CREATION_ERROR.getText(args.get(0)));
        }
        sender.sendMessage(LangConfig.WORLD_CREATED.getText(args.get(0)));
    }
}
