package io.github.xrickastley.originsmath.conditions.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.powers.LinkedVariableIntPower;

import net.minecraft.entity.Entity;

public class RelativeResourceCondition {
	private static boolean condition(SerializableData.Instance data, Entity entity) {
		try {
			final Comparison comparison = data.get("comparison");
			final PowerType<?> resource = data.get("resource");
			final double relativity = data.getDouble("relativity");
			
			return comparison.compare(getRelativeValue(entity, resource), relativity);
		} catch (Exception e) {
			return false;
		}
    }

	private static double getRelativeValue(Entity entity, PowerType<?> powerType) {
		final Power power = powerType.get(entity);

		if (power instanceof final LinkedVariableIntPower lvip) return lvip.supplyDoubleValue() / lvip.getMax();
		else if (power instanceof final VariableIntPower vip) return vip.getValue() / vip.getMax();
		else if (power instanceof final CooldownPower cp) return cp.getProgress();
		else throw new RuntimeException(String.format("Attempted to use invalid power type \"%s\" as a resource!", powerType.getIdentifier().toString()));
	}

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(
            OriginsMath.identifier("relative_resource"),
            new SerializableData()
                .add("resource", ApoliDataTypes.POWER_TYPE)
                .add("comparison", ApoliDataTypes.COMPARISON)
                .add("relativity", SerializableDataTypes.DOUBLE),
            RelativeResourceCondition::condition
        );
    }
}
