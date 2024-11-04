package io.github.xrickastley.originsmath.conditions.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.commands.ResourceCommand;

import net.minecraft.entity.Entity;

public class CompareResourcesCondition {
    private static boolean condition(SerializableData.Instance data, Entity entity) {
		try {
			final Comparison comparison = data.get("comparison");

			return comparison.compare(
				ResourceCommand.obtainResourceValue(entity, data.get("left_resource")),
				ResourceCommand.obtainResourceValue(entity, data.get("right_resource"))
			);
		} catch (Exception e) {
			return false;
		}
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
