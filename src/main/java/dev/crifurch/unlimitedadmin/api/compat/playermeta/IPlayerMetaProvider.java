package dev.crifurch.unlimitedadmin.api.compat.playermeta;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Collection;

public interface IPlayerMetaProvider {
    @Nonnull
    Collection<String> getPrefixes(Player player);

    @Nonnull
    Collection<String> getSuffixes(Player player);

    @Nonnull
    String getNickname(Player player);

    @Nonnull
    String getDisplayName(Player player);
}
