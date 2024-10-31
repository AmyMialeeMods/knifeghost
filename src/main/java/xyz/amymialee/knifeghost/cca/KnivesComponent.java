package xyz.amymialee.knifeghost.cca;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import xyz.amymialee.knifeghost.KnifeGhost;
import xyz.amymialee.knifeghost.entity.KnifeGhostEntity;

import java.util.ArrayList;
import java.util.List;

public class KnivesComponent implements AutoSyncedComponent {
    public static final ComponentKey<KnivesComponent> KEY = ComponentRegistry.getOrCreate(KnifeGhost.id("knives"), KnivesComponent.class);
    private final KnifeGhostEntity ghost;
    private final List<ItemStack> knives;

    public KnivesComponent(KnifeGhostEntity ghost) {
        this.ghost = ghost;
        var list = new ArrayList<>(DefaultedList.ofSize(8, ItemStack.EMPTY));
        list.replaceAll(ignored -> ghost.getRandom().nextInt(32) != 0 ? KnifeGhost.KNIFE.getDefaultStack() : KnifeGhost.getRandomKnife(ghost.getRandom()));
        this.knives = list;
    }

    private void sync() {
        KEY.sync(this.ghost);
    }

    public ItemStack getKnifeStack(int id) {
        return this.knives.get(id);
    }

    public void setKnifeStacks(List<ItemStack> stacks) {
        for (var i = 0; i < this.knives.size(); i++) this.knives.set(i, i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY);
        this.sync();
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        var list = new NbtList();
        this.knives.forEach((itemStack) -> list.add(itemStack.encode(registryLookup)));
        tag.put("knives", list);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        var list = tag.getList("knives", NbtElement.COMPOUND_TYPE);
        var knifeList = new ArrayList<>(DefaultedList.ofSize(8, ItemStack.EMPTY));
        for (var i = 0; i < knifeList.size(); i++) {
            var nbtElement = list.size() > i ? (NbtCompound) list.get(i) : new NbtCompound();
            knifeList.set(i, ItemStack.fromNbtOrEmpty(registryLookup, nbtElement));
        }
        this.setKnifeStacks(knifeList);
    }
}