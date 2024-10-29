package io.github.xrickastley.originsmath.conditions.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;

import net.minecraft.entity.Entity;

public class FullResourceCondition {
    private static boolean condition(SerializableData.Instance data, Entity entity) {
		final PowerType<?> powerType = data.get("resource");
		final Power power = powerType.get(entity);

		if (power instanceof final VariableIntPower vip) {
			return vip.getValue() >= vip.getMax();
		} else if (power instanceof final CooldownPower cp) {
			return cp.getRemainingTicks() >= cp.cooldownDuration;
		} else throw new RuntimeException(String.format("Attempted to use invalid power type \"%s\" as a resource!", power.getType().getIdentifier().toString()));
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
