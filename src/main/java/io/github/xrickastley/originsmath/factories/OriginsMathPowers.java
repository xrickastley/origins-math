package io.github.xrickastley.originsmath.factories;

import java.util.function.Supplier;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.powers.*;

import net.minecraft.registry.Registry;

public class OriginsMathPowers {
	public static void register() {
		register(AttributeLinkedResourcePower::createFactory);
		register(CurrentBiomeLinkedResourcePower::createFactory);
		register(DamageDealtLinkedResourcePower::createFactory);
		register(DamageTakenLinkedResourcePower::createFactory);
		register(HealingLinkedResourcePower::createFactory);
		register(MathResourcePower::createFactory);
		register(ModifiableResourcePower::createFactory);
		register(ModifyResourceMaximumPower::createFactory);
		register(ModifyResourceMinimumPower::createFactory);
		register(PlayerLinkedResourcePower::createFactory);
		register(ScoreboardLinkedResourcePower::createFactory);
		register(SimpleModifyingPower::createFactory);
		register(StatusEffectLinkedResourcePower::createFactory);

		OriginsMath
			.sublogger(OriginsMathPowers.class)
			.info("Registered all powers!");
	}

    private static PowerFactory<?> register(Supplier<PowerFactory<?>> factorySupplier) {
        return register(factorySupplier.get());
    }

	private static PowerFactory<?> register(PowerFactory<?> powerFactory) {
		if (ApoliRegistries.POWER_FACTORY.containsId(powerFactory.getSerializerId())) return ApoliRegistries.POWER_FACTORY.get(powerFactory.getSerializerId());

		return Registry.register(ApoliRegistries.POWER_FACTORY, powerFactory.getSerializerId(), powerFactory);
	}
}
