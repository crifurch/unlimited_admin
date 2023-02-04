package dev.crifurch.unlimitedadmin.modules.shop;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModuleDefinition;
import dev.crifurch.unlimitedadmin.api.modules.RawModule;
import dev.crifurch.unlimitedadmin.modules.shop.commands.DonateCommand;
import dev.crifurch.unlimitedadmin.modules.shop.commands.ShopCommand;
import dev.crifurch.unlimitedadmin.modules.shop.data.DonationRepository;
import dev.crifurch.unlimitedadmin.modules.shop.data.PlayerDonationCache;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShopModule extends RawModule {
    private final List<ICommand> commands = new ArrayList<>();
    private final List<PlayerDonationCache> donationCaches = new ArrayList<>();

    @Override
    public @NotNull IModuleDefinition getDefinition() {
        return ModulesManager.SHOP;
    }


    @Override
    public void onEnable() {
        ShopModuleConfig.load();
        CommandsShopConfig.load();
        commands.add(new ShopCommand(this));
        commands.add(new DonateCommand(this));
    }

    @Override
    public void onDisable() {

    }

    @Override
    public @NotNull List<ICommand> getCommands() {
        return commands;
    }

    public PlayerDonationCache getDonationCache(Player player) {
        for (PlayerDonationCache cache : donationCaches) {
            if (cache.getPlayerName().equals(player.getName()) && !cache.isExpired()) {
                return cache;
            }
        }
        final PlayerDonationCache playerDonationAmount = DonationRepository.getPlayerDonationAmount(player.getName());
        donationCaches.add(playerDonationAmount);
        return playerDonationAmount;
    }

}
