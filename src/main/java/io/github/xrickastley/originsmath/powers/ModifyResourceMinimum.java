package io.github.xrickastley.originsmath.powers;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.xrickastley.originsmath.OriginsMath;

import net.minecraft.entity.LivingEntity;

public class ModifyResourceMinimum extends ResourceBoundModifyingPower {
	private ModifyResourceMinimum(PowerType<?> type, LivingEntity entity, PowerType<?> resource) {
        super(type, entity, resource);
    }

	public static PowerFactory<?> createFactory() {
		return ResourceBoundModifyingPower.createResourceModifyingFactory(
			OriginsMath.identifier("modify_resource_minimum"),
			ModifyResourceMinimum::new
		);
	}
}
