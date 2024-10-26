package xyz.amymialee.knifeghost;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.impl.tag.convention.v2.TagRegistration;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.knifeghost.entity.KnifeEntity;
import xyz.amymialee.knifeghost.entity.KnifeGhostEntity;
import xyz.amymialee.knifeghost.item.KnifeItem;
import xyz.amymialee.knifeghost.network.KnifeGhostSyncPayload;
import xyz.amymialee.mialib.templates.MRegistry;

public class KnifeGhost implements ModInitializer {
    public static final String MOD_ID = "knifeghost";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final MRegistry REGISTRY = new MRegistry(MOD_ID);

    public static final EntityType<KnifeGhostEntity> KNIFE_GHOST_ENTITY = REGISTRY.registerEntity("knife_ghost", EntityType.Builder.create(KnifeGhostEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.8F).maxTrackingRange(16).build(), KnifeGhostEntity.createGhostAttributes());
    public static final EntityType<KnifeEntity> KNIFE_ENTITY = REGISTRY.register("knife", EntityType.Builder.create(KnifeEntity::new, SpawnGroup.MISC).dimensions(0.65f, 0.65f).maxTrackingRange(16).build());

    public static final RegistryKey<DamageType> KNIFE_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("knife"));

    public static final Item KNIFE = REGISTRY.registerItem("knife", new KnifeItem(new Item.Settings().fireproof().attributeModifiers(KnifeItem.createAttributes()).rarity(Rarity.EPIC)), ItemGroups.TOOLS);
    public static final Item KNIFE_FRENCH = REGISTRY.registerItem("knife_french", new KnifeItem(new Item.Settings().fireproof().attributeModifiers(KnifeItem.createAttributes()).rarity(Rarity.EPIC)), ItemGroups.TOOLS);
    public static final Item KNIFE_LEET = REGISTRY.registerItem("knife_leet", new KnifeItem(new Item.Settings().fireproof().attributeModifiers(KnifeItem.createAttributes()).rarity(Rarity.EPIC)), ItemGroups.TOOLS);
    public static final Item KNIFE_RETRO = REGISTRY.registerItem("knife_retro", new KnifeItem(new Item.Settings().fireproof().attributeModifiers(KnifeItem.createAttributes()).rarity(Rarity.EPIC)), ItemGroups.TOOLS);
    public static final Item KNIFE_SHINY = REGISTRY.registerItem("knife_shiny", new KnifeItem(new Item.Settings().fireproof().attributeModifiers(KnifeItem.createAttributes()).rarity(Rarity.EPIC)), ItemGroups.TOOLS);
    public static final Item KNIFE_TACTICAL = REGISTRY.registerItem("knife_tactical", new KnifeItem(new Item.Settings().fireproof().attributeModifiers(KnifeItem.createAttributes()).rarity(Rarity.EPIC)), ItemGroups.TOOLS);

    public static final TagKey<Item> KNIVES = TagKey.of(RegistryKeys.ITEM, id("knives"));

    public static final SoundEvent KNIFEGHOST_DEATH = REGISTRY.registerSound("knifeghost.death");
    public static final SoundEvent KNIFEGHOST_HURT = REGISTRY.registerSound("knifeghost.hurt");
    public static final SoundEvent KNIFEGHOST_IDLE = REGISTRY.registerSound("knifeghost.idle");
    public static final SoundEvent KNIFEGHOST_THROW = REGISTRY.registerSound("knifeghost.throw");

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(KnifeGhostSyncPayload.ID, KnifeGhostSyncPayload.CODEC);
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