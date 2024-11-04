package io.github.xrickastley.originsmath.powers;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.xrickastley.originsmath.OriginsMath;

import net.minecraft.entity.LivingEntity;

public class SimpleModifyingPower extends ValueModifyingPower {
	public SimpleModifyingPower(PowerType<?> powerType, LivingEntity entity) {
		super(powerType, entity);
	}

	public static PowerFactory<?> createFactory() {
		return ValueModifyingPower.createValueModifyingFactory(
			SimpleModifyingPower::new,
			OriginsMath.identifier("simple_modify")
		);
	}
}
