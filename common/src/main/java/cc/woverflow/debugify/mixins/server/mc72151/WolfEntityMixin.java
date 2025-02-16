package cc.woverflow.debugify.mixins.server.mc72151;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WolfEntity.class)
public class WolfEntityMixin {
    @ModifyVariable(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/TameableEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.BEFORE), ordinal = 0, argsOnly = true)
    private float modifyAmount(float amount, DamageSource source) {
        Entity entity = source.getAttacker();

        if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof PersistentProjectileEntity)) {
            // wolves modify damage = (amount + 1) / 2
            // so (0 + 1) / 2 = 0.5
            if (amount == 0.5)
                return 0;
        }

        // else just turn the original
        return amount;
    }
}
