package dev.crifurch.unlimitedadmin.fixes.mixins;
import dev.crifurch.unlimitedadmin.modules.chat.ChatConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftServer.class)
public class WhitelistMessageMixin {
    @Redirect(method = "kickUnlistedPlayers", at = @At(value = "INVOKE",target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"))
    public void kickUnlistedPlayers(ServerGamePacketListenerImpl instance, Component p_9943_){
        if(ChatConfig.SERVER.WHITE_LIST_MESSAGE_ENABLED.get()){
            instance.disconnect(new TextComponent(ChatConfig.SERVER.WHITELIST_MESSAGE.get()));
            return;
        }
        instance.disconnect(p_9943_);
    }
}
