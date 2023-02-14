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
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomServerLoginPacketListenerImpl extends ServerLoginPacketListenerImpl {
    private static final HashMap<Character, String> ILLEGAL_CHARS_FIX = new HashMap<>();

    static {
       ILLEGAL_CHARS_FIX.put(' ', "");

        ILLEGAL_CHARS_FIX.put('а', "a");
        ILLEGAL_CHARS_FIX.put('б', "b");
        ILLEGAL_CHARS_FIX.put('в', "v");
        ILLEGAL_CHARS_FIX.put('г', "g");
        ILLEGAL_CHARS_FIX.put('д', "d");
        ILLEGAL_CHARS_FIX.put('е', "e");
        ILLEGAL_CHARS_FIX.put('ё', "e");
        ILLEGAL_CHARS_FIX.put('ж', "zh");
        ILLEGAL_CHARS_FIX.put('з', "z");
        ILLEGAL_CHARS_FIX.put('и', "i");
        ILLEGAL_CHARS_FIX.put('й', "iy");
        ILLEGAL_CHARS_FIX.put('к', "k");
        ILLEGAL_CHARS_FIX.put('л', "l");
        ILLEGAL_CHARS_FIX.put('м', "m");
        ILLEGAL_CHARS_FIX.put('н', "n");
        ILLEGAL_CHARS_FIX.put('о', "o");
        ILLEGAL_CHARS_FIX.put('п', "p");
        ILLEGAL_CHARS_FIX.put('р', "r");
        ILLEGAL_CHARS_FIX.put('с', "s");
        ILLEGAL_CHARS_FIX.put('т', "t");
        ILLEGAL_CHARS_FIX.put('у', "u");
        ILLEGAL_CHARS_FIX.put('ф', "f");
        ILLEGAL_CHARS_FIX.put('х', "h");
        ILLEGAL_CHARS_FIX.put('ц', "c");
        ILLEGAL_CHARS_FIX.put('ч', "ch");
        ILLEGAL_CHARS_FIX.put('ш', "sh");
        ILLEGAL_CHARS_FIX.put('щ', "sh");
        ILLEGAL_CHARS_FIX.put('ь', "");
        ILLEGAL_CHARS_FIX.put('ъ', "");
        ILLEGAL_CHARS_FIX.put('ы', "");
        ILLEGAL_CHARS_FIX.put('э', "a");
        ILLEGAL_CHARS_FIX.put('ю', "yu");
        ILLEGAL_CHARS_FIX.put('я', "ya");

        ILLEGAL_CHARS_FIX.put('А', "A");
        ILLEGAL_CHARS_FIX.put('Б', "B");
        ILLEGAL_CHARS_FIX.put('В', "V");
        ILLEGAL_CHARS_FIX.put('Г', "G");
        ILLEGAL_CHARS_FIX.put('Д', "D");
        ILLEGAL_CHARS_FIX.put('Е', "E");
        ILLEGAL_CHARS_FIX.put('Ё', "E");
        ILLEGAL_CHARS_FIX.put('Ж', "Zh");
        ILLEGAL_CHARS_FIX.put('З', "Z");
        ILLEGAL_CHARS_FIX.put('И', "I");
        ILLEGAL_CHARS_FIX.put('Й', "Iy");
        ILLEGAL_CHARS_FIX.put('К', "K");
        ILLEGAL_CHARS_FIX.put('Л', "L");
        ILLEGAL_CHARS_FIX.put('М', "M");
        ILLEGAL_CHARS_FIX.put('Н', "N");
        ILLEGAL_CHARS_FIX.put('О', "O");
        ILLEGAL_CHARS_FIX.put('П', "P");
        ILLEGAL_CHARS_FIX.put('Р', "R");
        ILLEGAL_CHARS_FIX.put('С', "S");
        ILLEGAL_CHARS_FIX.put('Т', "T");
        ILLEGAL_CHARS_FIX.put('У', "U");
        ILLEGAL_CHARS_FIX.put('Ф', "F");
        ILLEGAL_CHARS_FIX.put('Х', "H");
        ILLEGAL_CHARS_FIX.put('Ц', "C");
        ILLEGAL_CHARS_FIX.put('Ч', "Ch");
        ILLEGAL_CHARS_FIX.put('Ш', "Sh");
        ILLEGAL_CHARS_FIX.put('Щ', "Sh");
        ILLEGAL_CHARS_FIX.put('Ь', "");
        ILLEGAL_CHARS_FIX.put('Ъ', "");
        ILLEGAL_CHARS_FIX.put('Ы', "");
        ILLEGAL_CHARS_FIX.put('Э', "A");
        ILLEGAL_CHARS_FIX.put('Ю', "Yu");
        ILLEGAL_CHARS_FIX.put('Я', "Ya");

    }

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
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            String found = ILLEGAL_CHARS_FIX.get(c);
            if (found != null) {
                builder.append(found);
            } else{
                builder.append("_");
            }
        }
        return builder.toString();
    }
}
