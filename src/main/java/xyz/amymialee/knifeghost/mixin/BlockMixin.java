package xyz.amymialee.knifeghost.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.knifeghost.KnifeGhost;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "cannotConnect", at = @At("RETURN"), cancellable = true)
    private static void knifeghost$morepumpkins(BlockState state, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()
                && (state.isOf(KnifeGhost.SWORD_PUMPKIN)
                || state.isOf(KnifeGhost.GHOST_PUMPKIN)
                || state.isOf(KnifeGhost.KNIFE_PUMPKIN)
                || state.isOf(KnifeGhost.SWORD_O_LANTERN)
                || state.isOf(KnifeGhost.GHOST_O_LANTERN)
                || state.isOf(KnifeGhost.KNIFE_O_LANTERN))) {
            cir.setReturnValue(true);
        }
    }
}