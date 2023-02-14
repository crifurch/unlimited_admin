package dev.crifurch.unlimitedadmin.fixes.classes;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.logging.LogUtils;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomServerLoginPacketListenerImpl extends ServerLoginPacketListenerImpl {
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Random RANDOM = new Random();
    private final byte[] nonce = new byte[4];
    final MinecraftServer server;
    public final Connection connection;
    CustomServerLoginPacketListenerImpl.State state = CustomServerLoginPacketListenerImpl.State.HELLO;
    private int tick;
    @Nullable
    public GameProfile gameProfile;
    private final String serverId = "";
    private String originalUsername = "";
    @Nullable
    private ServerPlayer delayedAcceptPlayer;

    public CustomServerLoginPacketListenerImpl(MinecraftServer p_10027_, Connection p_10028_) {
        super(p_10027_, p_10028_);
        this.server = p_10027_;
        this.connection = p_10028_;
        RANDOM.nextBytes(this.nonce);
    }

    public void tick() {
        if (this.state == CustomServerLoginPacketListenerImpl.State.NEGOTIATING) {
            // We force the state into "NEGOTIATING" which is otherwise unused. Once we're completed we move the negotiation onto "READY_TO_ACCEPT"
            // Might want to promote player object creation to here as well..
            boolean negotiationComplete = net.minecraftforge.network.NetworkHooks.tickNegotiation(null, this.connection, this.delayedAcceptPlayer);
            if (negotiationComplete)
                this.state = CustomServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
        } else if (this.state == CustomServerLoginPacketListenerImpl.State.READY_TO_ACCEPT) {
            this.handleAcceptedLogin();
        } else if (this.state == CustomServerLoginPacketListenerImpl.State.DELAY_ACCEPT) {
            ServerPlayer serverplayer = this.server.getPlayerList().getPlayer(this.gameProfile.getId());
            if (serverplayer == null) {
                this.state = CustomServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
                this.placeNewPlayer(this.delayedAcceptPlayer);
                this.delayedAcceptPlayer = null;
            }
        }

        if (this.tick++ == 600) {
            this.disconnect(new TranslatableComponent("multiplayer.disconnect.slow_login"));
        }

    }

    public Connection getConnection() {
        return this.connection;
    }

    public void disconnect(Component p_10054_) {
        try {
            LOGGER.info("Disconnecting {}: {}", this.getUserName(), p_10054_.getString());
            this.connection.send(new ClientboundLoginDisconnectPacket(p_10054_));
            this.connection.disconnect(p_10054_);
        } catch (Exception exception) {
            LOGGER.error("Error whilst disconnecting player", (Throwable) exception);
        }

    }

    public void handleAcceptedLogin() {
        if (!this.gameProfile.isComplete()) {
            this.gameProfile = this.createFakeProfile(this.gameProfile);
        }

        Component component = this.server.getPlayerList().canPlayerLogin(this.connection.getRemoteAddress(), this.gameProfile);
        if (component != null) {
            this.disconnect(component);
        } else {
            this.state = CustomServerLoginPacketListenerImpl.State.ACCEPTED;
            if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
                this.connection.send(new ClientboundLoginCompressionPacket(this.server.getCompressionThreshold()), (p_10041_) -> {
                    this.connection.setupCompression(this.server.getCompressionThreshold(), true);
                });
            }

            this.connection.send(new ClientboundGameProfilePacket(this.gameProfile));
            ServerPlayer serverplayer = this.server.getPlayerList().getPlayer(this.gameProfile.getId());

            try {
                ServerPlayer serverplayer1 = this.server.getPlayerList().getPlayerForLogin(this.gameProfile);
                if (serverplayer != null) {
                    this.state = CustomServerLoginPacketListenerImpl.State.DELAY_ACCEPT;
                    this.delayedAcceptPlayer = serverplayer1;
                } else {
                    this.placeNewPlayer(serverplayer1);
                }
            } catch (Exception exception) {
                LOGGER.error("Couldn't place player in world", (Throwable) exception);
                Component component1 = new TranslatableComponent("multiplayer.disconnect.invalid_player_data");
                this.connection.send(new ClientboundDisconnectPacket(component1));
                this.connection.disconnect(component1);
            }
        }

    }

    private void placeNewPlayer(ServerPlayer p_143700_) {
        this.server.getPlayerList().placeNewPlayer(this.connection, p_143700_);
    }

    public void onDisconnect(Component p_10043_) {
        LOGGER.info("{} lost connection: {}", this.getUserName(), p_10043_.getString());
    }

    public String getUserName() {
        return this.gameProfile != null ? this.gameProfile + " (" + this.connection.getRemoteAddress() + ")" : String.valueOf(this.connection.getRemoteAddress());
    }

    @Override
    public void handleHello(ServerboundHelloPacket p_10047_) {
        Validate.validState(this.state == CustomServerLoginPacketListenerImpl.State.HELLO, "Unexpected hello packet");
        this.gameProfile = p_10047_.getGameProfile();
        this.originalUsername = p_10047_.getGameProfile().getName();
        String newName = originalUsername;
        if (newName.length() > 16) {
            newName = newName.substring(0, 16);
        }
        newName = fixRussian(newName);
        if (newName.length() > 16) {
            newName = newName.substring(0, 16);
        }
        GameProfile oldProfile = this.gameProfile;
        this.gameProfile = new GameProfile(this.gameProfile.getId(), newName);
        this.gameProfile.getProperties().putAll(oldProfile.getProperties());
        if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
            this.state = CustomServerLoginPacketListenerImpl.State.KEY;
            this.connection.send(new ClientboundHelloPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
        } else {
            this.state = CustomServerLoginPacketListenerImpl.State.NEGOTIATING;
        }

    }

    @Override
    public void handleKey(ServerboundKeyPacket p_10049_) {
        Validate.validState(this.state == CustomServerLoginPacketListenerImpl.State.KEY, "Unexpected key packet");
        PrivateKey privatekey = this.server.getKeyPair().getPrivate();

        final String s;
        try {
            if (!Arrays.equals(this.nonce, p_10049_.getNonce(privatekey))) {
                throw new IllegalStateException("Protocol error");
            }

            SecretKey secretkey = p_10049_.getSecretKey(privatekey);
            Cipher cipher = Crypt.getCipher(2, secretkey);
            Cipher cipher1 = Crypt.getCipher(1, secretkey);
            s = (new BigInteger(Crypt.digestData("", this.server.getKeyPair().getPublic(), secretkey))).toString(16);
            this.state = CustomServerLoginPacketListenerImpl.State.AUTHENTICATING;
            this.connection.setEncryptionKey(cipher, cipher1);
        } catch (CryptException cryptexception) {
            throw new IllegalStateException("Protocol error", cryptexception);
        }

        Thread thread = new Thread(net.minecraftforge.fml.util.thread.SidedThreadGroups.SERVER, "User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet()) {
            public void run() {
                GameProfile gameprofile = CustomServerLoginPacketListenerImpl.this.gameProfile;

                try {
                    String currentName = gameprofile.getName();
                    CustomServerLoginPacketListenerImpl.this.gameProfile = CustomServerLoginPacketListenerImpl.this.server.getSessionService().hasJoinedServer(new GameProfile(null, originalUsername), s, this.getAddress());

                    if (CustomServerLoginPacketListenerImpl.this.gameProfile != null) {
                        GameProfile newProfile = new GameProfile(CustomServerLoginPacketListenerImpl.this.gameProfile.getId(), currentName);
                        newProfile.getProperties().putAll(CustomServerLoginPacketListenerImpl.this.gameProfile.getProperties());
                        CustomServerLoginPacketListenerImpl.this.gameProfile = newProfile;
                        CustomServerLoginPacketListenerImpl.LOGGER.info("UUID of player {} is {}", CustomServerLoginPacketListenerImpl.this.gameProfile.getName(), CustomServerLoginPacketListenerImpl.this.gameProfile.getId());
                        CustomServerLoginPacketListenerImpl.this.state = CustomServerLoginPacketListenerImpl.State.NEGOTIATING;
                    } else if (CustomServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                        CustomServerLoginPacketListenerImpl.LOGGER.warn("Failed to verify username but will let them in anyway!");
                        CustomServerLoginPacketListenerImpl.this.gameProfile = CustomServerLoginPacketListenerImpl.this.createFakeProfile(gameprofile);
                        CustomServerLoginPacketListenerImpl.this.state = CustomServerLoginPacketListenerImpl.State.NEGOTIATING;
                    } else {
                        CustomServerLoginPacketListenerImpl.this.disconnect(new TranslatableComponent("multiplayer.disconnect.unverified_username"));
                        CustomServerLoginPacketListenerImpl.LOGGER.error("Username '{}' tried to join with an invalid session", (Object) gameprofile.getName());
                    }
                } catch (AuthenticationUnavailableException authenticationunavailableexception) {
                    if (CustomServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                        CustomServerLoginPacketListenerImpl.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        CustomServerLoginPacketListenerImpl.this.gameProfile = CustomServerLoginPacketListenerImpl.this.createFakeProfile(gameprofile);
                        CustomServerLoginPacketListenerImpl.this.state = CustomServerLoginPacketListenerImpl.State.NEGOTIATING;
                    } else {
                        CustomServerLoginPacketListenerImpl.this.disconnect(new TranslatableComponent("multiplayer.disconnect.authservers_down"));
                        CustomServerLoginPacketListenerImpl.LOGGER.error("Couldn't verify username because servers are unavailable");
                    }
                }

            }

            @Nullable
            private InetAddress getAddress() {
                SocketAddress socketaddress = CustomServerLoginPacketListenerImpl.this.connection.getRemoteAddress();
                return CustomServerLoginPacketListenerImpl.this.server.getPreventProxyConnections() && socketaddress instanceof InetSocketAddress ? ((InetSocketAddress) socketaddress).getAddress() : null;
            }
        };
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
    }

    public void handleCustomQueryPacket(ServerboundCustomQueryPacket p_10045_) {
        if (!net.minecraftforge.network.NetworkHooks.onCustomPayload(p_10045_, this.connection))
            this.disconnect(new TranslatableComponent("multiplayer.disconnect.unexpected_query_response"));
    }

    protected @NotNull GameProfile createFakeProfile(GameProfile p_10039_) {
        UUID uuid = Player.createPlayerUUID(p_10039_.getName());
        return new GameProfile(uuid, p_10039_.getName());
    }

    enum State {
        HELLO,
        KEY,
        AUTHENTICATING,
        NEGOTIATING,
        READY_TO_ACCEPT,
        DELAY_ACCEPT,
        ACCEPTED;
    }

    private static String fixRussian(String name) {
        char[] abcCyr = {' ', 'а', 'б', 'в', 'г', 'д', 'ѓ', 'е', 'ж', 'з', 'ѕ', 'и', 'ј', 'к', 'л', 'љ', 'м', 'н', 'њ', 'о', 'п', 'р', 'с', 'т', 'ќ', 'у', 'ф', 'х', 'ц', 'ч', 'џ', 'ш', 'А', 'Б', 'В', 'Г', 'Д', 'Ѓ', 'Е', 'Ж', 'З', 'Ѕ', 'И', 'Ј', 'К', 'Л', 'Љ', 'М', 'Н', 'Њ', 'О', 'П', 'Р', 'С', 'Т', 'Ќ', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Џ', 'Ш', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '/', '-', 'Й', 'й', 'Ё', 'ё',};
        String[] abcLat = {"", "a", "b", "v", "g", "d", "]", "e", "zh", "z", "y", "i", "j", "k", "l", "q", "m", "n", "w", "o", "p", "r", "s", "t", "'", "u", "f", "h", "c", "ch", "x", "{", "A", "B", "V", "G", "D", "}", "E", "Zh", "Z", "Y", "I", "J", "K", "L", "Q", "M", "N", "W", "O", "P", "R", "S", "T", "KJ", "U", "F", "H", "C", ":", "X", "{", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "/", "-", "IY", "iy", "e", "e",};

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            boolean found = false;
            for (int x = 0; x < abcCyr.length; x++) {
                if (c == abcCyr[x]) {
                    builder.append(abcLat[x]);
                    found = true;
                }
            }
            if (!found) {
                if (c > 0 && c <= 127) {
                    builder.append(c);
                } else {
                    builder.append("_");
                }
            }
        }
        return builder.toString();
    }
}
