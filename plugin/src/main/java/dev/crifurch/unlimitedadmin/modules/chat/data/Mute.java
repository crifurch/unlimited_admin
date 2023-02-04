package dev.crifurch.unlimitedadmin.modules.chat.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.UUID;

public class Mute {
    private final UUID uuid;
    private final List<String> mutedChannels;

    public Mute(UUID uuid, List<String> mutedChannels) {
        this.uuid = uuid;
        this.mutedChannels = mutedChannels;
    }

    public static Mute fromConfigurationSection(ConfigurationSection section) {
        final UUID uuid = UUID.fromString(section.getName());
        final List<String> mutedChannels = section.getStringList("mutedChannels");
        return new Mute(uuid, mutedChannels);
    }

    public UUID getUUID() {
        return uuid;
    }

    public List<String> getMutedChannels() {
        return mutedChannels;
    }

    public void addMutedChannel(String channel) {
        if (mutedChannels.contains(channel) || mutedChannels.contains("*")) {
            return;
        }
        if (channel.equals("*")) {
            mutedChannels.clear();
        }
        if (mutedChannels.contains("!" + channel)) {
            mutedChannels.remove("!" + channel);
            return;
        }
        mutedChannels.add(channel);
    }

    public void addExceptChannel(String channel) {
        if (channel.equals("*")) {
            mutedChannels.clear();
            return;
        }
        mutedChannels.remove(channel);
        if (mutedChannels.contains("!" + channel)) {
            return;
        }
        if (mutedChannels.contains("*")) {
            mutedChannels.add("!" + channel);
        }
    }

    public ConfigurationSection toConfigurationSection(ConfigurationSection section) {
        section.set("mutedChannels", mutedChannels);
        return section;
    }

    public boolean isMuted(String channel) {
        if (mutedChannels.contains("*")) {
            return !mutedChannels.contains("!" + channel);
        }
        return mutedChannels.contains(channel);
    }
}
