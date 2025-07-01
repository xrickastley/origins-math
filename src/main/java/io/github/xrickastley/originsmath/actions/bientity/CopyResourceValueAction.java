package io.github.xrickastley.originsmath.actions.bientity;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.ValueProviders;
import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;

public class CopyResourceValueAction {
	private static void action(SerializableData.Instance data, Pair<Entity, Entity> entity) {
		final Entity actor = entity.getLeft();
		final Entity target = entity.getRight();
		
		final PowerType<Power> actorResource = data.get("actor_resource");
		final PowerType<Power> targetResource = data.get("target_resource");
		final ResourceOperation operation = data.get("operation");

		final double change = ValueProviders.getValueOrThrow(targetResource, target).doubleValue();

		ValueProviders
			.getModifierOrThrow(actorResource, actor)
			.modify(operation, actorResource, actor, change);
		
		PowerHolderComponent.syncPower(actor, actorResource);
	}

	public static ActionFactory<Pair<Entity, Entity>> getFactory() {
		return new ActionFactory<>(
			OriginsMath.identifier("copy_resource_value"),
			new SerializableData()
				.add("actor_resource", ApoliDataTypes.POWER_TYPE)
				.add("target_resource", ApoliDataTypes.POWER_TYPE)
				.add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD),
			CopyResourceValueAction::action
		);
	}
}
