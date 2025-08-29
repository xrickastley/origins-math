package io.github.xrickastley.originsmath.networking.s2c;

import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.config.OriginsMathConfig;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record SyncRequiredConfigPacket(boolean isDedicatedServer, boolean experimentASM) implements FabricPacket {
	public static final PacketType<SyncRequiredConfigPacket> TYPE = PacketType.create(
		OriginsMath.identifier("s2c/sync_required_config"), SyncRequiredConfigPacket::read
	);

	public static SyncRequiredConfigPacket create(boolean isDedicatedServer) {
		return new SyncRequiredConfigPacket(
			isDedicatedServer,
			OriginsMathConfig.Experiments.EXTENDED_COMPATIBILITY_THROUGH_ASM.getValue()
		);
	}

	private static SyncRequiredConfigPacket read(PacketByteBuf buffer) {
		return new SyncRequiredConfigPacket(buffer.readBoolean(), buffer.readBoolean());
	}

	private <T> void syncConfig(OriginsMathConfig.Entry<T> entry, T value) {
		if (isDedicatedServer) {
			entry.setValueFromServer(value);
		} else {
			entry.setValue(value);
		}
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeBoolean(isDedicatedServer);
		buffer.writeBoolean(experimentASM);
	}

	public void sync() {
		syncConfig(OriginsMathConfig.Experiments.EXTENDED_COMPATIBILITY_THROUGH_ASM, experimentASM);
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}
