package io.github.xrickastley.originsmath.powers.apoli;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.ResourceBacked;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.function.Predicate;

public class EntityGlowPower extends Power {

	private final Predicate<Entity> entityCondition;
	private final Predicate<Pair<Entity, Entity>> bientityCondition;
	private final boolean useTeams;
	private final ResourceBacked<Float> red;
	private final ResourceBacked<Float> green;
	private final ResourceBacked<Float> blue;

	public EntityGlowPower(PowerType<?> type, LivingEntity entity, Predicate<Entity> entityCondition, Predicate<Pair<Entity, Entity>> bientityCondition, boolean useTeams, ResourceBacked<Float> red, ResourceBacked<Float> green, ResourceBacked<Float> blue) {
		super(type, entity);
		this.entityCondition = entityCondition;
		this.bientityCondition = bientityCondition;
		this.useTeams = useTeams;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public boolean doesApply(Entity e) {
		return (entityCondition == null || entityCondition.test(e)) && (bientityCondition == null || bientityCondition.test(new Pair<>(entity, e)));
	}

	public boolean usesTeams() {
		return useTeams;
	}

	public float getRed() {
		return red.floatValue();
	}

	public float getGreen() {
		return green.floatValue();
	}

	public float getBlue() {
		return blue.floatValue();
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(OriginsMath.identifier("entity_glow"),
			new SerializableData()
				.add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("use_teams", SerializableDataTypes.BOOLEAN, true)
				.add("red", ResourceBacked.DataTypes.RESOURCE_BACKED_FLOAT, ResourceBacked.fromNumber(1.0F))
				.add("green", ResourceBacked.DataTypes.RESOURCE_BACKED_FLOAT, ResourceBacked.fromNumber(1.0F))
				.add("blue", ResourceBacked.DataTypes.RESOURCE_BACKED_FLOAT, ResourceBacked.fromNumber(1.0F)),
			data -> (type, player) -> new EntityGlowPower(
				type, 
				player,
				data.get("entity_condition"),
				data.get("bientity_condition"),
				data.getBoolean("use_teams"),
				data.get("red"),
				data.get("green"),
				data.get("blue")
			)
		).allowCondition();
	}
}
