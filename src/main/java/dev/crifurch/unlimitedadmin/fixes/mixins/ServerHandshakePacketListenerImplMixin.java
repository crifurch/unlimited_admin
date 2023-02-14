package dev.crifurch.unlimitedadmin.fixes.mixins;

import dev.crifurch.unlimitedadmin.fixes.classes.CustomServerLoginPacketListenerImpl;
import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(ServerHandshakePacketListenerImpl.class)
public class ServerHandshakePacketListenerImplMixin {
    private static final Component IGNORE_STATUS_REASON = new TextComponent("Ignoring status request");
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private Connection connection;

    /**
     * @author Crifurch
     * @reason allow all usernames
     */
    @Overwrite
    public void handleIntention(ClientIntentionPacket p_9975_) {
        if (!net.minecraftforge.server.ServerLifecycleHooks.handleServerLogin(p_9975_, this.connection)) return;
        switch(p_9975_.getIntention()) {
            case LOGIN:
                this.connection.setProtocol(ConnectionProtocol.LOGIN);
                if (p_9975_.getProtocolVersion() != SharedConstants.getCurrentVersion().getProtocolVersion()) {
                    Component component;
                    if (p_9975_.getProtocolVersion() < 754) {
                        component = new TranslatableComponent("multiplayer.disconnect.outdated_client", SharedConstants.getCurrentVersion().getName());
                    } else {
                        component = new TranslatableComponent("multiplayer.disconnect.incompatible", SharedConstants.getCurrentVersion().getName());
                    }

                    this.connection.send(new ClientboundLoginDisconnectPacket(component));
                    this.connection.disconnect(component);
                } else {
                    this.connection.setListener(new CustomServerLoginPacketListenerImpl(this.server, this.connection));
                }
                break;
            case STATUS:
                if (this.server.repliesToStatus()) {
                    this.connection.setProtocol(ConnectionProtocol.STATUS);
                    this.connection.setListener(new ServerStatusPacketListenerImpl(this.server, this.connection));
                } else {
                    this.connection.disconnect(IGNORE_STATUS_REASON);
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + p_9975_.getIntention());
        }

    }
}
