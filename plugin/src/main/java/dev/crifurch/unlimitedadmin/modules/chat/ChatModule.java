package dev.crifurch.unlimitedadmin.modules.chat;

import dev.crifurch.unlimitedadmin.ModulesManager;
import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;
import dev.crifurch.unlimitedadmin.api.interfaces.ICommand;
import dev.crifurch.unlimitedadmin.api.interfaces.module.IModuleDefinition;
import dev.crifurch.unlimitedadmin.api.modules.RawModule;
import dev.crifurch.unlimitedadmin.api.permissions.UnlimitedAdminPermissionsList;
import dev.crifurch.unlimitedadmin.api.utils.PlaceHolderUtils;
import dev.crifurch.unlimitedadmin.integrations.permissions.PermissionStatus;
import dev.crifurch.unlimitedadmin.modules.chat.commands.MuteCommand;
import dev.crifurch.unlimitedadmin.modules.chat.commands.UnmuteCommand;
import dev.crifurch.unlimitedadmin.modules.chat.commands.notifications.*;
import dev.crifurch.unlimitedadmin.modules.chat.commands.privatemessages.AnswerCommand;
import dev.crifurch.unlimitedadmin.modules.chat.commands.privatemessages.MsgCommand;
import dev.crifurch.unlimitedadmin.modules.chat.commands.say.SayCommand;
import dev.crifurch.unlimitedadmin.modules.chat.commands.say.SayLaterCommand;
import dev.crifurch.unlimitedadmin.modules.chat.data.Ignore;
import dev.crifurch.unlimitedadmin.modules.chat.data.Mute;
import dev.crifurch.unlimitedadmin.modules.chat.data.Notification;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ConsoleMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.PlayerMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.channels.GlobalChatChannel;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.channels.LocalChatChannel;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.channels.NotificationsChatChannel;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.channels.PrivateMessageChatChannel;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.firewalls.AdsFirewall;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.firewalls.BadWordFirewall;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.firewalls.MuteFirewall;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.utilchannels.DublicateChildWrapper;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.utilchannels.FirewallChatChannel;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.utilchannels.LogChatChannel;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.utilchannels.SpyChatChannel;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.IChatChanel;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.ILoggedChat;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.ISpiedChat;
import dev.crifurch.unlimitedadmin.modules.chat.listeners.ChatMessageListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

//todo custom channels
public class ChatModule extends RawModule {


    final UnlimitedAdmin plugin;

    private final List<ICommand> commands = new ArrayList<>();
    private final List<Listener> listeners = new ArrayList<>();
    private final List<IChatChanel> chatChannels = new ArrayList<>();
    private SpyChatChannel spyChatChannel;
    private LogChatChannel logChatChannel;
    private final Map<String, Notification> adsNotifications = new HashMap<>();
    private final Map<Player, PlayerMessageSender> answerMap = new HashMap<>();

    public ChatModule(@NotNull UnlimitedAdmin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull IModuleDefinition getDefinition() {
        return ModulesManager.CHAT;
    }

    @Override
    public void onEnable() {
        ChatModuleConfig.init(this);
        ChatMuteConfig.init(this);
        listeners.add(new ChatMessageListener(this));
        if (ChatModuleConfig.NOTIFICATIONS_ENABLED.getBoolean()) {
            commands.add(new CancelNotificationCommand(this));
            commands.add(new NotificationsListCommand(this));
            commands.add(new AddNotificationCommand(this));
            commands.add(new SaveNotificationCommand(this));
            commands.add(new DeleteNotificationCommand(this));
            commands.add(new NotificationInfoCommand(this));
            loadNotifications();
        }

        if (ChatModuleConfig.IS_SPY_CHAT_ENABLED.getBoolean()) {
            spyChatChannel = new SpyChatChannel(this);
        }
        if (ChatModuleConfig.IS_LOG_CHAT_ENABLED.getBoolean()) {
            logChatChannel = new LogChatChannel(this);
        }
        addChatChannel(new NotificationsChatChannel(this));

        if (ChatModuleConfig.IS_GLOBAL_CHAT_ENABLED.getBoolean()) {
            addChatChannel(new GlobalChatChannel(this));
        }
        if (ChatModuleConfig.IS_LOCAL_CHAT_ENABLED.getBoolean()) {
            addChatChannel(new LocalChatChannel(this));
        }
        if (ChatModuleConfig.IS_PRIVATE_CHAT_ENABLED.getBoolean()) {
            addChatChannel(new PrivateMessageChatChannel(this));
            commands.add(new MsgCommand(this));
            commands.add(new AnswerCommand(this));
        }

        if (ChatModuleConfig.BAD_WORDS_ENABLED.getBoolean()) {
            BadWordFirewall.loadBadWords(this);
        }

        commands.add(new SayCommand(this));
        commands.add(new SayLaterCommand(this));
        commands.add(new MuteCommand(this));
        commands.add(new UnmuteCommand(this));
    }

    @Override
    public void onDisable() {
        BadWordFirewall.unloadBadWords();
        chatChannels.clear();
        spyChatChannel = null;
        logChatChannel = null;
        for (Notification notification : adsNotifications.values()) {
            notification.stop();
        }
        adsNotifications.clear();
        answerMap.clear();
    }

    @Override
    public Collection<Listener> getListeners() {
        return listeners;
    }

    public static IChatChanel unwrapChannel(IChatChanel chatChanel) {
        if (chatChanel instanceof DublicateChildWrapper) {
            return unwrapChannel(((DublicateChildWrapper) chatChanel).getChild());
        }
        if (chatChanel instanceof FirewallChatChannel) {
            return unwrapChannel(((FirewallChatChannel) chatChanel).getChild());
        }
        return chatChanel;
    }

    public void addChatChannel(IChatChanel chatChannel) {
        IChatChanel toAdd = chatChannel;
        toAdd = new MuteFirewall(toAdd, this);

        if (ChatModuleConfig.ADS_ENABLED.getBoolean()) {
            toAdd = new AdsFirewall(toAdd, this);
        }
        if (ChatModuleConfig.BAD_WORDS_ENABLED.getBoolean()) {
            toAdd = new BadWordFirewall(toAdd, this);
        }
        if (unwrapChannel(toAdd) instanceof ISpiedChat && spyChatChannel != null) {
            toAdd = new DublicateChildWrapper(toAdd, spyChatChannel, this);
        }
        if (unwrapChannel(toAdd) instanceof ILoggedChat && logChatChannel != null) {
            toAdd = new DublicateChildWrapper(toAdd, logChatChannel, this);
        }
        chatChannels.add(toAdd);
        chatChannels.sort(Comparator.comparingInt(o -> {
            if (o.getChannelPrefix() == null) return 0;
            return -o.getChannelPrefix().length();
        }));
    }

    @Override
    public @NotNull List<ICommand> getCommands() {
        return commands;
    }

    public void broadcastMessage(@Nullable ChatMessageSender sender, String message) {
        String messageToSend = message;
        for (IChatChanel chatChannel : chatChannels) {
            String channelPrefix = chatChannel.getChannelPrefix();
            IChatChanel toSend = null;
            if (channelPrefix != null && message.startsWith(channelPrefix)) {
                messageToSend = message.substring(channelPrefix.length());
                if (messageToSend.trim().isEmpty()) {
                    return;
                }
                toSend = chatChannel;
            }
            if (channelPrefix == null) {
                toSend = chatChannel;
            }
            if (toSend != null) {
                if (sender == null) {
                    sender = new ConsoleMessageSender(null);
                }
                final String broadcast = toSend.broadcast(sender, messageToSend, null);
                if (broadcast != null) {
                    sender.sendMessage(
                            PlaceHolderUtils.replacePlayerPlaceholders(broadcast)
                    );
                }
                return;
            }
        }
    }

    public List<String> getChannelsList() {
        List<String> list = new ArrayList<>();
        for (IChatChanel chatChannel : chatChannels) {
            String channelPrefix = chatChannel.getName();
            list.add(channelPrefix);
        }
        return list;
    }

    public void addForAnswer(ChatMessageSender sender, Player receiver) {
        if (sender instanceof PlayerMessageSender) {
            answerMap.put(receiver, (PlayerMessageSender) sender);
        } else {
            answerMap.remove(receiver);
        }
    }

    @Nullable
    public PlayerMessageSender getForAnswer(Player receiver) {
        return answerMap.get(receiver);
    }

    public Collection<String> getNotificationNames() {
        return adsNotifications.keySet();
    }

    public boolean addNotification(String name, Notification notification) {
        if (adsNotifications.containsKey(name)) {
            return false;
        }
        adsNotifications.put(name, notification);
        return true;
    }

    public boolean addCyclicNotification(String name, String message, int time) {
        return addCyclicNotification(name, message, time, null);
    }

    public boolean addCyclicNotification(String name, String message, int time, Consumer<String> onMessage) {
        final Notification value = new Notification(this, message, time, onMessage);
        if (!addNotification(name, value)) {
            return false;
        }
        value.start();
        return true;
    }

    public boolean addDelayedNotification(String name, String message, int time) {
        return addDelayedNotification(name, message, time, null);
    }

    public boolean addDelayedNotification(String name, String message, int time, Consumer<String> onMessage) {
        final Notification value = new Notification(this, message, time, s -> {
            cancelNotification(name);
            if (onMessage != null) {
                onMessage.accept(s);
            }
        });
        if (!addNotification(name, value)) {
            return false;
        }
        value.schedule();
        return true;
    }

    private void loadNotifications() {
        try {
            final ConfigurationSection section = ChatModuleConfig.NOTIFICATION_MESSAGES.getSection();
            if (section == null) return;
            for (String key : section.getKeys(false)) {
                try {
                    final ConfigurationSection notificationSection = section.getConfigurationSection(key);
                    if (notificationSection == null) {
                        continue;
                    }
                    final Notification notification = Notification.fromConfig(this, notificationSection);
                    if (notification == null) {
                        continue;
                    }
                    if (addNotification(key, notification)) {
                        notification.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean cancelNotification(String name) {
        final Notification remove = adsNotifications.remove(name);
        if (remove != null) {
            remove.stop();
        }
        return remove != null;
    }

    public void saveNotification(String name) throws NotifibleException {
        final Notification notification = adsNotifications.get(name);
        if (notification == null) {
            throw new NotifibleException(LangConfig.NO_SUCH_NOTIFICATION.getText(name));
        }
        try {
            ConfigurationSection section = ChatModuleConfig.NOTIFICATION_MESSAGES.getSection();
            if (section == null) {
                section = ChatModuleConfig.NOTIFICATION_MESSAGES.createSection();
            }
            ConfigurationSection notificationSection = section.getConfigurationSection(name);
            if (notificationSection == null) {
                notificationSection = section.createSection(name);
            }
            notification.toConfig(notificationSection);
            ChatModuleConfig.NOTIFICATION_MESSAGES.saveSection(section);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNotificationInfo(String name) throws NotifibleException {
        final Notification notification = adsNotifications.get(name);
        if (notification == null) {
            throw new NotifibleException(LangConfig.NO_SUCH_NOTIFICATION.getText(name));
        }
        return notification.getInfo(name);
    }

    public void deleteNotification(String name) throws NotifibleException {
        final Notification notification = adsNotifications.get(name);
        if (notification == null) {
            throw new NotifibleException(LangConfig.NO_SUCH_NOTIFICATION.getText(name));
        }
        try {
            ConfigurationSection section = ChatModuleConfig.NOTIFICATION_MESSAGES.getSection();
            if (section == null) {
                section = ChatModuleConfig.NOTIFICATION_MESSAGES.createSection();
            }
            section.set(name, null);
            ChatModuleConfig.NOTIFICATION_MESSAGES.saveSection(section);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean requestSendMessage(ChatMessageSender sender, Player receiver) {
        if (sender.getPermissionStatus(UnlimitedAdminPermissionsList.CHAT_IGNORE_BYPASS) == PermissionStatus.PERMISSION_TRUE) {
            return true;
        }

        final Ignore ignore = getIgnore(receiver.getUniqueId());
        if (ignore == null) {
            return true;
        }
        return !ignore.isIgnored(sender.getUUID().toString());
    }

    public void setMute(Mute mute) {
        ChatMuteConfig.saveMuted(mute);
    }

    public void setIgnore(Ignore ignore) {
        ChatMuteConfig.saveIgnored(ignore);
    }

    @Nullable
    public Mute getMute(UUID uuid) {
        return ChatMuteConfig.getMuteForPlayer(uuid);
    }

    @Nullable
    public Ignore getIgnore(UUID uuid) {
        return ChatMuteConfig.getIgnoreForPlayer(uuid);
    }
}
