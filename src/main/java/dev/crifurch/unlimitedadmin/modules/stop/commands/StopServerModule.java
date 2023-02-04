package dev.crifurch.unlimitedadmin.modules.stop.commands;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

public class StopServerModule {
    final MinecraftServer server;
    boolean isStopping = false;

    public StopServerModule(MinecraftServer server) {
        this.server = server;
    }

    public void stop(int seconds) {
         if (isStopping) {
            return;
        }
        final int delay = seconds;

        isStopping = true;
        new Thread(() -> {
            int time = delay;
            while (time > 0) {
                if (time == delay || time == 60 || time == 30 || time <= 15) {
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
            server.halt(false);
        }).start();
    }

    void sendMsg(String msg) {
        server.getPlayerList().getPlayers().forEach(player -> {
            player.sendMessage(new TextComponent(msg), player.getUUID());
        });
    }

}
