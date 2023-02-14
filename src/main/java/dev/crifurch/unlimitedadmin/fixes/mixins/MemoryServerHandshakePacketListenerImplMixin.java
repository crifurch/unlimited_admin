package dev.crifurch.unlimitedadmin.fixes.mixins;

import dev.crifurch.unlimitedadmin.fixes.classes.CustomServerLoginPacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.MemoryServerHandshakePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(MemoryServerHandshakePacketListenerImpl.class)
public class MemoryServerHandshakePacketListenerImplMixin {
    @Shadow
    @Final
    private  MinecraftServer server;

    @Shadow
    @Final
    private  Connection connection;
    /**
     * @author Crifurch
     * @reason allow all usernames
     */
    @Overwrite
    public void handleIntention(ClientIntentionPacket p_9697_) {
        if (!net.minecraftforge.server.ServerLifecycleHooks.handleServerLogin(p_9697_, this.connection)) return;
        this.connection.setProtocol(p_9697_.getIntention());
        this.connection.setListener(new CustomServerLoginPacketListenerImpl(this.server, this.connection));
    }
}
