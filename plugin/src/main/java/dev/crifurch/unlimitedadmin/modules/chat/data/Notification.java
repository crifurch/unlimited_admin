package dev.crifurch.unlimitedadmin.modules.chat.data;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.implementations.channels.NotificationsChatChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class Notification implements Runnable {
    private final String message;
    private final int interval;
    private final ChatModule module;

    private final Consumer<String> onSendMessagesConsumer;

    private BukkitTask task;

    public Notification(ChatModule chatModule, String message, int interval, Consumer<String> onSendMessagesConsumer) {
        this.message = NotificationsChatChannel.CHANNEL_PREFIX + message;
        this.interval = 20 * interval;
        this.module = chatModule;
        this.onSendMessagesConsumer = onSendMessagesConsumer;
    }

    public Notification(ChatModule chatModule, String message, int interval) {
        this.message = NotificationsChatChannel.CHANNEL_PREFIX + message;
        this.interval = 20 * interval;
        this.module = chatModule;
        this.onSendMessagesConsumer = null;
    }

    public void stop() {
        if (task != null) task.cancel();
    }

    public void schedule() {
        task = Bukkit.getScheduler().runTaskLater(UnlimitedAdmin.getInstance(), this, interval);
    }

    public void start() {
        if (task != null) return;
        run();
    }

    public String getMessage() {
        return message.substring(NotificationsChatChannel.CHANNEL_PREFIX.length());
    }

    public int getInterval() {
        return interval / 20;
    }

    @Override
    public void run() {
        module.broadcastMessage(null, message);
        if (interval > 0) {
            task = Bukkit.getScheduler().runTaskLater(UnlimitedAdmin.getInstance(), this, interval);
        } else {
            stop();
        }
        if (onSendMessagesConsumer != null) onSendMessagesConsumer.accept(message);
    }

    public static Notification fromConfig(ChatModule chatModule, @NotNull ConfigurationSection section) {
        String message = section.getString("message");
        if (message == null) return null;
        int interval = section.getInt("interval");
        return new Notification(chatModule, message, interval);
    }

    public String getInfo(String name) {
        return "name: " + name + "\nmessage: " + getMessage() + "\ninterval: " + getInterval() + "s";
    }


    public void toConfig(ConfigurationSection section) {
        section.set("message", getMessage());
        section.set("interval", getInterval());
    }
}
