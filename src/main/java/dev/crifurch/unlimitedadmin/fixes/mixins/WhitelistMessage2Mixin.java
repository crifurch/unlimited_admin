package dev.crifurch.unlimitedadmin.fixes.mixins;

import com.mojang.authlib.GameProfile;
import dev.crifurch.unlimitedadmin.modules.chat.ChatConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerList.class)
public class WhitelistMessage2Mixin {

    @Inject(method = "canPlayerLogin", at = @At(value = "RETURN", shift = At.Shift.BEFORE, ordinal = 1), cancellable = true)
    public void canPlayerLogin(SocketAddress p_11257_, GameProfile p_11258_, CallbackInfoReturnable<Component> cir) {
        if (ChatConfig.SERVER.WHITE_LIST_MESSAGE_ENABLED.get())
            cir.setReturnValue(new TextComponent(ChatConfig.SERVER.WHITELIST_MESSAGE.get()));

    }
}
