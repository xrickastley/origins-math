package io.github.xrickastley.originsmath.powers;

import java.util.function.Function;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.InstanceValueSupplier;
import net.minecraft.entity.LivingEntity;

public class LivingEntityLinkedResourcePower extends SuppliedLinkedVariableIntPower<LivingEntity> {
	private LivingEntityLinkedResourcePower(PowerType<?> type, LivingEntity entity, LivingEntityProperty property) {
		super(type, entity, property, () -> entity);
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("living_entity_linked_resource"),
			new SerializableData()
				.add("property", LivingEntityLinkedResourcePower.LIVING_ENTITY_PROPERTY),
			data -> (powerType, livingEntity) -> new LivingEntityLinkedResourcePower(powerType, livingEntity, data.get("property"))
		);
	}

	private static final SerializableDataType<LivingEntityProperty> LIVING_ENTITY_PROPERTY = SerializableDataType.enumValue(LivingEntityProperty.class);

	private static enum LivingEntityProperty implements InstanceValueSupplier<LivingEntity> {
		HEALTH			(entity -> entity.getHealth()),
		RELATIVE_HEALTH	(entity -> entity.getHealth() / entity.getMaxHealth()),
		ABSORPTION		(entity -> entity.getAbsorptionAmount()),
		BREATHING		(entity -> entity.getAir()),
		FIRE_TICKS		(entity -> entity.getFireTicks()),
		FROZEN_TICKS	(entity -> entity.getFrozenTicks()),
		FREEZING_SCALE	(entity -> entity.getFreezingScale()),
		STUCK_ARROWS	(entity -> entity.getStuckArrowCount()),
		FALL_DISTANCE	(entity -> entity.fallDistance),
		TIME_OF_DAY		(entity -> entity.getWorld().getTimeOfDay() % 24000L),
		AGE				(entity -> entity.age),
		X				(entity -> entity.getX()),
		Y				(entity -> entity.getY()),
		Z				(entity -> entity.getZ()),
		VELOCITY_X		(entity -> entity.getVelocity().getX()),
		VELOCITY_Y		(entity -> entity.getVelocity().getY()),
		VELOCITY_Z		(entity -> entity.getVelocity().getZ()),
		PITCH			(entity -> entity.getPitch()),
		YAW				(entity -> entity.getYaw()),
		ROLL			(entity -> entity.getRoll());

		private final Function<LivingEntity, Number> supplier;

		LivingEntityProperty(Function<LivingEntity, Number> supplier) {
			this.supplier = supplier;
		}

		public Number supplyAsNumber(LivingEntity player) {
			return supplier.apply(player);
		}
	}
}
