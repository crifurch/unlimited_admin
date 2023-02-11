package dev.crifurch.unlimitedadmin.fixes.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractArrow.class)
public class ArrowDespawnMixin {

    @Shadow
    private int life;


    /**
     * @author Crifurch
     * @reason Arrows should despawn after 120 ticks, not 1200 ticks.
     */
    @Overwrite
    public void tickDespawn() {
        ++this.life;
        if (this.life > 120) {
            ((Entity)(Object)this).discard();
        }
    }
}
