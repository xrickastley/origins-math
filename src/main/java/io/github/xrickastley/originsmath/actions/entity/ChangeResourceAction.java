package io.github.xrickastley.originsmath.actions.entity;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.ResourceBacked;
import io.github.xrickastley.originsmath.util.ValueProviders.ValueModifier;
import io.github.xrickastley.originsmath.util.ValueProviders;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class ChangeResourceAction {
	private static void action(SerializableData.Instance data, Entity entity) {
		if (!(entity instanceof LivingEntity)) return;

		final PowerType<?> powerType = data.get("resource");
		final ResourceOperation operation = data.get("operation");
		final ResourceBacked<?> change = data.get("change");

		final PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
		final Power power = component.getPower(powerType);

		final ValueModifier<Power> modifier = ValueProviders.getModifierOrThrow(powerType, entity);

		if (operation == ResourceOperation.ADD) {
			modifier.ADD_MODIFIER.accept(power, change);
		} else {
			modifier.SET_MODIFIER.accept(power, change);
		}

		PowerHolderComponent.syncPower(entity, powerType);
	}

	public static ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsMath.identifier("change_resource"),
			new SerializableData()
				.add("resource", ApoliDataTypes.POWER_TYPE)
				.add("change", ResourceBacked.DataTypes.RESOURCE_BACKED_DOUBLE)
				.add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD),
			ChangeResourceAction::action
		);
	}
}
