package io.github.xrickastley.originsmath.factories;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.actions.entity.*;

import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;

public class OriginsMathEntityActions {
	public static void register() {
		register(VariableExecuteCommandAction.getFactory());

		OriginsMath
			.sublogger(OriginsMathPowers.class)
			.info("Registered all base entity actions!");
	}

	private static ActionFactory<Entity> register(ActionFactory<Entity> actionFactory) {
		return Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
	}
}
