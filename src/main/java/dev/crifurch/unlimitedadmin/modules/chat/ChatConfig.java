package dev.crifurch.unlimitedadmin.modules.chat;

import dev.crifurch.unlimitedadmin.UnlimitedAdminForge;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

public class ChatConfig {

    public static class Server {
        public final BooleanValue GLOBAL_CHAT_ENABLED;
        public final ConfigValue<? extends String> GLOBAL_CHAT_FORMAT;
        public final ConfigValue<? extends String> GLOBAL_CHAT_PREFIX;
        public final BooleanValue IS_LOCAL_CHAT_ENABLED;
        public final ConfigValue<? extends String> LOCAL_CHAT_FORMAT;
        public final ConfigValue<? extends String> LOCAL_CHAT_PREFIX;
        public final IntValue LOCAL_CHAT_RADIUS;

        public final BooleanValue SHOW_NOBODY_HEAR_YOU;

        public final BooleanValue WHITE_LIST_MESSAGE_ENABLED;
        public final ConfigValue<String> WHITELIST_MESSAGE;


        Server(ForgeConfigSpec.Builder builder) {
            GLOBAL_CHAT_ENABLED = builder
                    .comment("Enable Global Chat")
                    .define("globalChatEnabled", true);

            GLOBAL_CHAT_FORMAT = builder
                    .comment("Global Chat Message Format")
                    .define("globalChatFormat", "%displayname: &e%message");

            GLOBAL_CHAT_PREFIX = builder
                    .comment("Global Chat Prefix symbols")
                    .define("globalChatPrefix", "!");


            IS_LOCAL_CHAT_ENABLED = builder
                    .comment("Enable Local Chat")
                    .define("localChatEnabled", true);

            LOCAL_CHAT_FORMAT = builder
                    .comment("Local Chat Message Format")
                    .define("localChatFormat", "%displayname: &f%message");

            LOCAL_CHAT_PREFIX = builder
                    .comment("Local Chat Prefix symbols")
                    .define("localChatPrefix", "");

            LOCAL_CHAT_RADIUS = builder
                    .comment("Local Chat Radius")
                    .defineInRange("localChatRadius", 50, 0, Integer.MAX_VALUE);

            SHOW_NOBODY_HEAR_YOU = builder
                    .comment("Show Nobody Hear You Message")
                    .define("showNobodyHearYou", true);

            WHITE_LIST_MESSAGE_ENABLED = builder
                    .comment("enable overwriting whitelist message")
                    .define("whitelistMessageEnabled", true);

            WHITELIST_MESSAGE = builder
                    .comment("Message when whitelist")
                    .define("whitelistMessage", "You are not in whitelist");
        }
    }

    public static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        UnlimitedAdminForge.LOGGER.debug("Loaded UnlimitedAdmin Chat's config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        UnlimitedAdminForge.LOGGER.debug("Loaded UnlimitedAdmin Chat's config just got changed on the file system!");
    }
}

