package xyz.amymialee.knifeghost.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.knifeghost.KnifeGhost;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Unique private static final Identifier SWORD_BLUR = KnifeGhost.id("textures/misc/swordblur.png");
    @Unique private static final Identifier GHOST_BLUR = KnifeGhost.id("textures/misc/ghostblur.png");
    @Unique private static final Identifier KNIFE_BLUR = KnifeGhost.id("textures/misc/knifeblur.png");

    @Shadow protected abstract void renderOverlay(DrawContext context, Identifier texture, float opacity);

    @WrapOperation(method = "renderMiscOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean knifeghost$renderoverlays(ItemStack instance, Item item, @NotNull Operation<Boolean> original, DrawContext context) {
        var result = original.call(instance, item);
        if (!result) {
            if (instance.isOf(KnifeGhost.SWORD_PUMPKIN.asItem())) {
                this.renderOverlay(context, SWORD_BLUR, 1.0F);
            } else if (instance.isOf(KnifeGhost.GHOST_PUMPKIN.asItem())) {
                this.renderOverlay(context, GHOST_BLUR, 1.0F);
            } else if (instance.isOf(KnifeGhost.KNIFE_PUMPKIN.asItem())) {
                this.renderOverlay(context, KNIFE_BLUR, 1.0F);
            }
        }
        return result;
    }
}