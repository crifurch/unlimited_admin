package dev.crifurch.unlimitedadmin.modules.chat.commands.privatemessages;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.utils.CommandArguments;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.PlayerMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.channels.PrivateMessageChatChannel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class AnswerCommand implements ICommand {
    private final ChatModule chatModule;

    public AnswerCommand(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public @NotNull String getName() {
        return "answer";
    }

    @Override
    public @Nullable List<String> getAliases() {
        return Arrays.asList("a", "r", "reply");
    }

    @Override
    public byte getMinArgsSize() {
        return 1;
    }

    @Override
    public void onCommand(CommandSender sender, CommandArguments args) throws NotifibleException {
        assertSenderIsPlayer(sender);
        Player player = (Player) sender;
        if (args.count() > 0) {
            String message = String.join(" ", args);
            final PlayerMessageSender forAnswer = chatModule.getForAnswer(player);
            if (forAnswer == null) {
                throw new NotifibleException(LangConfig.CHAT_NO_ONE_TO_ANSWER.getText());
            }
            chatModule.broadcastMessage(ChatMessageSender.fromSender(sender), PrivateMessageChatChannel.prepareMessage(message, forAnswer.getName()));
        }
    }


}