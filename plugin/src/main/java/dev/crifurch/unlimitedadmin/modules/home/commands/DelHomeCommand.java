package dev.crifurch.unlimitedadmin.modules.home.commands;

import dev.crifurch.unlimitedadmin.api.GlobalConstants;
import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.exceptions.command.CommandErrorException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.home.HomeModule;
import dev.crifurch.unlimitedadmin.modules.home.data.Home;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class DelHomeCommand implements ICommand {
    final HomeModule module;

    public DelHomeCommand(HomeModule module) {
        this.module = module;
    }

    @Override
    public byte getMaxArgsSize() {
        return 1;
    }

    @Override
    public @NotNull String getName() {
        return "delhome";
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        assertSenderIsPlayer(sender);
        String name = GlobalConstants.defaultEntryName;
        if (args.count() > 0) {
            name = args.get(0);
        }
        name = module.parseHomeName((Player) sender, name);
        if (name == null) {
            throw new CommandErrorException(LangConfig.NO_SUCH_HOME.getText());
        }
        final List<Home> homes = module.getOwnerHomes(((Player) sender).getUniqueId());
        String finalName = name.replace(':', '@');
        final Home home = homes.stream().filter(h ->
                finalName.equals(h.getId())
        ).findFirst().orElse(null);
        if (home == null) {
            throw new CommandErrorException(LangConfig.NO_SUCH_HOME.getText(name.split(":")[1]));
        }

        final boolean b = module.deleteHome(UUID.fromString(home.getUUID()), home.getName());
        if (!b) {
            throw new CommandErrorException();
        }
        sender.sendMessage(LangConfig.HOME_DELETED.getText(home.getName()));
    }
}
