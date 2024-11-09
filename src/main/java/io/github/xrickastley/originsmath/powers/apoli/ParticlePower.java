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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

public class ParticlePower extends Power {

    private final Predicate<Pair<Entity, Entity>> biEntityCondition;
    private final ParticleEffect particleEffect;

    private final Vec3d spread;

    private final ResourceBacked<Integer> frequency;
    private final ResourceBacked<Integer> count;

    private final ResourceBacked<Double> offsetX;
    private final ResourceBacked<Double> offsetY;
    private final ResourceBacked<Double> offsetZ;

    private final ResourceBacked<Float> speed;

    private final boolean visibleInFirstPerson;
    private final boolean visibleWhileInvisible;
    private final boolean force;

    public ParticlePower(PowerType<?> powerType, LivingEntity livingEntity, ParticleEffect particleEffect, Predicate<Pair<Entity, Entity>> biEntityCondition, ResourceBacked<Integer> count, ResourceBacked<Float> speed, boolean force, Vec3d spread, ResourceBacked<Double> offsetX, ResourceBacked<Double> offsetY, ResourceBacked<Double> offsetZ, ResourceBacked<Integer> frequency, boolean visibleInFirstPerson, boolean visibleWhileInvisible) {
        super(powerType, livingEntity);
        this.particleEffect = particleEffect;
        this.biEntityCondition = biEntityCondition;
        this.count = count;
        this.speed = speed;
        this.force = force;
        this.spread = spread;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.frequency = frequency;
        this.visibleInFirstPerson = visibleInFirstPerson;
        this.visibleWhileInvisible = visibleWhileInvisible;
    }

    public boolean doesApply(PlayerEntity viewer, boolean inFirstPerson) {
        return (!entity.isInvisibleTo(viewer) || this.isVisibleWhileInvisible())
            && (entity != viewer || (!inFirstPerson || this.isVisibleInFirstPerson()))
            && (viewer.getBlockPos().isWithinDistance(entity.getPos(), this.shouldForce() ? 512 : 32))
            && (entity.age % this.getFrequency() == 0)
            && (biEntityCondition == null || biEntityCondition.test(new Pair<>(entity, viewer)));
    }

    public ParticleEffect getParticle() {
        return particleEffect;
    }

    public Vec3d getSpread() {
        return spread;
    }

    public int getFrequency() {
        return frequency.intValue();
    }

    public double getOffsetX() {
        return offsetX.doubleValue();
    }

    public double getOffsetY() {
        return offsetY.doubleValue();
    }

    public double getOffsetZ() {
        return offsetZ.doubleValue();
    }

    public int getCount() {
        return count.intValue();
    }

    public float getSpeed() {
        return speed.floatValue();
    }

    public boolean shouldForce() {
        return force;
    }

    public boolean isVisibleInFirstPerson() {
        return visibleInFirstPerson;
    }

    public boolean isVisibleWhileInvisible() {
        return visibleWhileInvisible;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
            OriginsMath.identifier("particle"),
            new SerializableData()
                .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE)
                .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                .add("count", ResourceBacked.DataTypes.RESOURCE_BACKED_INT, ResourceBacked.fromNumber(1))
                .add("speed", ResourceBacked.DataTypes.RESOURCE_BACKED_FLOAT, ResourceBacked.fromNumber(0.0F))
                .add("force", SerializableDataTypes.BOOLEAN, false)
                .add("spread", SerializableDataTypes.VECTOR, new Vec3d(0.5, 0.5, 0.5))
                .add("offset_x", ResourceBacked.DataTypes.RESOURCE_BACKED_DOUBLE, ResourceBacked.fromNumber(0.0D))
                .add("offset_y", ResourceBacked.DataTypes.RESOURCE_BACKED_DOUBLE, ResourceBacked.fromNumber(0.5D))
                .add("offset_z", ResourceBacked.DataTypes.RESOURCE_BACKED_DOUBLE, ResourceBacked.fromNumber(0.0D))
                .add("frequency", ResourceBacked.DataTypes.RESOURCE_BACKED_INT)
                .add("visible_in_first_person", SerializableDataTypes.BOOLEAN, false)
                .add("visible_while_invisible", SerializableDataTypes.BOOLEAN, false),
            data -> (powerType, livingEntity) -> new ParticlePower(
                powerType,
                livingEntity,
                data.get("particle"),
                data.get("bientity_condition"),
                data.get("count"),
                data.get("speed"),
                data.get("force"),
                data.get("spread"),
                data.get("offset_x"),
                data.get("offset_y"),
                data.get("offset_z"),
                data.get("frequency"),
                data.get("visible_in_first_person"),
                data.get("visible_while_invisible")
            )
        ).allowCondition();
    }

}