package xyz.amymialee.knifeghost.item;

import net.fabricmc.fabric.mixin.content.registry.AxeItemAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CakeBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.templates.MToolMaterial;

import java.util.List;

public class KnifeItem extends ToolItem implements Equipment {
    private static final MToolMaterial KNIFE = new MToolMaterial().setToolDurability(0).setMiningSpeedMultiplier(9).setAttackDamage(4).setEnchantability(22);
    private final int colour;

    public KnifeItem(int colour, Item.@NotNull Settings settings) {
        super(KNIFE, settings.component(DataComponentTypes.TOOL, createToolComponent()));
        this.colour = colour;
    }

    private static @NotNull ToolComponent createToolComponent() {
        return new ToolComponent(List.of(ToolComponent.Rule.ofAlwaysDropping(List.of(Blocks.COBWEB), 15.0F), ToolComponent.Rule.of(BlockTags.SWORD_EFFICIENT, 1.5F)), 1.0F, 2);
    }

    public static AttributeModifiersComponent createAttributes() {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 0.5f + KNIFE.getAttackDamage(), EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2F, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND).build();
    }

    @Override
    public ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        var player = context.getPlayer();
        if (shouldCancelStripAttempt(player, context.getHand())) return ActionResult.PASS;
        var world = context.getWorld();
        var pos = context.getBlockPos();
        var state = world.getBlockState(pos);
        if (state.isOf(Blocks.MELON)) {
            world.breakBlock(pos, false);
            for (var i = 0; i < 9; i++) {
                var itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Items.MELON_SLICE, 1));
                itemEntity.mialib$setMergeDelay(10);
                itemEntity.setVelocity(world.random.nextGaussian() * 0.02, 0.15, world.random.nextGaussian() * 0.02);
                world.spawnEntity(itemEntity);
            }
            return ActionResult.SUCCESS;
        } else if (state.isOf(Blocks.CAKE)) {
            if (state.get(CakeBlock.BITES) == 0) {
                world.breakBlock(pos, false);
                var itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Items.CAKE, 1));
                itemEntity.setVelocity(world.random.nextGaussian() * 0.02, 0.15, world.random.nextGaussian() * 0.02);
                world.spawnEntity(itemEntity);
                return ActionResult.SUCCESS;
            }
        } else if (AxeItemAccessor.getStrippedBlocks().containsKey(state.getBlock())) {
            return Items.NETHERITE_AXE.useOnBlock(context);
        }
        return super.useOnBlock(context);
    }

    private static boolean shouldCancelStripAttempt(PlayerEntity player, Hand hand) {
        return player != null && hand.equals(Hand.MAIN_HAND) && player.getOffHandStack().isOf(Items.SHIELD) && !player.shouldCancelInteraction();
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, @NotNull PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postDamageEntity(@NotNull ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public int mialib$getNameColor(ItemStack stack) {
        return this.colour;
    }
}