package dev.crifurch.unlimitedadmin.modules.shop.commands;

import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.shop.CommandsShopConfig;
import dev.crifurch.unlimitedadmin.modules.shop.ShopModule;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ShopCommand implements ICommand {
    private final ShopModule module;

    public ShopCommand(ShopModule shopModule) {
        this.module = shopModule;
    }


    @Override
    public @NotNull String getName() {
        return "shop";
    }

    @Override
    public @Nullable List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        if (i == 0) {
            return Arrays.asList("command", "item");
        } else {
            if (args[0].equals("command")) {
                final List<String> prevArg = Arrays.asList(args).subList(1, Math.min(i, args.length));
                return CommandsShopConfig.getForTabCompletion(prevArg, i - 1);
            } else if (args[0].equals("item")) {
                //todo adds item when ready
                return null;
            }
        }
        return ICommand.super.getTabCompletion(sender, args, i);
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) {

    }
}
