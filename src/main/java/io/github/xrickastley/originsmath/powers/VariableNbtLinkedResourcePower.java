package io.github.xrickastley.originsmath.powers;

import org.slf4j.Logger;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.mixins.DataCommandAccess;
import io.github.xrickastley.originsmath.util.VariableSerializer;
import io.github.xrickastley.originsmath.util.nbt.InvalidNbtPathException;
import io.github.xrickastley.originsmath.util.nbt.VariableNbtPath;

import net.minecraft.command.EntityDataObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;

public class VariableNbtLinkedResourcePower extends LinkedVariableIntPower {
	private static final Logger LOGGER = OriginsMath.sublogger(VariableNbtLinkedResourcePower.class);

	private final VariableNbtPath path;
	private final VariableSerializer varSerializer;

	private VariableNbtLinkedResourcePower(PowerType<?> type, LivingEntity entity, VariableNbtPath path, VariableSerializer varSerializer) {
		super(type, entity);

		this.path = path;
		this.varSerializer = varSerializer;
	}

	@Override
	protected int supplyValue() {
		return (int) supplyDoubleValue();
	}

	@Override
	public double supplyDoubleValue() {
		try {
			final NbtElement element = DataCommandAccess.invokeGetNbt(path.getPathFor(this.varSerializer, this.entity), new EntityDataObject(this.entity));

			if (!(element instanceof final AbstractNbtNumber number)) throw new InvalidNbtPathException(path.toString(), "Expected a valid number value!");

			return number.doubleValue();
		} catch (Exception e) {
			LOGGER.error("Suppressed error while trying to supply a double value! \"0\" will be returned instead.", new InvalidNbtPathException(path.toString(), e.getMessage() + " for entity " + entity.toString()));

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

	private static final SerializableDataType<VariableNbtPath> VARIABLE_NBT_PATH = SerializableDataType.wrap(
		VariableNbtPath.class,
		SerializableDataTypes.STRING,
		VariableNbtPath::toString,
		VariableNbtPath::new
	);

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("variable_nbt_linked_resource"),
			new SerializableData()
				.add("path", VariableNbtLinkedResourcePower.VARIABLE_NBT_PATH)
				.add("variables", VariableSerializer.SERIALIZABLE_DATATYPE, VariableSerializer.EMPTY),
			data -> (powerType, livingEntity) -> new VariableNbtLinkedResourcePower(powerType, livingEntity, data.get("path"), data.get("variables"))
		);
	}
}
