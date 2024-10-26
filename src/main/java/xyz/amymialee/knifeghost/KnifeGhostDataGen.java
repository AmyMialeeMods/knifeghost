package xyz.amymialee.knifeghost;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.templates.MDataGen;

public class KnifeGhostDataGen extends MDataGen {
    @Override
    protected void generateTranslations(@NotNull MLanguageProvider provider, RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.@NotNull TranslationBuilder builder) {
        builder.add(KnifeGhost.KNIFE.getTranslationKey(), "Ghostly Knife");
        builder.add(KnifeGhost.KNIFE_FRENCH.getTranslationKey(), "Debonair Balisong");
        builder.add(KnifeGhost.KNIFE_LEET.getTranslationKey(), "Kirambit");
        builder.add(KnifeGhost.KNIFE_RETRO.getTranslationKey(), "Retro Knife");
        builder.add(KnifeGhost.KNIFE_SHINY.getTranslationKey(), "Ancient Dagger");
        builder.add(KnifeGhost.KNIFE_TACTICAL.getTranslationKey(), "Tactical Knife");

        builder.add(KnifeGhost.KNIFE_GHOST_ENTITY.getTranslationKey(), "Knife Ghost");
        builder.add(KnifeGhost.KNIFE_ENTITY.getTranslationKey(), "Knife");

        var knifeKeys = provider.getDamageKeys(KnifeGhost.KNIFE_DAMAGE.getValue());
        builder.add(knifeKeys[0], "%1$s was stabbed");
        builder.add(knifeKeys[1], "%1$s was stabbed by %2$s");
        builder.add(knifeKeys[2], "%1$s was stabbed by %2$s using %3$s");

        builder.add(provider.getSubtitleKey(KnifeGhost.KNIFEGHOST_DEATH), "Knife Ghost clatters");
        builder.add(provider.getSubtitleKey(KnifeGhost.KNIFEGHOST_HURT), "Knife Ghost rattles");
        builder.add(provider.getSubtitleKey(KnifeGhost.KNIFEGHOST_IDLE), "Knife Ghost jingles");
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
    }

    @Override
    protected void generateEntityTypeTags(@NotNull MEntityTypeTagProvider provider, RegistryWrapper.WrapperLookup arg) {
        provider.getOrCreateTagBuilder(EntityTypeTags.UNDEAD).add(KnifeGhost.KNIFE_GHOST_ENTITY);
        provider.getOrCreateTagBuilder(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(KnifeGhost.KNIFE_GHOST_ENTITY);
        provider.getOrCreateTagBuilder(EntityTypeTags.WITHER_FRIENDS).add(KnifeGhost.KNIFE_GHOST_ENTITY);
        provider.getOrCreateTagBuilder(EntityTypeTags.SENSITIVE_TO_SMITE).add(KnifeGhost.KNIFE_GHOST_ENTITY);
        provider.getOrCreateTagBuilder(EntityTypeTags.REDIRECTABLE_PROJECTILE).add(KnifeGhost.KNIFE_ENTITY);
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
        generator.register(KnifeGhost.KNIFE, Models.HANDHELD);
        generator.register(KnifeGhost.KNIFE_FRENCH, Models.HANDHELD);
        generator.register(KnifeGhost.KNIFE_LEET, Models.HANDHELD);
        generator.register(KnifeGhost.KNIFE_RETRO, Models.HANDHELD);
        generator.register(KnifeGhost.KNIFE_SHINY, Models.HANDHELD);
        generator.register(KnifeGhost.KNIFE_TACTICAL, Models.HANDHELD);
    }
}