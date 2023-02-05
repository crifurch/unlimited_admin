package dev.crifurch.unlimitedadmin.fixes.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelCrashFixMixin {

    @Inject(method = "isLoaded", at = @At("HEAD"), cancellable = true)
    public void isLoaded(BlockPos p_46750_, CallbackInfoReturnable<Boolean> cir) {
        if(p_46750_ == null) {
            cir.setReturnValue(false);
        }
    }

}
