package io.github.xrickastley.originsmath.powers;

import org.slf4j.Logger;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import net.minecraft.command.EntityDataObject;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType.NbtPath;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.server.command.DataCommand;

public class NbtLinkedResourcePower extends LinkedVariableIntPower {
	private static final Logger LOGGER = OriginsMath.sublogger(NbtLinkedResourcePower.class);

	private final NbtPath path;

	private NbtLinkedResourcePower(PowerType<?> type, LivingEntity entity, NbtPath path) {
		super(type, entity);

		this.path = path;
	}

	@Override
	protected int supplyValue() {
		return (int) supplyDoubleValue();
	}

	@Override
	public double supplyDoubleValue() {
		try {
			final NbtElement element = DataCommand.getNbt(path, new EntityDataObject(entity));

			if (!(element instanceof final AbstractNbtNumber number)) throw new InvalidNbtPathException(path, "Expected a valid number value!");

			return number.doubleValue();
		} catch (Exception e) {
			LOGGER.error("Suppressed error while trying to supply a double value! \"0\" will be returned instead.", new InvalidNbtPathException(path, e.getMessage() + " for entity " + entity.toString()));

			return 0;
		}
	}

	// Overrides to prevent recursion due to serialization of Powers into NBT.
	@Override
	public NbtElement toTag() {
		return NbtInt.of(1);
	}

	@Override
	public void fromTag(NbtElement tag) {}

	private static final SerializableDataType<NbtPath> NBT_PATH = SerializableDataType.wrap(
		NbtPath.class,
		SerializableDataTypes.STRING,
		NbtPath::toString,
		str -> {
			final StringReader reader = new StringReader(str);

			reader.setCursor(0);

			try {
				return NbtPathArgumentType.nbtPath().parse(reader);
			} catch (CommandSyntaxException e) {
				throw new InvalidNbtPathException(str, e.getMessage());
			}
		}
	);

	private static class InvalidNbtPathException extends RuntimeException {
		private final String path;

		public InvalidNbtPathException(final NbtPath path, final String message) {
			this(path.toString(), message);
		}

		public InvalidNbtPathException(final String path, final String message) {
			super(message);

			this.path = path;
		}

		@Override
		public String getMessage() {
			String message = super.getMessage();
			
			message = message.substring(0, 1).toLowerCase() + message.substring(1);

			return String.format("For NBT path \"%s\", %s", path, message);
		}
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("nbt_linked_resource"),
			new SerializableData()
				.add("path", NbtLinkedResourcePower.NBT_PATH),
			data -> (powerType, livingEntity) -> new NbtLinkedResourcePower(powerType, livingEntity, data.get("path"))
		);
	}
}
