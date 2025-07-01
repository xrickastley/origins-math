package io.github.xrickastley.originsmath.powers;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.xrickastley.originsmath.OriginsMath;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class ModifyAttributeLikeResourcePower extends ResourceModifyingPower {
	private ModifyAttributeLikeResourcePower(PowerType<?> type, LivingEntity entity, PowerType<?> resource) {
		super(type, entity, resource);
	}

	@Override
	public boolean appliesToResource(Identifier id) {
		return super.appliesToResource(id) && this.isActive();
	}

	public static PowerFactory<?> createFactory() {
		return ResourceModifyingPower.createResourceModifyingFactory(
			OriginsMath.identifier("modify_attribute_like_resource"),
			ModifyAttributeLikeResourcePower::new
		).allowCondition();
	}
}
