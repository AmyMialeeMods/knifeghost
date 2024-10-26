package xyz.amymialee.knifeghost;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xyz.amymialee.knifeghost.network.KnifeGhostSyncPayload;

public class KnifeGhostClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(KnifeGhostSyncPayload.ID, new KnifeGhostSyncPayload.Receiver());
    }
}