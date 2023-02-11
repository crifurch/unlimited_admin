package dev.crifurch.unlimitedadmin.modules.stop.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandStop {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("serverstop").requires(
                                (commandSourceStack) -> commandSourceStack.hasPermission(4)
                        ).executes(CommandStop::stop)
                        .then(Commands.argument("seconds", IntegerArgumentType.integer(0)).executes(CommandStop::stop))
                        .then(Commands.argument("seconds", IntegerArgumentType.integer(0))
                                .then(Commands.argument("reason", StringArgumentType.greedyString()).executes(CommandStop::stop))))
        ;
    }

    private static int stop(CommandContext<CommandSourceStack> context) {
        int seconds = 60;
        final String[] s = context.getInput().split(" ");
        if (s.length > 1) {
            seconds = Integer.parseInt(s[1]);
        }
        String reason = "Перезапуск сервера, скоро вернемся!";
        if (s.length > 2) {
            reason = Arrays.stream(s).skip(2).collect(Collectors.joining(" "));
        }

        StopServerModule stopServerModule = new StopServerModule(context.getSource().getServer());
        stopServerModule.stop(seconds, reason);
        return 0;
    }
}


//dispatcher.register(
//        Commands.literal("serverstop").requires((p_138665_) ->
//        p_138665_.hasPermission(4)
//        ).then(
//        Commands.argument("seconds", IntegerArgumentType.integer(0, 10000)))
//        .executes(context -> {
//        int seconds = 60;
//final String[] s = context.getInput().split(" ");
//        if (s.length > 1) {
//        seconds = Integer.parseInt(s[1]);
//        }
//        String reason = "Перезапуск сервера, скоро вернемся!";
//        if (s.length > 2) {
//        reason = Arrays.stream(s).skip(2).collect(Collectors.joining(" "));
//        }
//
//        StopServerModule stopServerModule = new StopServerModule(context.getSource().getServer());
//        stopServerModule.stop(seconds, reason);
//        return 0;
//        }));
