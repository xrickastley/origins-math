package io.github.xrickastley.originsmath.factories;

import java.util.function.Supplier;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.actions.bientity.VariableExecuteCommandAction;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;

public class OriginsMathBientityActions {
	public static void register() {
		register(VariableExecuteCommandAction::getFactory);

		OriginsMath
			.sublogger(OriginsMathPowers.class)
			.info("Registered all base bi-entity actions!");
	}

	private static ActionFactory<Pair<Entity, Entity>> register(Supplier<ActionFactory<Pair<Entity, Entity>>> factorySupplier) {
		return register(factorySupplier.get());
	}

	private static ActionFactory<Pair<Entity, Entity>> register(ActionFactory<Pair<Entity, Entity>> actionFactory) {
		if (ApoliRegistries.BIENTITY_ACTION.containsId(actionFactory.getSerializerId())) return ApoliRegistries.BIENTITY_ACTION.get(actionFactory.getSerializerId());

		return Registry.register(ApoliRegistries.BIENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
	}
}
