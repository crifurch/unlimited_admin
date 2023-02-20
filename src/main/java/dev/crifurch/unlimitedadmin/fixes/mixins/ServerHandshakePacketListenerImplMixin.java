package dev.crifurch.unlimitedadmin.fixes.mixins;

import dev.crifurch.unlimitedadmin.fixes.classes.CustomServerLoginPacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(ServerHandshakePacketListenerImpl.class)
public class ServerHandshakePacketListenerImplMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Redirect(method = "handleIntention", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setListener(Lnet/minecraft/network/PacketListener;)V"))
    public void handleIntention(@NotNull Connection instance, PacketListener p_129506_) {
        if (p_129506_ instanceof ServerLoginPacketListenerImpl) {
            instance.setListener(new CustomServerLoginPacketListenerImpl(this.server, p_129506_.getConnection()));
        } else {
            instance.setListener(p_129506_);
        }
    }
}
