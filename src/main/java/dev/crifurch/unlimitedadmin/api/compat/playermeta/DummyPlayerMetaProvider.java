package dev.crifurch.unlimitedadmin.api.compat.playermeta;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class DummyPlayerMetaProvider implements IPlayerMetaProvider{
    @NotNull
    @Override
    public Collection<String> getPrefixes(Player player) {
        throw new IllegalStateException("DummyPlayerMetaProvider should never be used");
    }

    @NotNull
    @Override
    public Collection<String> getSuffixes(Player player) {
        throw new IllegalStateException("DummyPlayerMetaProvider should never be used");
    }

    @NotNull
    @Override
    public String getNickname(Player player) {
        throw new IllegalStateException("DummyPlayerMetaProvider should never be used");
    }

    @NotNull
    @Override
    public String getDisplayName(Player player) {
        throw new IllegalStateException("DummyPlayerMetaProvider should never be used");
    }
}
