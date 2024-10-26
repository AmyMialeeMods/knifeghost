package xyz.amymialee.knifeghost.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.knifeghost.KnifeGhost;

import java.util.List;

public record KnifeGhostSyncPayload(int id, List<ItemStack> stacks) implements CustomPayload {
    public static final Id<KnifeGhostSyncPayload> ID = new Id<>(KnifeGhost.id("knife_ghost_sync"));
    public static final PacketCodec<RegistryByteBuf, KnifeGhostSyncPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, KnifeGhostSyncPayload::id, ItemStack.LIST_PACKET_CODEC, KnifeGhostSyncPayload::stacks, KnifeGhostSyncPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<KnifeGhostSyncPayload> {
        @Override
        public void receive(@NotNull KnifeGhostSyncPayload payload, ClientPlayNetworking.@NotNull Context context) {
            var world = context.client().world;
            if (world == null) return;
            var ghost = world.getEntityById(payload.id);
            if (ghost == null) return;
//            ghost.setStacks(payload.stacks);
        }
    }
}