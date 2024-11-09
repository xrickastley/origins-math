package io.github.xrickastley.originsmath.powers.apoli;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.ResourceBacked;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ExhaustOverTimePower extends Power {

	private final ResourceBacked<Integer> exhaustInterval;
	private final ResourceBacked<Float> exhaustion;

	public ExhaustOverTimePower(PowerType<?> type, LivingEntity entity, ResourceBacked<Integer> exhaustInterval, ResourceBacked<Float> exhaustion) {
		super(type, entity);
		this.exhaustInterval = exhaustInterval;
		this.exhaustion = exhaustion;
		this.setTicking();
	}

	public void tick() {
		if(entity instanceof PlayerEntity playerEntity && entity.age % exhaustInterval.intValue() == 0) {
			playerEntity.addExhaustion(exhaustion.floatValue());
		}
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(OriginsMath.identifier("exhaust"),
			new SerializableData()
				.add("interval", ResourceBacked.DataTypes.RESOURCE_BACKED_INT, ResourceBacked.fromNumber(20))
				.add("exhaustion", ResourceBacked.DataTypes.RESOURCE_BACKED_FLOAT),
			data -> (powerType, livingEntity) -> new ExhaustOverTimePower(
				powerType,
				livingEntity,
				data.get("interval"),
				data.get("exhaustion")
			)
		).allowCondition();
	}
}
