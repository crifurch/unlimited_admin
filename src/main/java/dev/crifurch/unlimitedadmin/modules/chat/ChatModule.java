package dev.crifurch.unlimitedadmin.modules.chat;

import dev.crifurch.unlimitedadmin.api.compat.playermeta.IPlayerMetaProvider;
import dev.crifurch.unlimitedadmin.common.UnlimitedAdmin;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static dev.crifurch.unlimitedadmin.utils.ColorUtils.replaceColorsSymbols;


public class ChatModule {

    public static String[] getPrefixes(Player player) {
        final Collection<MutableComponent> prefixes = player.getPrefixes();
        final ArrayList<String> prefixesList = new ArrayList<>();
        for (MutableComponent prefix : prefixes) {
            prefixesList.add(prefix.getString());
        }
        return prefixesList.toArray(new String[0]);
    }

    private static boolean isInSphere(BlockPos pos, BlockPos center, double radius) {
        double x = pos.getX() - center.getX();
        double y = pos.getY() - center.getY();
        double z = pos.getZ() - center.getZ();

        return x * x + y * y + z * z <= radius * radius;
    }

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        String message = event.getMessage();
        boolean isLocal = !message.startsWith(ChatConfig.SERVER.GLOBAL_CHAT_PREFIX.get());
        if (!isLocal) {
            message = message.substring(ChatConfig.SERVER.GLOBAL_CHAT_PREFIX.get().length());
        }
        Level world = event.getPlayer().getCommandSenderWorld();
        MinecraftServer server = world.getServer();
        final PlayerList playerList = Objects.requireNonNull(server).getPlayerList();
        ArrayList<Player> players = new ArrayList<>(playerList.getPlayers());
        if (isLocal) {
            BlockPos playerPos = event.getPlayer().blockPosition();
            players.removeIf(player ->
                    event.getPlayer().getCommandSenderWorld() != player.getCommandSenderWorld()
                            || !isInSphere(player.blockPosition(), playerPos, ChatConfig.SERVER.LOCAL_CHAT_RADIUS.get()
                    ));
        }

        if (players.size() == 0 || (players.size() == 1 && players.get(0) == event.getPlayer())) {
            if (ChatConfig.SERVER.SHOW_NOBODY_HEAR_YOU.get()) {
                event.getPlayer().sendMessage(new TextComponent("?????????? ???? ?????????????? ??????"), event.getPlayer().getUUID());
            }
            event.setCanceled(true);
            return;
        }

        String format = isLocal ? ChatConfig.SERVER.LOCAL_CHAT_FORMAT.get() : ChatConfig.SERVER.GLOBAL_CHAT_FORMAT.get();
        final IPlayerMetaProvider playerMetaProvider = UnlimitedAdmin.instance.playerMetaProvider;
        message = format
                .replace("%player", playerMetaProvider.getNickname(event.getPlayer()))
                .replace("%displayname", playerMetaProvider.getDisplayName(event.getPlayer()))
                .replace("%message", message)
                .replace("%prefixes", String.join("", playerMetaProvider.getPrefixes(event.getPlayer())))
                .replace("%suffixes", String.join("", playerMetaProvider.getSuffixes(event.getPlayer())));

        message = replaceColorsSymbols(message);

        Component component = new TextComponent(message);

        for (Player player : players) {
            player.sendMessage(component, event.getPlayer().getUUID());
        }

        event.setCanceled(true); //Required for CustomNPC Support
    }

}
