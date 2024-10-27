package io.github.xrickastley.originsmath.powers;

import java.util.function.Function;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.InstanceValueSupplier;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Pair;

/**
 * A resource who's value is based on the integer counterpart of an {@code EntityAttribute}.
 */
public class AttributeLinkedResourcePower extends SuppliedLinkedVariableIntPower<Pair<LivingEntity, EntityAttribute>> {
	private AttributeLinkedResourcePower(PowerType<?> type, LivingEntity entity, EntityAttribute attribute, AttributeValue value) {
		super(type, entity, value, () -> new Pair<LivingEntity,EntityAttribute>(entity, attribute));
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("attribute_linked_resource"),
			new SerializableData()
				.add("attribute", SerializableDataTypes.ATTRIBUTE)
				.add("value", AttributeLinkedResourcePower.ATTRIBUTE_VALUE, AttributeValue.TOTAL),
			data -> (powerType, livingEntity) -> new AttributeLinkedResourcePower(powerType, livingEntity, data.get("attribute"), data.get("value"))
		);
	}

	private static final SerializableDataType<AttributeValue> ATTRIBUTE_VALUE = SerializableDataType.enumValue(AttributeValue.class);

	private static enum AttributeValue implements InstanceValueSupplier<Pair<LivingEntity, EntityAttribute>> {
		BASE  (pair -> pair.getLeft().getAttributeBaseValue(pair.getRight())),
		TOTAL (pair -> pair.getLeft().getAttributeValue(pair.getRight()));

		private final Function<Pair<LivingEntity, EntityAttribute>, Number> supplier;

		AttributeValue(Function<Pair<LivingEntity, EntityAttribute>, Number> supplier) {
			this.supplier = supplier;
		}

		public int supplyValue(Pair<LivingEntity, EntityAttribute> entityAndAttribute) {
			return supplier
				.apply(entityAndAttribute)
				.intValue();
		}

		public Number supplyAsNumber(Pair<LivingEntity, EntityAttribute> player) {
			return supplier.apply(player);
		}
	}
}
