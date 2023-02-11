package dev.crifurch.unlimitedadmin.modules.stop.commands;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class StopServerModule {
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
                if (time == seconds || time == 60 || time == 30 || time <= 15) {
                    sendMsg("\u00A7cСервер будет выключен через " + time + " секунд");
                }
                time--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sendMsg("\u00A7cСервер выключается...");

            final List<ServerPlayer> players = server.getPlayerList().getPlayers().subList(0, server.getPlayerList().getPlayers().size());
            players.forEach(player -> player.connection.disconnect(new TextComponent("\u00A7cСервер выключен: " + reason)));
            server.halt(false);
        }).start();
    }

    void sendMsg(String msg) {
        server.getPlayerList().getPlayers().forEach(player -> {
            player.sendMessage(new TextComponent(msg), player.getUUID());
        });
    }

}
