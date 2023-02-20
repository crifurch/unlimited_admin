package dev.crifurch.unlimitedadmin.common.compat.playermeta;

import dev.crifurch.unlimitedadmin.api.compat.playermeta.IPlayerMetaProvider;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LuckyPermsPlayerMetaProvider implements IPlayerMetaProvider {
    private LuckPerms perms;

    @Override
    public @NotNull Collection<String> getPrefixes(Player player) {
        ensureInitialized();
        try {
            User usr = perms.getUserManager().getUser(player.getUUID());
            if (usr == null) return Collections.emptyList();
            CachedMetaData mdat = usr.getCachedData().getMetaData();
            return mdat.getPrefixes().values();
        } catch (IllegalStateException ise) {
            return Collections.emptyList();
        }
    }

    @Override
    public @NotNull Collection<String> getSuffixes(Player player) {
        ensureInitialized();
        try {
            User usr = perms.getUserManager().getUser(player.getUUID());
            if (usr == null) return Collections.emptyList();
            CachedMetaData mdat = usr.getCachedData().getMetaData();
            return mdat.getSuffixes().values();
        } catch (IllegalStateException ise) {
            return Collections.emptyList();
        }
    }

    @Override
    public @NotNull String getNickname(Player player) {
        return player.getName().getString();
    }

    @Override
    public @NotNull String getDisplayName(Player player) {
        ensureInitialized();
        ArrayList<String> parts = new ArrayList<>(getPrefixes(player));
        parts.add(getNickname(player));
        parts.addAll(getSuffixes(player));
        return String.join("", parts);
    }

    private void ensureInitialized() {
        if (perms == null) {
            perms = LuckPermsProvider.get();
        }
    }
}
