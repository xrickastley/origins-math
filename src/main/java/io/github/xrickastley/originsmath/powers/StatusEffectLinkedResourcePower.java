package io.github.xrickastley.originsmath.powers;

import java.util.function.Function;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.InstanceValueSupplier;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

/**
 * A resource who's value is based on the integer counterpart of an {@code EntityAttribute}.
 */
public class StatusEffectLinkedResourcePower extends SuppliedLinkedVariableIntPower<StatusEffectInstance> {
	private StatusEffectLinkedResourcePower(PowerType<?> type, LivingEntity entity, StatusEffect effect, StatusEffectProperty property) {
		super(type, entity, property, () -> entity.getStatusEffect(effect));
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("status_effect_linked_resource"),
			new SerializableData()
				.add("effect", SerializableDataTypes.STATUS_EFFECT)
				.add("property", StatusEffectLinkedResourcePower.STATUS_EFFECT_PROPERTY),
			data -> (powerType, livingEntity) -> new StatusEffectLinkedResourcePower(
				powerType, 
				livingEntity, 
				data.get("effect"), 
				data.get("property")
			)
		);
	}

	private static final SerializableDataType<StatusEffectProperty> STATUS_EFFECT_PROPERTY = SerializableDataType.enumValue(StatusEffectProperty.class);

	private static enum StatusEffectProperty implements InstanceValueSupplier<StatusEffectInstance> {
		AMPLIFIER (instance -> instance != null ? instance.getAmplifier() : -1),
		DURATION  (instance -> instance != null ? instance.getDuration() : 0);

		private final Function<StatusEffectInstance, Number> supplier;

		StatusEffectProperty(Function<StatusEffectInstance, Number> supplier) {
			this.supplier = supplier;
		}

		public int supplyValue(StatusEffectInstance player) {
			return supplier
				.apply(player)
				.intValue();
		}

		public Number supplyAsNumber(StatusEffectInstance player) {
			return supplier.apply(player);
		}
	} 
}
