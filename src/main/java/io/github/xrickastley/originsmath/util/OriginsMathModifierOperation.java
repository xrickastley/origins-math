package io.github.xrickastley.originsmath.util;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.modifier.IModifierOperation;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierOperation;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;

import net.minecraft.data.client.BlockStateVariantMap.TriFunction;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;

public enum OriginsMathModifierOperation implements IModifierOperation {
	STANDARD_MULTIPLY_BASE(
		Phase.BASE, 
		233, 
		(values, base, current) -> current * values.stream().reduce(0.0, Double::sum)
	),
	STANDARD_MULTIPLY_TOTAL(
		Phase.TOTAL,
		233,
		(values, base, current) -> current * values.stream().reduce(0.0, Double::sum)
	),
	STANDARD_DIVIDE_BASE(
		Phase.BASE,
		266,
		(values, base, current) -> current / values.stream().reduce(0.0, Double::sum)
	),
	STANDARD_DIVIDE_TOTAL(
		Phase.TOTAL,
		266,
		(values, base, current) -> current / values.stream().reduce(0.0, Double::sum)
	);

	private final Phase phase;
	private final int order;
	private final TriFunction<List<Double>, Double, Double, Double> function;

	OriginsMathModifierOperation(Phase phase, int order, TriFunction<List<Double>, Double, Double, Double> function) {
		this.phase = phase;
		this.order = order;
		this.function = function;
	}

	@Override
	public Phase getPhase() {
		return phase;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public SerializableData getData() {
		return ModifierOperation.DATA;
	}

	@Override
	public double apply(Entity entity, List<SerializableData.Instance> instances, double base, double current) {
		return function.apply(
			instances
				.stream()
				.map(instance -> {
					double value = 0;
					if (instance.isPresent("resource")) {
						PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
						PowerType<?> powerType = instance.get("resource");

						if (!component.hasPower(powerType)) {
							value = instance.get("value");
						} else {
							Power p = component.getPower(powerType);
							
							// Not using LinkedVariableIntPower to keep parity with the standard Modifier Operations.
							if (p instanceof VariableIntPower vip) value = vip.getValue();
							else if (p instanceof CooldownPower cp) value = cp.getRemainingTicks();
						}
					} else {
						value = instance.get("value");
					}

					if (instance.isPresent("modifier")) {
						List<Modifier> modifiers = instance.get("modifier");
						value = ModifierUtil.applyModifiers(entity, modifiers, value);
					}

					return value;
			})
			.collect(Collectors.toList()),
		base, current);
	}

	public static void register() {
		for (OriginsMathModifierOperation operation : OriginsMathModifierOperation.values()) {
			Registry.register(
				ApoliRegistries.MODIFIER_OPERATION,
				OriginsMath.identifier(operation.toString().toLowerCase(Locale.ROOT)),
				operation
			);
		}	
	}
}
