package dev.crifurch.unlimitedadmin.modules.stop.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

import java.lang.reflect.Field;

public class CommandStop {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
                dispatcher.register(
                Commands.literal("stop").then(
                        Commands.argument("seconds", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                ).executes(context -> {
                    if(!context.getSource().hasPermission(4)) {
                        context.getSource().sendSuccess(new TextComponent("Эта команда доступна только для админа"), false);
                        return 0;
                    }
            int seconds = 60;
            if (context.getInput().split(" ").length > 1) {
                seconds = Integer.parseInt(context.getInput().split(" ")[1]);
            };
            StopServerModule stopServerModule = new StopServerModule(context.getSource().getServer());
            stopServerModule.stop(seconds);
            return 1;
        }));
    }

}
