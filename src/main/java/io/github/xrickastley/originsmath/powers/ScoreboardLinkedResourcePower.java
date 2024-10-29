package io.github.xrickastley.originsmath.powers;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

public class ScoreboardLinkedResourcePower extends LinkedVariableIntPower {
	private final String objective;

	private ScoreboardLinkedResourcePower(PowerType<?> type, LivingEntity entity, String objective) {
		super(type, entity);
		
		this.objective = objective;
	}

	@Override
	protected int supplyValue() {
		final Scoreboard scoreboard = entity.getWorld().getScoreboard();
		final ScoreboardObjective scObjective = scoreboard.getNullableObjective(objective);
		final String name = ScoreboardLinkedResourcePower.getUuidOrNameString(entity);

		return scObjective == null || !scoreboard.playerHasObjective(name, scObjective)
			? 0
			: scoreboard.getPlayerScore(name, scObjective).getScore();
	}

	@Override
	public double supplyDoubleValue() {
		return supplyValue();
	}

	private static String getUuidOrNameString(Entity entity) {
		return entity instanceof PlayerEntity playerEntity ? playerEntity.getEntityName() : entity.getUuidAsString();
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("scoreboard_linked_resource"),
			new SerializableData()
				.add("objective", SerializableDataTypes.STRING),
			data -> (powerType, livingEntity) -> new ScoreboardLinkedResourcePower(powerType, livingEntity, data.getString("objective"))
		);
	}
}
