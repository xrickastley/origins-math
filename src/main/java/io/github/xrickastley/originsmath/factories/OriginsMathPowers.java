package io.github.xrickastley.originsmath.factories;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.powers.*;
import net.minecraft.registry.Registry;

public class OriginsMathPowers {
	public static void register() {
		register(AttributeLinkedResourcePower.createFactory());
		register(PlayerLinkedResourcePower.createFactory());
		register(MathResourcePower.createFactory());
		register(ScoreboardLinkedResourcePower.createFactory());
		register(StatusEffectLinkedResourcePower.createFactory());

		OriginsMath
			.sublogger(OriginsMathPowers.class)
			.info("Registered all powers!");
	}

	private static PowerFactory<?> register(PowerFactory<?> powerFactory) {
		return Registry.register(ApoliRegistries.POWER_FACTORY, powerFactory.getSerializerId(), powerFactory);
	}
}
