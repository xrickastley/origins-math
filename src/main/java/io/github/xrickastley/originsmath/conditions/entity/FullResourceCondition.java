package io.github.xrickastley.originsmath.conditions.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.ValueProviders.ValueProvider;
import io.github.xrickastley.originsmath.util.ValueProviders;

import net.minecraft.entity.Entity;

public class FullResourceCondition {
	private static boolean condition(SerializableData.Instance data, Entity entity) {
		final PowerType<?> powerType = data.get("resource");
		final Power power = powerType.get(entity);

		final ValueProvider<Power> provider = ValueProviders.getProviderOrThrow(powerType, entity);

		return provider.VALUE_PROVIDER.apply(power) >= provider.MAX_PROVIDER.apply(power);
	}

	public static ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsMath.identifier("full_resource"),
			new SerializableData()
				.add("resource", ApoliDataTypes.POWER_TYPE),
				FullResourceCondition::condition
		);
	}
}
