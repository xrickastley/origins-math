package io.github.xrickastley.originsmath.actions.entity;

import org.mariuszgromada.math.mxparser.Expression;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.powers.MathResourcePower;
import io.github.xrickastley.originsmath.util.VariableSerializer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class VariableChangeResourceAction {
	private static void action(SerializableData.Instance data, Entity entity) {
		if (!(entity instanceof LivingEntity)) return;

		final PowerType<?> powerType = data.get("resource");
		final ResourceOperation operation = data.get("operation");
		final Expression expression = data.get("expression");
		final VariableSerializer variables = data.get("variables");

		PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
		Power power = component.getPower(powerType);
		int change = ((int) new Expression(expression.getExpressionString(), variables.getArgumentArray(entity, false)).calculate());
		
		if (power instanceof VariableIntPower vip) {
			if (operation == ResourceOperation.ADD) {
				int newValue = vip.getValue() + change;
				vip.setValue(newValue);
			} else if (operation == ResourceOperation.SET) {
				vip.setValue(change);
			}
		} else if (power instanceof CooldownPower cp) {
			if (operation == ResourceOperation.ADD) {
				cp.modify(change);
			} else if (operation == ResourceOperation.SET) {
				cp.setCooldown(change);
			}
		}

		PowerHolderComponent.syncPower(entity, powerType);
	}

	public static ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsMath.identifier("variable_change_resource"),
			new SerializableData()
				.add("resource", ApoliDataTypes.POWER_TYPE)
				.add("expression", MathResourcePower.EXPRESSION)
				.add("variables", VariableSerializer.SERIALIZABLE_DATATYPE, VariableSerializer.EMPTY)
				.add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD),
			VariableChangeResourceAction::action
		);
	}
}
