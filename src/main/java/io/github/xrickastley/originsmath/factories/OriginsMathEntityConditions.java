package io.github.xrickastley.originsmath.factories;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.conditions.entity.*;

import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;

public class OriginsMathEntityConditions {
	public static void register() {
		register(CompareResourcesCondition.getFactory());
		register(EmptyResourceCondition.getFactory());
		register(FullResourceCondition.getFactory());

		OriginsMath
			.sublogger(OriginsMathPowers.class)
			.info("Registered all base entity conditions!");
	}

	private static ConditionFactory<Entity> register(ConditionFactory<Entity> conditionFactory) {
		if (ApoliRegistries.ENTITY_CONDITION.containsId(conditionFactory.getSerializerId())) return ApoliRegistries.ENTITY_CONDITION.get(conditionFactory.getSerializerId());

		return Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
	}
	
}
