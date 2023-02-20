package dev.crifurch.unlimitedadmin.api.compat.playermeta;

import dev.crifurch.unlimitedadmin.UnlimitedAdminForge;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PlayerMetaProvider implements IPlayerMetaProvider {
    private static final String[] knownProviders = new String[]{
            "dev.crifurch.unlimitedadmin.common.compat.playermeta.LuckyPermsPlayerMetaProvider",
            "dev.crifurch.unlimitedadmin.common.compat.playermeta.ForgePlayerMetaProvider",
            "dev.crifurch.unlimitedadmin.api.compat.playermeta.DummyPlayerMetaProvider",
    };

    private IPlayerMetaProvider playerMetaProvider;

    public PlayerMetaProvider() {
        int i = 0;
        while (i < knownProviders.length && playerMetaProvider == null) {
            try {
                playerMetaProvider = tryLoadProvider(knownProviders[i]);
            } catch (Throwable ignored) {
                UnlimitedAdminForge.LOGGER.error("Failed to load PlayerMetaProvider: " + knownProviders[i] + " (Throwable)" + ignored.getMessage());
            }
            i++;
        }
        if (playerMetaProvider == null)
            throw new IllegalStateException("Could not find a valid PlayerMetaProvider");
        else
            UnlimitedAdminForge.LOGGER.info("Loaded PlayerMetaProvider: " + playerMetaProvider.getClass().getName());
    }

    @Nullable
    private IPlayerMetaProvider tryLoadProvider(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return (IPlayerMetaProvider) clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            UnlimitedAdminForge.LOGGER.error("Failed to load PlayerMetaProvider: " + className);
            return null;
        }
    }

    @NotNull
    @Override
    public Collection<String> getPrefixes(Player player) {
        return playerMetaProvider.getPrefixes(player);
    }

    @NotNull
    @Override
    public Collection<String> getSuffixes(Player player) {
        return playerMetaProvider.getPrefixes(player);
    }

    @NotNull
    @Override
    public String getNickname(Player player) {
        return playerMetaProvider.getNickname(player);
    }

    @NotNull
    @Override
    public String getDisplayName(Player player) {
        return playerMetaProvider.getDisplayName(player);
    }

    public void setPlayerMetaProvider(@NotNull IPlayerMetaProvider playerMetaProvider) {
        if (playerMetaProvider == this)
            throw new IllegalArgumentException("PlayerMetaProvider cannot be set to itself");

        this.playerMetaProvider = playerMetaProvider;
    }
}
