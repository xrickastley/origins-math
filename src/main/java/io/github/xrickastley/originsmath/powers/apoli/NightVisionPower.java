package io.github.xrickastley.originsmath.powers.apoli;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.ResourceBacked;
import net.minecraft.entity.LivingEntity;

import java.util.function.Function;

public class NightVisionPower extends Power {
	private final Function<LivingEntity, Float> strengthFunction;

	public NightVisionPower(PowerType<?> type, LivingEntity entity) {
		this(type, entity, ResourceBacked.fromNumber(1.0F));
	}

	public NightVisionPower(PowerType<?> type, LivingEntity entity, ResourceBacked<Float> strength) {
		this(type, entity, pe -> strength.floatValue());
	}

	public NightVisionPower(PowerType<?> type, LivingEntity entity, Function<LivingEntity, Float> strengthFunction) {
		super(type, entity);

		this.strengthFunction = strengthFunction;
	}

	public float getStrength() {
		return strengthFunction.apply(this.entity);
	}

	@SuppressWarnings("unchecked")
	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(OriginsMath.identifier("night_vision"),
			new SerializableData()
				.add("strength", ResourceBacked.DataTypes.RESOURCE_BACKED_FLOAT, ResourceBacked.fromNumber(1.0F)),
			data -> (type, player) -> new NightVisionPower(type, player, (ResourceBacked<Float>) data.get("strength"))
		).allowCondition();
	}
}
