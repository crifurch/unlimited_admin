package dev.crifurch.unlimitedadmin.modules.shop.data;

import dev.crifurch.unlimitedadmin.modules.shop.ShopModuleConfig;

import java.time.LocalDateTime;
import java.util.Map;

public class PlayerDonationCache {
    private double amount;
    private LocalDateTime cacheDate;
    private String playerName;

    public PlayerDonationCache(String playerName, double amount, LocalDateTime cacheDate) {
        this.playerName = playerName;
        this.amount = amount;
        this.cacheDate = cacheDate;
    }

    public void fromJson(Map<String, Object> json) {
        this.playerName = (String) json.get(ShopModuleConfig.SHOP_NICKNAME_FIELD_KEY.getText());
        this.amount = (double) json.get(ShopModuleConfig.SHOP_AMOUNT_FIELD_KEY.getText());
        this.cacheDate = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(cacheDate.plusMinutes(10));
    }

    public double getAmount() {
        return amount;
    }

    public String getPlayerName() {
        return playerName;
    }
}
