package io.github.xrickastley.originsmath;

import org.slf4j.Logger;

import io.github.xrickastley.originsmath.networking.s2c.SyncRequiredConfigPacket;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class OriginsMathServer implements DedicatedServerModInitializer {
	public static final Logger LOGGER = OriginsMath.sublogger("Server");

	@Override
	public void onInitializeServer() {
		OriginsMathServer.LOGGER.info("Origins: Math (Server) Initialized!");

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			OriginsMathServer.LOGGER.info("Sending config sync packet to {}...", handler.player.getName().getString());

			ServerPlayNetworking.send(handler.player, SyncRequiredConfigPacket.create(server.isDedicated()));
		});
	}
}
