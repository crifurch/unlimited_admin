package dev.crifurch.unlimitedadmin.modules.maintain.commands;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandNotEnoughArgsException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.maintain.MaintainModule;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MaintainModeCommand implements ICommand {
    final MaintainModule module;

    public MaintainModeCommand(MaintainModule module) {
        this.module = module;
    }

    @Override
    public String getUsageText() {
        return ICommand.super.getUsageText() + " <value>";
    }

    @Override
    public @NotNull String getName() {
        return "mode";
    }

    @Override
    public byte getMaxArgsSize() {
        return 1;
    }

    @Override
    public byte getMinArgsSize() {
        return 1;
    }

    @Override
    public @Nullable List<String> getTabCompletion(CommandSender sender, String[] args, int i) {
        return Arrays.asList("1", "0", "true", "false");
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws CommandNotEnoughArgsException {
        final boolean aTrue = Boolean.TRUE.equals(args.get(0, Boolean.class));
        module.setMaintainMode(aTrue);
        if (aTrue) {
            sender.sendMessage(LangConfig.SERVER_IN_MAINTAIN_MODE.getText());
        } else {
            sender.sendMessage(LangConfig.SERVER_NOT_IN_MAINTAIN_MODE.getText());
        }
    }
}
