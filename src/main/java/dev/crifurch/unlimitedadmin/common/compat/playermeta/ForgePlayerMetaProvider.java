package dev.crifurch.unlimitedadmin.common.compat.playermeta;

import dev.crifurch.unlimitedadmin.api.compat.playermeta.IPlayerMetaProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public class ForgePlayerMetaProvider implements IPlayerMetaProvider {
    @Override
    public @NotNull Collection<String> getPrefixes(Player player) {
        return player.getPrefixes().stream().map(Component::getString).collect(Collectors.toList());
    }

    @Override
    public @NotNull Collection<String> getSuffixes(Player player) {
        return player.getSuffixes().stream().map(Component::getString).collect(Collectors.toList());
    }

    @Override
    public @NotNull String getNickname(Player player) {
        if (player.getCustomName() != null)
            return player.getCustomName().getString();

        return player.getName().getString();
    }

    @Override
    public @NotNull String getDisplayName(Player player) {
        return player.getDisplayName().getString();
    }
}
