package dev.crifurch.unlimitedadmin.modules.shop;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.api.providers.PluginFileProvider;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public enum ShopModuleConfig {
    SHOP_DONATE_URL("url", "https://api.capscraft.com/shop/donate/%s"),
    SHOP_DONATE_PAGE_URL("url_for_donate_page", "https://capscraft.com?donate=%s"),
    SHOP_NICKNAME_FIELD_KEY("nickname_field", "nickname"),
    SHOP_AMOUNT_FIELD_KEY("amount_field", "amount");

    private final String value;
    private final String path;
    private static YamlConfiguration cfg;
    private static final File f = PluginFileProvider.UnlimitedAdmin.getModuleConfigFile(ModulesManager.SHOP);

    ShopModuleConfig(String path, String value) {
        this.path = path;
        this.value = value;
    }
    public String getText(Object... args) {
        final String string = cfg.getString(path);
        if (string == null) {
            return value;
        }
        return String.format(string, args);
    }

    public String getText() {
        final String string = cfg.getString(path);
        if (string == null) {
            return value;
        }
        return string;
    }


    public static void load() {
        reload(false);
        save();
    }

    public static void save() {
        for (ShopModuleConfig c : values()) {
            if (!cfg.contains(c.path)) {
                c.set(c.value, false);
            }
        }
        try {
            cfg.save(f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void set(Object value, boolean save) {
        cfg.set(path, value);
        if (save) {
            try {
                cfg.save(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            reload(false);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void reload(boolean complete) {
        if (!complete) {
            f.getParentFile().mkdirs();
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cfg = YamlConfiguration.loadConfiguration(f);
            return;
        }
        load();
    }
}
