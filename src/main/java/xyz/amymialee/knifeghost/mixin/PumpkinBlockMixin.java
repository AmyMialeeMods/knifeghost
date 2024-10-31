package xyz.amymialee.knifeghost.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.knifeghost.KnifeGhost;
import xyz.amymialee.knifeghost.item.KnifeItem;
import xyz.amymialee.knifeghost.util.KnifeGhostSpawner;

@Mixin(PumpkinBlock.class)
public class PumpkinBlockMixin {
    @Inject(method = "onUseWithItem", at = @At("HEAD"), cancellable = true)
    protected void knifeghost$haunted(@NotNull ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ItemActionResult> cir) {
        var isSword = stack.getItem() instanceof SwordItem;
        var isKnife = stack.getItem() instanceof KnifeItem;
        if (!isSword && !isKnife) return;
        if (world.isClient) {
            cir.setReturnValue(ItemActionResult.success(true));
            return;
        }
        var side = hit.getSide();
        var direction = side.getAxis() == Direction.Axis.Y ? player.getHorizontalFacing().getOpposite() : side;
        world.playSound(null, pos, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, isKnife ? 1.2f : 0.65F);
        if (isSword) {
            world.setBlockState(pos, KnifeGhost.SWORD_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, direction), 11);
        } else {
            world.setBlockState(pos, KnifeGhost.KNIFE_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, direction), 11);
        }
        var itemEntity = new ItemEntity(world, pos.getX() + 0.5 + direction.getOffsetX() * 0.65, pos.getY() + 0.1, pos.getZ() + 0.5 + direction.getOffsetZ() * 0.65, new ItemStack(Items.PUMPKIN_SEEDS, 4));
        itemEntity.setVelocity(0.05 * direction.getOffsetX() + world.random.nextDouble() * 0.02, 0.05, 0.05 * direction.getOffsetZ() + world.random.nextDouble() * 0.02);
        world.spawnEntity(itemEntity);
        if (isSword) stack.damage(1, player, LivingEntity.getSlotForHand(hand));
        world.emitGameEvent(player, GameEvent.SHEAR, pos);
        player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        cir.setReturnValue(ItemActionResult.success(false));
        if (!isSword) return;
        KnifeGhostSpawner.trySpawn(world, pos);
        if (!(world instanceof ServerWorld serverWorld)) return;
        var hitPos = hit.getPos();
        serverWorld.spawnParticles(ParticleTypes.WITCH, hitPos.x, hitPos.y, hitPos.z, 32, 0.15, 0.15, 0.15, 0.025);
    }
}