package dev.crifurch.unlimitedadmin.modules.chat.implementations.firewalls;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.permissions.UnlimitedAdminPermissionsList;
import dev.crifurch.unlimitedadmin.integrations.permissions.PermissionStatus;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.data.Mute;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.utilchannels.FirewallChatChannel;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.IChatChanel;

public class MuteFirewall extends FirewallChatChannel {

    public MuteFirewall(IChatChanel child, ChatModule chatModule) {
        super(chatModule, child);
    }

    @Override
    public boolean isBlocked(ChatMessageSender sender, String message) {
        final Mute mute = getModule().getMute(sender.getUUID());
        if (mute == null) {
            return false;
        }
        return !(!mute.isMuted(getName()) ||
                sender.getPermissionStatus(UnlimitedAdminPermissionsList.CHAT_MUTE_BYPASS)
                        == PermissionStatus.PERMISSION_TRUE);
    }

    @Override
    public String getBlockedMessage(ChatMessageSender sender, String message) {
        return LangConfig.CHAT_MUTED_IN_CHANNEL.getText(getName());
    }
}
