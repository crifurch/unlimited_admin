package dev.crifurch.unlimitedadmin.fixes.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderDragon.class)
public class DragonSoundDisableMixin {

    @Redirect(method = "tickDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;globalLevelEvent(ILnet/minecraft/core/BlockPos;I)V"))
    public void tick(Level instance, int p_46665_, BlockPos p_46666_, int p_46667_) {
    }
}
