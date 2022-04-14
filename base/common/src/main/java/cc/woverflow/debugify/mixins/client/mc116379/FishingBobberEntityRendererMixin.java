package cc.woverflow.debugify.mixins.client.mc116379;

import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FishingBobberEntityRenderer.class)
public class FishingBobberEntityRendererMixin {
    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 2)
    private float modifyHandSwingProgress(float handSwingProgress, FishingBobberEntity bobber) {
        PlayerEntity player = bobber.getPlayerOwner();
        int j = player.getMainArm() == Arm.RIGHT ? 1 : -1;
        int j2 = j;
        ItemStack itemStack = player.getMainHandStack();
        if (!itemStack.isOf(Items.FISHING_ROD)) {
            j = -j;
        }

        return j == j2 ? handSwingProgress : 0;
    }
}
