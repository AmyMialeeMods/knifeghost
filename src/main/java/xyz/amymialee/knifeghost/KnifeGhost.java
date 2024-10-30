package xyz.amymialee.knifeghost;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import xyz.amymialee.knifeghost.cca.KnivesComponent;
import xyz.amymialee.knifeghost.entity.KnifeEntity;
import xyz.amymialee.knifeghost.entity.KnifeGhostEntity;
import xyz.amymialee.knifeghost.item.KnifeItem;
import xyz.amymialee.mialib.templates.MRegistry;

import java.util.function.Consumer;

public class KnifeGhost implements ModInitializer, EntityComponentInitializer {
    public static final String MOD_ID = "knifeghost";
    public static final MRegistry REGISTRY = new MRegistry(MOD_ID);

    public static final EntityType<KnifeGhostEntity> KNIFE_GHOST_ENTITY = REGISTRY.registerEntity("knife_ghost", EntityType.Builder.create(KnifeGhostEntity::new, SpawnGroup.MONSTER).dimensions(0.8F, 2F).maxTrackingRange(16).build(), KnifeGhostEntity.createGhostAttributes(), new MRegistry.EggData(0xAAEFCF, 0x88DFBF));
    public static final EntityType<KnifeEntity> KNIFE_ENTITY = REGISTRY.register("knife", EntityType.Builder.create(KnifeEntity::new, SpawnGroup.MISC).dimensions(0.65f, 0.65f).maxTrackingRange(16).build());

    public static final RegistryKey<DamageType> KNIFE_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("knife"));

    public static final Item KNIFE = REGISTRY.registerItem("knife", new KnifeItem(0xA9D1C5, new Item.Settings().fireproof().attributeModifiers(KnifeItem.createAttributes()).rarity(Rarity.EPIC)), ItemGroups.TOOLS);
    public static final Item KNIFE_FRENCH = REGISTRY.registerItem("knife_french", new KnifeItem(0x434343, new Item.Settings().fireproof().attributeModifiers(KnifeItem.createAttributes()).rarity(Rarity.EPIC)), ItemGroups.TOOLS);
    public static final Item KNIFE_LEET = REGISTRY.registerItem("knife_leet", new KnifeItem(0xD14775, new Item.Settings().fireproof().attributeModifiers(KnifeItem.createAttributes()).rarity(Rarity.EPIC)), ItemGroups.TOOLS);
    public static final Item KNIFE_RETRO = REGISTRY.registerItem("knife_retro", new KnifeItem(0x49348E, new Item.Settings().fireproof().attributeModifiers(KnifeItem.createAttributes()).rarity(Rarity.EPIC)), ItemGroups.TOOLS);
    public static final Item KNIFE_SHINY = REGISTRY.registerItem("knife_shiny", new KnifeItem(0x7583FF, new Item.Settings().fireproof().attributeModifiers(KnifeItem.createAttributes()).rarity(Rarity.EPIC)), ItemGroups.TOOLS);
    public static final Item KNIFE_TACTICAL = REGISTRY.registerItem("knife_tactical", new KnifeItem(0x687A4F, new Item.Settings().fireproof().attributeModifiers(KnifeItem.createAttributes()).rarity(Rarity.EPIC)), ItemGroups.TOOLS);

    public static final Block SWORD_PUMPKIN = REGISTRY.registerBlockWithItem("sword_pumpkin", new WearableCarvedPumpkinBlock(AbstractBlock.Settings.copy(Blocks.CARVED_PUMPKIN)), ItemGroups.NATURAL);
    public static final Block GHOST_PUMPKIN = REGISTRY.registerBlockWithItem("ghost_pumpkin", new WearableCarvedPumpkinBlock(AbstractBlock.Settings.copy(Blocks.CARVED_PUMPKIN)), ItemGroups.NATURAL);
    public static final Block KNIFE_PUMPKIN = REGISTRY.registerBlockWithItem("knife_pumpkin", new WearableCarvedPumpkinBlock(AbstractBlock.Settings.copy(Blocks.CARVED_PUMPKIN)), ItemGroups.NATURAL);
    public static final Block SWORD_O_LANTERN = REGISTRY.registerBlockWithItem("sword_o_lantern", new CarvedPumpkinBlock(AbstractBlock.Settings.copy(Blocks.JACK_O_LANTERN)), ItemGroups.NATURAL);
    public static final Block GHOST_O_LANTERN = REGISTRY.registerBlockWithItem("ghost_o_lantern", new CarvedPumpkinBlock(AbstractBlock.Settings.copy(Blocks.JACK_O_LANTERN)), ItemGroups.NATURAL);
    public static final Block KNIFE_O_LANTERN = REGISTRY.registerBlockWithItem("knife_o_lantern", new CarvedPumpkinBlock(AbstractBlock.Settings.copy(Blocks.JACK_O_LANTERN)), ItemGroups.NATURAL);

    public static final TagKey<Item> KNIVES = TagKey.of(RegistryKeys.ITEM, id("knives"));

    public static final SoundEvent KNIFEGHOST_CHARGE = REGISTRY.registerSound("charge");
    public static final SoundEvent KNIFEGHOST_DEATH = REGISTRY.registerSound("death");
    public static final SoundEvent KNIFEGHOST_HURT = REGISTRY.registerSound("hurt");
    public static final SoundEvent KNIFEGHOST_IDLE = REGISTRY.registerSound("idle");
    public static final SoundEvent KNIFEGHOST_IMPACT = REGISTRY.registerSound("impact");
    public static final SoundEvent KNIFEGHOST_THROW = REGISTRY.registerSound("throw");

    @Override
    public void onInitialize() {
        Consumer<ItemConvertible> consumer = (pumpkin) -> DispenserBlock.registerBehavior(pumpkin, new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                World world = pointer.world();
                var blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                var carvedPumpkinBlock = (CarvedPumpkinBlock) pumpkin;
                if (world.isAir(blockPos) && carvedPumpkinBlock.canDispense(world, blockPos)) {
                    if (!world.isClient) {
                        world.setBlockState(blockPos, carvedPumpkinBlock.getDefaultState(), Block.NOTIFY_ALL);
                        world.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
                    }
                    stack.decrement(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor(pointer, stack));
                }
                return stack;
            }
        });
        consumer.accept(SWORD_PUMPKIN);
        consumer.accept(GHOST_PUMPKIN);
        consumer.accept(KNIFE_PUMPKIN);
        CompostingChanceRegistry.INSTANCE.add(SWORD_PUMPKIN, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(GHOST_PUMPKIN, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(KNIFE_PUMPKIN, 0.65F);
    }

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(KnifeGhostEntity.class, KnivesComponent.KEY).respawnStrategy(RespawnCopyStrategy.INVENTORY).end(KnivesComponent::new);
    }

    public static @NotNull ItemStack getRandomKnife(@NotNull Random random) {
        return switch (random.nextInt(4)) {
            case 0 -> new ItemStack(KNIFE_FRENCH);
            case 1 -> new ItemStack(KNIFE_LEET);
            case 2 -> new ItemStack(KNIFE_SHINY);
            case 3 -> new ItemStack(KNIFE_TACTICAL);
            default -> new ItemStack(KNIFE);
        };
    }

    public static @NotNull Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}