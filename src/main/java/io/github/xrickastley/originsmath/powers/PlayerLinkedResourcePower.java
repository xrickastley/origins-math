package io.github.xrickastley.originsmath.powers;

import java.util.function.Function;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.InstanceValueSupplier;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerLinkedResourcePower extends SuppliedLinkedVariableIntPower<PlayerEntity> {
	private PlayerLinkedResourcePower(PowerType<?> type, LivingEntity entity, PlayerProperty property) {
		super(type, entity, property, () -> entity instanceof final PlayerEntity player ? player : null);
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("player_linked_resource"),
			new SerializableData()
				.add("property", PlayerLinkedResourcePower.PLAYER_PROPERTY),
			data -> (powerType, livingEntity) -> new PlayerLinkedResourcePower(powerType, livingEntity, data.get("property"))
		);
	}

	private static final SerializableDataType<PlayerProperty> PLAYER_PROPERTY = SerializableDataType.enumValue(PlayerProperty.class);

	private static enum PlayerProperty implements InstanceValueSupplier<PlayerEntity> {
		FOOD_LEVEL      (player -> player.getHungerManager().getFoodLevel()),
		SATURATION      (player -> player.getHungerManager().getSaturationLevel()),
		HEALTH          (player -> player.getHealth()),
		RELATIVE_HEALTH (player -> player.getHealth() / player.getMaxHealth()),
		ABSORPTION      (player -> player.getAbsorptionAmount()),
		BREATHING       (player -> player.getAir()),
		FIRE_TICKS      (player -> player.getFireTicks()),
		FROZEN_TICKS    (player -> player.getFrozenTicks()),
		FREEZING_SCALE  (player -> player.getFreezingScale()),
		EXP_SCORE       (player -> player.getScore()),
		SLEEP_TIMER     (player -> player.getSleepTimer()),
		STUCK_ARROWS    (player -> player.getStuckArrowCount()),
		X               (player -> player.getX()),
		Y               (player -> player.getY()),
		Z               (player -> player.getZ()),
		VELOCITY_X      (player -> player.getVelocity().getX()),
		VELOCITY_Y      (player -> player.getVelocity().getY()),
		VELOCITY_Z      (player -> player.getVelocity().getZ()),
		PITCH           (player -> player.getPitch()),
		YAW             (player -> player.getYaw()),
		ROLL            (player -> player.getRoll());

		private final Function<PlayerEntity, Number> supplier;

		PlayerProperty(Function<PlayerEntity, Number> supplier) {
			this.supplier = supplier;
		}

		public int supplyValue(PlayerEntity player) {
			return supplier
				.apply(player)
				.intValue();
		}

		public Number supplyAsNumber(PlayerEntity player) {
			return supplier.apply(player);
		}
	}
}