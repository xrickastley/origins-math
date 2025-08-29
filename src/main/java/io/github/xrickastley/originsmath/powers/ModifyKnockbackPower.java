package io.github.xrickastley.originsmath.powers;

import java.util.Collections;
import java.util.List;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction.Axis;

public class ModifyKnockbackPower extends Power {
	private final List<Modifier> xModifiers;
	private final List<Modifier> yModifiers;
	private final List<Modifier> zModifiers;

	private ModifyKnockbackPower(PowerType<?> type, LivingEntity entity, List<Modifier> xModifiers, List<Modifier> yModifiers, List<Modifier> zModifiers) {
		super(type, entity);

		this.xModifiers = xModifiers;
		this.yModifiers = yModifiers;
		this.zModifiers = zModifiers;
	}

	public List<Modifier> getXModifiers() {
		return Collections.unmodifiableList(this.xModifiers);
	}

	public List<Modifier> getYModifiers() {
		return Collections.unmodifiableList(this.yModifiers);
	}

	public List<Modifier> getZModifiers() {
		return Collections.unmodifiableList(this.zModifiers);
	}

	private List<Modifier> getModifiers(Axis axis) {
		switch (axis) {
			case X:
				return this.getXModifiers();
			case Y:
				return this.getYModifiers();
			case Z:
				return this.getZModifiers();
			default:
				throw new EnumConstantNotPresentException(Axis.class, axis.getName());
		}
	}

	public static double applyModifiers(Axis axis, Entity entity, double original) {
		return ModifyKnockbackPower.applyModifiers(axis, PowerHolderComponent.getPowers(entity, ModifyKnockbackPower.class), entity, original);
	}

	public static double applyModifiers(Axis axis, List<ModifyKnockbackPower> powers, Entity entity, double original) {
		final List<Modifier> modifiers = powers
			.stream()
			.<Modifier>mapMulti((power, consumer) -> power.getModifiers(axis).forEach(consumer))
			.toList();

		return ModifierUtil.applyModifiers(entity, modifiers, original);
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("modify_knockback"),
			new SerializableData()
                .add("x", Modifier.LIST_TYPE, Collections.emptyList())
                .add("y", Modifier.LIST_TYPE, Collections.emptyList())
                .add("z", Modifier.LIST_TYPE, Collections.emptyList()),
			data -> (powerType, livingEntity) -> new ModifyKnockbackPower(
				powerType,
				livingEntity,
				data.get("x"),
				data.get("y"),
				data.get("z")
			)
		).allowCondition();
	}
}
