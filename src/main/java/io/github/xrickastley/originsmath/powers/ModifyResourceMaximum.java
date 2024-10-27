package io.github.xrickastley.originsmath.powers;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.xrickastley.originsmath.OriginsMath;

import net.minecraft.entity.LivingEntity;

public class ModifyResourceMaximum extends ValueModifyingPower {
	private ModifyResourceMaximum(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

	public static PowerFactory<?> createFactory() {
		return ValueModifyingPower.createValueModifyingFactory(ModifyResourceMaximum::new, OriginsMath.identifier("modify_resource_maximum"));
	}
}
