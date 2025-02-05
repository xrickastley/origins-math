package io.github.xrickastley.originsmath.powers;

import java.util.function.Consumer;
import java.util.function.Predicate;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;

public class DamageTakenLinkedResourcePower extends TimedLinkedVariableIntPower<Pair<DamageSource, Float>> {
	private final Predicate<Pair<DamageSource, Float>> damageCondition; 
	private final Consumer<Pair<Entity, Entity>> bientityAction;

	private DamageTakenLinkedResourcePower(PowerType<?> type, LivingEntity entity, int duration, Predicate<Pair<DamageSource, Float>> damageCondition, Consumer<Pair<Entity, Entity>> bientityAction) {
		super(type, entity, duration, pair -> pair.getRight());

		this.damageCondition = damageCondition;
		this.bientityAction = bientityAction;
	}

	/**
	 * Sets the damage data for this {@code DamageTakenLinkedResourcePower}. The damage data will 
	 * only be set if it fulfills the provided {@code damageCondition} for this {@code Power}.
	 * 
	 * Once the damage data is set, the associated {@code entityAction} with this power will be
	 * executed on the holder.
	 * 
	 * @param source The {@code DamageSource} of the damage dealt.
	 * @param amount The amount of damage dealt.
	 */
	public void setDamageData(DamageSource source, float amount) {
		final Pair<DamageSource,Float> damagePair = new Pair<>(source, amount);

		if (damageCondition != null && !damageCondition.test(damagePair)) return;

		this.setTemporaryValue(damagePair);
		
		if (bientityAction != null) this.bientityAction.accept(new Pair<>(this.entity, source.getAttacker()));
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("damage_taken_linked_resource"),
			new SerializableData()
				.add("duration", SerializableDataTypes.INT, Integer.MAX_VALUE)
				.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null),
			data -> (powerType, livingEntity) -> new DamageTakenLinkedResourcePower(
				powerType,
				livingEntity,
				data.getInt("duration"),
				data.get("damage_condition"),
				data.get("bientity_action")
			)
		);
	}
}
