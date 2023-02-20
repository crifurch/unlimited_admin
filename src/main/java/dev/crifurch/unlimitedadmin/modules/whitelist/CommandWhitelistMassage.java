package dev.crifurch.unlimitedadmin.modules.whitelist;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.crifurch.unlimitedadmin.modules.chat.ChatConfig;
import dev.crifurch.unlimitedadmin.utils.ColorUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandWhitelistMassage {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("whitelistmsg").requires(
                        (commandSourceStack) -> commandSourceStack.hasPermission(4)
                ).then(Commands.argument("msg", StringArgumentType.greedyString()).executes(context -> {
                    ChatConfig.SERVER.WHITELIST_MESSAGE.set(ColorUtils.replaceColorsSymbols(StringArgumentType.getString(context, "msg")));
                    ChatConfig.SERVER.WHITELIST_MESSAGE.save();
                    return 0;
                })))
        ;
    }
}
