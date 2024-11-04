package io.github.xrickastley.originsmath.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.BiConsumer;
import java.util.function.Function;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.xrickastley.originsmath.commands.ResourceCommand;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

public class ResourceBacked<T extends Number> extends Number {
	private final PowerType<?> powerType;
	private final T number;
	private Entity targetEntity;

	public static ResourceBacked<Double> fromPowerType(final PowerType<?> powerType) {
		return new ResourceBacked<Double>(powerType);
	}

	public static <T extends Number> ResourceBacked<T> fromPowerType(final PowerType<?> powerType, final Class<T> numberClass) {
		return new ResourceBacked<T>(powerType);
	}

	public static <T extends Number> ResourceBacked<T> fromNumber(final T number) {
		return new ResourceBacked<T>(number);
	}

	private ResourceBacked(final PowerType<?> powerType) {
		this.powerType = powerType;
		this.number = null;
	}

	private ResourceBacked(final T number) {
		this.powerType = null;
		this.number = number;
	}

	private Number getValue() {
		return this.powerType != null
			? this.targetEntity != null
				? ResourceCommand.getAbsoluteValue(powerType.get(targetEntity))
				: 0
			: this.number != null
				? this.number
				: 0;
	}

	public void setTargetEntity(Entity entity) {
		this.targetEntity = entity;
	}

	@Override
	public int intValue() {
		return getValue().intValue();
	}

	@Override
	public double doubleValue() {
		return getValue().doubleValue();
	}

	@Override
	public float floatValue() {
		return getValue().floatValue();
	}

	@Override
	public long longValue() {
		return (long) getValue();
	}

	private static <T extends Number> BiConsumer<PacketByteBuf, ResourceBacked<T>> createSendFn(Class<T> numberClass, BiConsumer<PacketByteBuf, T> sendToPacket) {
		return (packet, rb) -> {
			if (rb.number != null) {
				packet.writeByte(0);
				sendToPacket.accept(packet, rb.number);
			} else {
				packet.writeByte(1);
				ApoliDataTypes.POWER_TYPE.send(packet, rb.powerType);
			}
		};
	}

	private static <T extends Number> Function<PacketByteBuf, ResourceBacked<T>> createReceiveFn(Class<T> numberClass, Function<PacketByteBuf, T> receiveFromPacket) {
		return packet -> {
			int type = packet.readByte();

			switch (type) {
				case 0:
					return ResourceBacked.fromNumber(receiveFromPacket.apply(packet));
				case 1:
					PowerType<?> powerType = ApoliDataTypes.POWER_TYPE.receive(packet);

					return ResourceBacked.fromPowerType(powerType, numberClass);
				default:
					throw new UnsupportedOperationException(ResourceBacked.class.getSimpleName());
			}
		};
	}

	private static <T extends Number> Function<JsonElement, ResourceBacked<T>> createReadFn(Class<T> numberClass, Function<JsonPrimitive, T> readFromJson) {
		return json -> {
			if (!(json instanceof JsonPrimitive jsonPrimitive)) throw new UnsupportedOperationException(JsonElement.class.getSimpleName());

			if (jsonPrimitive.isNumber()) {
				return ResourceBacked.fromNumber(readFromJson.apply(jsonPrimitive));
			} else {
				PowerType<?> powerType = ApoliDataTypes.POWER_TYPE.read(json);

				return ResourceBacked.fromPowerType(powerType, numberClass);
			}
		};
	}

	private static <T extends Number> JsonElement write(ResourceBacked<T> resourceBacked) {
		return resourceBacked.powerType == null
			? new JsonPrimitive(resourceBacked.number)
			: ApoliDataTypes.POWER_TYPE.write(((PowerTypeReference<?>) resourceBacked.powerType));
	}

	public static interface DataTypes {
		public static SerializableDataType<ResourceBacked<Integer>> RESOURCE_BACKED_INT = new SerializableDataType<>(
			ClassUtil.castClass(ResourceBacked.class),
			ResourceBacked.createSendFn(Integer.class, PacketByteBuf::writeInt),
			ResourceBacked.createReceiveFn(Integer.class, PacketByteBuf::readInt),
			ResourceBacked.createReadFn(Integer.class, JsonPrimitive::getAsInt),
			ResourceBacked::write
		);

		public static SerializableDataType<ResourceBacked<Float>> RESOURCE_BACKED_FLOAT = new SerializableDataType<>(
			ClassUtil.castClass(ResourceBacked.class),
			ResourceBacked.createSendFn(Float.class, PacketByteBuf::writeFloat),
			ResourceBacked.createReceiveFn(Float.class, PacketByteBuf::readFloat),
			ResourceBacked.createReadFn(Float.class, JsonPrimitive::getAsFloat),
			ResourceBacked::write
		);

		public static SerializableDataType<ResourceBacked<Double>> RESOURCE_BACKED_DOUBLE = new SerializableDataType<>(
			ClassUtil.castClass(ResourceBacked.class),
			ResourceBacked.createSendFn(Double.class, PacketByteBuf::writeDouble),
			ResourceBacked.createReceiveFn(Double.class, PacketByteBuf::readDouble),
			ResourceBacked.createReadFn(Double.class, JsonPrimitive::getAsDouble),
			ResourceBacked::write
		);
	}
}
