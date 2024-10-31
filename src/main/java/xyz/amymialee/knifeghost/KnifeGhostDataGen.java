package xyz.amymialee.knifeghost;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.templates.MDataGen;

import java.util.Optional;
import java.util.function.BiConsumer;

public class KnifeGhostDataGen extends MDataGen {
    public static final Model HANDHELD_KNIFE = new Model(Optional.of(KnifeGhost.id("item/handheld_knife")), Optional.empty(), TextureKey.LAYER0);

    @Override
    protected void generateTranslations(@NotNull MLanguageProvider provider, RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.@NotNull TranslationBuilder builder) {
        builder.add(SpawnEggItem.forEntity(KnifeGhost.KNIFE_GHOST_ENTITY), "Knife Ghost Spawn Egg");
        builder.add(KnifeGhost.KNIFE.getTranslationKey(), "Ghostly Knife");
        builder.add(KnifeGhost.KNIFE_FRENCH.getTranslationKey(), "Debonair Balisong");
        builder.add(KnifeGhost.KNIFE_LEET.getTranslationKey(), "Kirambit");
        builder.add(KnifeGhost.KNIFE_RETRO.getTranslationKey(), "Retro Knife");
        builder.add(KnifeGhost.KNIFE_SHINY.getTranslationKey(), "Ancient Dagger");
        builder.add(KnifeGhost.KNIFE_TACTICAL.getTranslationKey(), "Tactical Knife");

        builder.add(KnifeGhost.SWORD_PUMPKIN.getTranslationKey(), "Poorly Carved Pumpkin");
        builder.add(KnifeGhost.GHOST_PUMPKIN.getTranslationKey(), "Ghostly Carved Pumpkin");
        builder.add(KnifeGhost.KNIFE_PUMPKIN.getTranslationKey(), "Knife Carved Pumpkin");
        builder.add(KnifeGhost.SWORD_O_LANTERN.getTranslationKey(), "Crooked Jack o'Lantern");
        builder.add(KnifeGhost.GHOST_O_LANTERN.getTranslationKey(), "Ghostly Jack o'Lantern");
        builder.add(KnifeGhost.KNIFE_O_LANTERN.getTranslationKey(), "Sharp Jack o'Lantern");

        builder.add(KnifeGhost.KNIFE_GHOST_ENTITY.getTranslationKey(), "Knife Ghost");
        builder.add(KnifeGhost.KNIFE_ENTITY.getTranslationKey(), "Knife");

        var knifeKeys = provider.getDamageKeys(KnifeGhost.KNIFE_DAMAGE.getValue());
        builder.add(knifeKeys[0], "%1$s was stabbed");
        builder.add(knifeKeys[1], "%1$s was stabbed by %2$s");
        builder.add(knifeKeys[2], "%1$s was stabbed by %2$s using %3$s");

        builder.add(provider.getSubtitleKey(KnifeGhost.KNIFEGHOST_CHARGE), "Knife Ghost charges");
        builder.add(provider.getSubtitleKey(KnifeGhost.KNIFEGHOST_DEATH), "Knife Ghost clatters");
        builder.add(provider.getSubtitleKey(KnifeGhost.KNIFEGHOST_HURT), "Knife Ghost rattles");
        builder.add(provider.getSubtitleKey(KnifeGhost.KNIFEGHOST_IDLE), "Knife Ghost jingles");
        builder.add(provider.getSubtitleKey(KnifeGhost.KNIFEGHOST_IMPACT), "Knife stabs");
        builder.add(provider.getSubtitleKey(KnifeGhost.KNIFEGHOST_THROW), "Knife Ghost chucks");
    }

    @Override
    protected void generateItemTags(@NotNull MItemTagProvider provider, RegistryWrapper.WrapperLookup arg) {
        provider.getOrCreateTagBuilder(KnifeGhost.KNIVES)
                .add(KnifeGhost.KNIFE)
                .add(KnifeGhost.KNIFE_FRENCH)
                .add(KnifeGhost.KNIFE_LEET)
                .add(KnifeGhost.KNIFE_RETRO)
                .add(KnifeGhost.KNIFE_SHINY)
                .add(KnifeGhost.KNIFE_TACTICAL);
        provider.getOrCreateTagBuilder(ConventionalItemTags.ENCHANTABLES).addTag(KnifeGhost.KNIVES);
        provider.getOrCreateTagBuilder(ConventionalItemTags.MINING_TOOL_TOOLS).addTag(KnifeGhost.KNIVES);
        provider.getOrCreateTagBuilder(ConventionalItemTags.MELEE_WEAPON_TOOLS).addTag(KnifeGhost.KNIVES);
        provider.getOrCreateTagBuilder(ConventionalItemTags.TOOLS).addTag(KnifeGhost.KNIVES);
        provider.getOrCreateTagBuilder(ItemTags.MINING_LOOT_ENCHANTABLE).addTag(KnifeGhost.KNIVES);
        provider.getOrCreateTagBuilder(ItemTags.MINING_ENCHANTABLE).addTag(KnifeGhost.KNIVES);
        provider.getOrCreateTagBuilder(ItemTags.WEAPON_ENCHANTABLE).addTag(KnifeGhost.KNIVES);
        provider.getOrCreateTagBuilder(ItemTags.SHARP_WEAPON_ENCHANTABLE).addTag(KnifeGhost.KNIVES);
        provider.getOrCreateTagBuilder(ItemTags.FIRE_ASPECT_ENCHANTABLE).addTag(KnifeGhost.KNIVES);
        provider.getOrCreateTagBuilder(ItemTags.BREAKS_DECORATED_POTS).addTag(KnifeGhost.KNIVES);
        provider.getOrCreateTagBuilder(ItemTags.EQUIPPABLE_ENCHANTABLE)
                .add(KnifeGhost.SWORD_PUMPKIN.asItem())
                .add(KnifeGhost.GHOST_PUMPKIN.asItem())
                .add(KnifeGhost.KNIFE_PUMPKIN.asItem());
        provider.getOrCreateTagBuilder(ItemTags.VANISHING_ENCHANTABLE)
                .add(KnifeGhost.SWORD_PUMPKIN.asItem())
                .add(KnifeGhost.GHOST_PUMPKIN.asItem())
                .add(KnifeGhost.KNIFE_PUMPKIN.asItem())
                .addTag(KnifeGhost.KNIVES);
    }

    @Override
    protected void generateBlockTags(@NotNull MBlockTagProvider provider, RegistryWrapper.WrapperLookup arg) {
        provider.getOrCreateTagBuilder(BlockTags.ENDERMAN_HOLDABLE)
                .add(KnifeGhost.SWORD_PUMPKIN)
                .add(KnifeGhost.GHOST_PUMPKIN)
                .add(KnifeGhost.KNIFE_PUMPKIN);
        provider.getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(KnifeGhost.SWORD_PUMPKIN)
                .add(KnifeGhost.GHOST_PUMPKIN)
                .add(KnifeGhost.KNIFE_PUMPKIN)
                .add(KnifeGhost.SWORD_O_LANTERN)
                .add(KnifeGhost.GHOST_O_LANTERN)
                .add(KnifeGhost.KNIFE_O_LANTERN);
        provider.getOrCreateTagBuilder(BlockTags.SWORD_EFFICIENT)
                .add(KnifeGhost.SWORD_PUMPKIN)
                .add(KnifeGhost.GHOST_PUMPKIN)
                .add(KnifeGhost.KNIFE_PUMPKIN)
                .add(KnifeGhost.SWORD_O_LANTERN)
                .add(KnifeGhost.GHOST_O_LANTERN)
                .add(KnifeGhost.KNIFE_O_LANTERN);
    }

    @Override
    protected void generateEntityTypeTags(@NotNull MEntityTypeTagProvider provider, RegistryWrapper.WrapperLookup arg) {
        provider.getOrCreateTagBuilder(EntityTypeTags.UNDEAD).add(KnifeGhost.KNIFE_GHOST_ENTITY);
        provider.getOrCreateTagBuilder(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(KnifeGhost.KNIFE_GHOST_ENTITY);
        provider.getOrCreateTagBuilder(EntityTypeTags.WITHER_FRIENDS).add(KnifeGhost.KNIFE_GHOST_ENTITY);
        provider.getOrCreateTagBuilder(EntityTypeTags.SENSITIVE_TO_SMITE).add(KnifeGhost.KNIFE_GHOST_ENTITY);
    }

    @Override
    protected void generateDamageTypeTags(@NotNull MDamageTypeTagProvider provider, RegistryWrapper.WrapperLookup arg) {
        provider.getOrCreateTagBuilder(DamageTypeTags.BYPASSES_COOLDOWN).addOptional(KnifeGhost.KNIFE_DAMAGE);
        provider.getOrCreateTagBuilder(DamageTypeTags.IS_PROJECTILE).addOptional(KnifeGhost.KNIFE_DAMAGE);
        provider.getOrCreateTagBuilder(DamageTypeTags.NO_IMPACT).addOptional(KnifeGhost.KNIFE_DAMAGE);
        provider.getOrCreateTagBuilder(DamageTypeTags.NO_KNOCKBACK).addOptional(KnifeGhost.KNIFE_DAMAGE);
    }

    @Override
    protected void generateItemModels(MModelProvider provider, @NotNull ItemModelGenerator generator) {
        generator.register(KnifeGhost.KNIFE, HANDHELD_KNIFE);
        generator.register(KnifeGhost.KNIFE_FRENCH, HANDHELD_KNIFE);
        generator.register(KnifeGhost.KNIFE_LEET, HANDHELD_KNIFE);
        generator.register(KnifeGhost.KNIFE_RETRO, HANDHELD_KNIFE);
        generator.register(KnifeGhost.KNIFE_SHINY, HANDHELD_KNIFE);
        generator.register(KnifeGhost.KNIFE_TACTICAL, HANDHELD_KNIFE);
        generator.register(SpawnEggItem.forEntity(KnifeGhost.KNIFE_GHOST_ENTITY), MModelProvider.SPAWN_EGG);
    }

    @Override
    protected void generateBlockStateModels(MModelProvider provider, @NotNull BlockStateModelGenerator generator) {
        var textureMap = TextureMap.sideEnd(Blocks.PUMPKIN);
        generator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.PUMPKIN, ModelIds.getBlockModelId(Blocks.PUMPKIN)));
        generator.registerNorthDefaultHorizontalRotatable(KnifeGhost.SWORD_PUMPKIN, textureMap);
        generator.registerNorthDefaultHorizontalRotatable(KnifeGhost.GHOST_PUMPKIN, textureMap);
        generator.registerNorthDefaultHorizontalRotatable(KnifeGhost.KNIFE_PUMPKIN, textureMap);
        generator.registerNorthDefaultHorizontalRotatable(KnifeGhost.SWORD_O_LANTERN, textureMap);
        generator.registerNorthDefaultHorizontalRotatable(KnifeGhost.GHOST_O_LANTERN, textureMap);
        generator.registerNorthDefaultHorizontalRotatable(KnifeGhost.KNIFE_O_LANTERN, textureMap);
    }

    @Override
    protected void generateBlockLootTables(@NotNull MBlockLootTableProvider provider) {
        provider.addDrop(KnifeGhost.SWORD_PUMPKIN);
        provider.addDrop(KnifeGhost.GHOST_PUMPKIN);
        provider.addDrop(KnifeGhost.KNIFE_PUMPKIN);
        provider.addDrop(KnifeGhost.SWORD_O_LANTERN);
        provider.addDrop(KnifeGhost.GHOST_O_LANTERN);
        provider.addDrop(KnifeGhost.KNIFE_O_LANTERN);
    }

    @Override
    protected void generateRecipes(MRecipeProvider provider, RecipeExporter exporter) {
        BiConsumer<ItemConvertible, ItemConvertible> consumer = (input, output) -> ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output)
                .input('A', input)
                .input('B', Blocks.TORCH)
                .pattern("A")
                .pattern("B")
                .criterion("has_carved_pumpkin", RecipeProvider.conditionsFromItem(input))
                .offerTo(exporter);
        consumer.accept(KnifeGhost.SWORD_PUMPKIN, KnifeGhost.SWORD_O_LANTERN);
        consumer.accept(KnifeGhost.GHOST_PUMPKIN, KnifeGhost.GHOST_O_LANTERN);
        consumer.accept(KnifeGhost.KNIFE_PUMPKIN, KnifeGhost.KNIFE_O_LANTERN);
    }

    @Override
    protected void generateDamageTypes(MDamageTypeProvider provider, RegistryWrapper.WrapperLookup registries, FabricDynamicRegistryProvider.@NotNull Entries entries) {
        entries.add(KnifeGhost.KNIFE_DAMAGE, new DamageType(KnifeGhost.KNIFE_DAMAGE.getValue().toTranslationKey(), 0.1f));
    }
}