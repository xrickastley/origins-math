package io.github.xrickastley.originsmath;

import org.slf4j.Logger;

import io.github.xrickastley.originsmath.config.OriginsMathConfig;
import io.github.xrickastley.originsmath.networking.s2c.SyncRequiredConfigPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class OriginsMathClient implements ClientModInitializer {
	public static final Logger LOGGER = OriginsMath.sublogger("Client");

	@Override
	public void onInitializeClient() {
		OriginsMathClient.LOGGER.info("Origins: Math (Client) Initialized!");

		ClientPlayConnectionEvents.INIT.register(OriginsMathClient::registerClientReceivers);
		ClientPlayConnectionEvents.DISCONNECT.register(OriginsMathClient::onDisconnect);
	}

	public static void registerClientReceivers(ClientPlayNetworkHandler handler, MinecraftClient client) {
		ClientPlayNetworking.registerGlobalReceiver(SyncRequiredConfigPacket.TYPE, (packet, player, sender) -> {
			LOGGER.info("Syncing Origins: Math config from server!");

			packet.sync();
		});
	}

	public static void onDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
		OriginsMathConfig.Entry.desyncEntries();
	}
}