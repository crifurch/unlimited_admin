package dev.crifurch.unlimitedadmin.modules.chat.implementations.firewalls;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.permissions.UnlimitedAdminPermissionsList;
import dev.crifurch.unlimitedadmin.integrations.permissions.PermissionStatus;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModuleConfig;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.utilchannels.FirewallChatChannel;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.IChatChanel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdsFirewall extends FirewallChatChannel {
    private static final Pattern URL_PATTER = Pattern.compile("[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");

    public AdsFirewall(IChatChanel child, ChatModule chatModule) {
        super(chatModule, child);
    }

    @Override
    public boolean isBlocked(ChatMessageSender sender, String message) {
        final List<String> stringList1 = ChatModuleConfig.ADS_CHANNELS.getStringList();
        if (!(stringList1.contains("*") || stringList1.contains(getName()))) {
            return false;
        }
        if (sender.getPermissionStatus(UnlimitedAdminPermissionsList.CHAT_ADS_BYPASS) == PermissionStatus.PERMISSION_TRUE) {
            return false;
        }
        final Matcher matcher = URL_PATTER.matcher(message);
        if (!matcher.find()) {
            return false;
        }
        final String url = matcher.group();
        final List<String> stringList = ChatModuleConfig.ADS_DOMAINS_EXCEPT.getStringList();
        for (String s : stringList) {
            if (url.startsWith(s)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getBlockedMessage(ChatMessageSender sender, String message) {
        return LangConfig.CHAT_ADS_DETECTED.getText();
    }
}
