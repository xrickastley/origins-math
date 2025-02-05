package io.github.xrickastley.originsmath.powers;

import java.util.function.Consumer;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class HealingLinkedResourcePower extends TimedLinkedVariableIntPower<Float> {
	private final Consumer<Entity> entityAction;

	private HealingLinkedResourcePower(PowerType<?> type, LivingEntity entity, int duration, Consumer<Entity> entityAction) {
		super(type, entity, duration, x -> x);

		this.entityAction = entityAction;
	}

	/**
	 * Sets the amount of healing for this {@code HealingLinkedResourcePower}. 
	 * 
	 * Once the amount of healing is set, the associated {@code entityAction} with this power will
	 * be executed on the holder.
	 * 
	 * @param amount The amount of healing received.
	 */
	public void setHealingAmount(float amount) {
		this.setTemporaryValue(amount);

		if (this.entityAction != null) this.entityAction.accept(this.entity);
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("healing_linked_resource"),
			new SerializableData()
				.add("duration", SerializableDataTypes.INT, Integer.MAX_VALUE)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null),
			data -> (powerType, livingEntity) -> new HealingLinkedResourcePower(
				powerType,
				livingEntity,
				data.getInt("duration"),
				data.get("entity_action")
			)
		);
	}
}
