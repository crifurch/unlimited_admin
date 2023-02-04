package dev.crifurch.unlimitedadmin.modules.teleporting.commands;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandErrorException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.api.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TpPosCommand implements ICommand {
    private final UnlimitedAdmin plugin;


    public TpPosCommand(UnlimitedAdmin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsageText() {
        return ICommand.super.getUsageText() + " <x> <y> <z> [world|current] [player]";
    }

    @Override
    public @Nullable List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        if (i < 3) {
            List<String> result = new ArrayList<>();
            for (int it = i; it < 3; it++) {
                StringBuilder s = new StringBuilder();
                for (int k = 0; k < 3 - it; k++) {
                    s.append("~ ");
                }
                result.add(s.toString());
            }
            return result;
        }
        if (i == 3) {
            final List<String> collect = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
            collect.add(0, "current");
            return collect;
        }

        return ICommand.super.getTabCompletion(sender, args, i);
    }

    @Override
    public byte getMaxArgsSize() {
        return 5;
    }

    public byte getMinArgsSize() {
        return 3;
    }

    @Override
    public @NotNull String getName() {
        return "tppos";
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        UUID targetPlayer = null;
        if (args.count() > 4) {
            assertOtherPermission(sender);
            targetPlayer = plugin.getPlayersMapModule().getPlayerUUID(args.get(4));
            if (targetPlayer == null) {
                throw new CommandErrorException(LangConfig.NO_SUCH_PLAYER.getText(args.get(4)));
            }
        }
        if (targetPlayer == null) {
            assertSenderIsPlayer(sender);
            targetPlayer = ((Player) sender).getUniqueId();
        }

        final Location location = PlayerUtils.getLocation(targetPlayer);
        try {
            assert location != null;
            location.setX(mapPos(location.getX(), args.get(0)));
            location.setY(mapPos(location.getY(), args.get(1)));
            location.setZ(mapPos(location.getZ(), args.get(2)));
            if (args.count() > 3) {
                if (!args.get(3).equals("current")) {
                    final ArrayList<World> worlds = new ArrayList<>(Bukkit.getWorlds());
                    worlds.removeIf(world -> !world.getName().equals(args.get(3)));
                    if (worlds.isEmpty()) {
                        throw new CommandErrorException(LangConfig.NO_SUCH_WORLD.getText(args.get(3)));
                    }
                    location.setWorld(worlds.get(0));
                }
            }
        } catch (Exception e) {
            throw new CommandErrorException();
        }

        if (!PlayerUtils.setLocation(targetPlayer, location)) {
            throw new CommandErrorException();
        }
    }

    private double mapPos(double coordinate, String add) {
        if (add.startsWith("~")) {
            if (add.length() == 1) {
                return coordinate;
            }
            try {
                return coordinate + Double.parseDouble(add.substring(1));
            } catch (Exception e) {
                return coordinate + Integer.parseInt(add.substring(1));
            }
        }
        try {
            return Double.parseDouble(add);
        } catch (Exception e) {
            return Integer.parseInt(add);
        }
    }
}
