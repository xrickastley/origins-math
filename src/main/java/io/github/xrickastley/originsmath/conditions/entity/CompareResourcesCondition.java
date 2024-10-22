package io.github.xrickastley.originsmath.conditions.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.powers.LinkedVariableIntPower;

import net.minecraft.entity.Entity;

public class CompareResourcesCondition {
    private static boolean condition(SerializableData.Instance data, Entity entity) {
		try {
			final Comparison comparison = data.get("comparison");
	
			return comparison.compare(
				obtainResourceValue(entity, data.get("left_resource")),
				obtainResourceValue(entity, data.get("right_resource"))
			);
		} catch (Exception e) {
			return false;
		}
    }

	private static double obtainResourceValue(Entity entity, PowerType<?> powerType) {
		final Power power = powerType.get(entity);

		if (power instanceof final LinkedVariableIntPower lvip) return lvip.supplyDoubleValue();
		else if (power instanceof final VariableIntPower vip) return vip.getValue();
		else if (power instanceof final CooldownPower cp) return cp.getRemainingTicks();
		else throw new RuntimeException(String.format("Attempted to use invalid power type \"%s\" as a resource!", power.getType().getIdentifier().toString()));
	}

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(
            OriginsMath.identifier("compare_resources"),
            new SerializableData()
                .add("left_resource", ApoliDataTypes.POWER_TYPE)
                .add("comparison", ApoliDataTypes.COMPARISON)
                .add("right_resource", ApoliDataTypes.POWER_TYPE),
            CompareResourcesCondition::condition
        );
    }
}
