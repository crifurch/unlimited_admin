package dev.crifurch.unlimitedadmin.modules.stop.commands;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StopServerModule {
    final UUID actionUUID = UUID.fromString("7a132a29-6d18-4c26-ae68-98798cdc9677");
    final MinecraftServer server;
    boolean isStopping = false;

    public StopServerModule(MinecraftServer server) {
        this.server = server;
    }

    public void stop(final int seconds, final String reason) {
        if (isStopping) {
            return;
        }

        isStopping = true;
        new Thread(() -> {
            int time = seconds;
            while (time > 0) {
                sendMsg("\u00A7c" + reason, ((float) time) / seconds);
                time--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sendMsg("\u00A7cСервер выключается...", 0);

            final List<ServerPlayer> players = new ArrayList<>(server.getPlayerList().getPlayers());
            players.forEach(player -> player.connection.disconnect(new TextComponent("\u00A7cСервер выключен: " + reason)));
            server.halt(false);
        }).start();
    }

    void sendMsg(@Nullable String msg, float progress) {
        final BossEvent event = new BossEventMessage(actionUUID, msg == null ? null : new TextComponent(msg), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
        event.setProgress(progress);
        ClientboundBossEventPacket packet = null;
        if (msg != null) {
            packet = ClientboundBossEventPacket.createUpdateNamePacket(event);
        }
        ClientboundBossEventPacket finalNamePacket = packet;
        server.getPlayerList().getPlayers().forEach(player -> {
            if ((int) progress == 1) {
                player.connection.send(ClientboundBossEventPacket.createAddPacket(event));
            }
            if (finalNamePacket != null)
                player.connection.send(finalNamePacket);
            player.connection.send(ClientboundBossEventPacket.createUpdateProgressPacket(event));
        });
    }


    static class BossEventMessage extends BossEvent {

        public BossEventMessage(UUID p_18849_, Component p_18850_, BossBarColor p_18851_, BossBarOverlay p_18852_) {
            super(p_18849_, p_18850_, p_18851_, p_18852_);
        }
    }
}
