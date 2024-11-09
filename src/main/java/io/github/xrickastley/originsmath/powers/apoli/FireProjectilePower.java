package io.github.xrickastley.originsmath.powers.apoli;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.ActiveCooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.ResourceBacked;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.function.Consumer;

public class FireProjectilePower extends ActiveCooldownPower {
	private final EntityType<?> entityType;
	private final ResourceBacked<Integer> projectileCount;
	private final ResourceBacked<Integer> interval;
	private final ResourceBacked<Integer> startDelay;
	private final ResourceBacked<Float> speed;
	private final ResourceBacked<Float> divergence;
	private final SoundEvent soundEvent;
	private final NbtCompound tag;
	private final Consumer<Entity> projectileAction;
	private final Consumer<Entity> shooterAction;

	private boolean isFiringProjectiles;
	private boolean finishedStartDelay;
	private int shotProjectiles;

	public FireProjectilePower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, EntityType<?> entityType, ResourceBacked<Integer> projectileCount, ResourceBacked<Integer> interval, ResourceBacked<Integer> startDelay, ResourceBacked<Float> speed, ResourceBacked<Float> divergence, SoundEvent soundEvent, NbtCompound tag, Consumer<Entity> projectileAction, Consumer<Entity> shooterAction) {
		super(type, entity, cooldownDuration, hudRender, null);
		this.entityType = entityType;
		this.projectileCount = projectileCount;
		this.interval = interval;
		this.startDelay = startDelay;
		this.speed = speed;
		this.divergence = divergence;
		this.soundEvent = soundEvent;
		this.tag = tag;
		this.projectileAction = projectileAction;
		this.shooterAction = shooterAction;
		this.setTicking(true);
	}

	@Override
	public void onUse() {
		if(canUse()) {
			isFiringProjectiles = true;
			use();
		}
	}

	@Override
	public NbtElement toTag() {
		NbtCompound nbt = new NbtCompound();
		nbt.putLong("LastUseTime", lastUseTime);
		nbt.putInt("ShotProjectiles", shotProjectiles);
		nbt.putBoolean("FinishedStartDelay", finishedStartDelay);
		nbt.putBoolean("IsFiringProjectiles", isFiringProjectiles);
		return nbt;
	}

	@Override
	public void fromTag(NbtElement tag) {
		if(tag instanceof NbtLong) {
			lastUseTime = ((NbtLong)tag).longValue();
		}
		else {
			lastUseTime = ((NbtCompound)tag).getLong("LastUseTime");
			shotProjectiles = ((NbtCompound)tag).getInt("ShotProjectiles");
			finishedStartDelay = ((NbtCompound)tag).getBoolean("FinishedStartDelay");
			isFiringProjectiles = ((NbtCompound)tag).getBoolean("IsFiringProjectiles");
		}
	}

	@SuppressWarnings("resource")
	public void tick() {
		if (!isFiringProjectiles) return;
		
		if (!finishedStartDelay && startDelay.intValue() == 0) finishedStartDelay = true;
		
		if (!finishedStartDelay && (entity.getEntityWorld().getTime() - lastUseTime) % startDelay.intValue() == 0) {
			finishedStartDelay = true;
			shotProjectiles += 1;

			if (shotProjectiles <= projectileCount.intValue()) {
				if (soundEvent != null) {
					entity
						.getWorld()
						.playSound(
							null, 
							entity.getX(), 
							entity.getY(), 
							entity.getZ(), 
							soundEvent, 
							SoundCategory.NEUTRAL, 
							0.5F, 
							0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F)
						);
				}

				if (!entity.getWorld().isClient) fireProjectile();
			} else {
				shotProjectiles = 0;
				finishedStartDelay = false;
				isFiringProjectiles = false;
			}
		} else if (interval.intValue() == 0 && finishedStartDelay) {
			if (soundEvent != null) {
				entity
					.getWorld()
					.playSound(
						null, 
						entity.getX(), 
						entity.getY(), 
						entity.getZ(), 
						soundEvent, 
						SoundCategory.NEUTRAL, 
						0.5F, 
						0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F)
					);
			}

			if (!entity.getWorld().isClient) {
				for(; shotProjectiles < projectileCount.intValue(); shotProjectiles++) fireProjectile();
			}

			shotProjectiles = 0;
			finishedStartDelay = false;
			isFiringProjectiles = false;
		} else if (finishedStartDelay && (entity.getEntityWorld().getTime() - lastUseTime) % interval.intValue() == 0) {
			shotProjectiles += 1;

			if (shotProjectiles <= projectileCount.intValue()) {
				if (soundEvent != null) {
					entity
						.getWorld()
						.playSound(
							null, 
							entity.getX(), 
							entity.getY(), 
							entity.getZ(), 
							soundEvent, 
							SoundCategory.NEUTRAL, 
							0.5F, 
							0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F)
						);
				}

				if (!entity.getWorld().isClient) fireProjectile();
			} else {
				shotProjectiles = 0;
				finishedStartDelay = false;
				isFiringProjectiles = false;
			}
		}
		
	}

	@SuppressWarnings("resource")
	private void fireProjectile() {
		if (entityType == null || entity.getWorld().isClient) return;

		ServerWorld serverWorld = (ServerWorld) entity.getWorld();
		float yaw = entity.getYaw();
		float pitch = entity.getPitch();

		Entity entityToSpawn = MiscUtil
			.getEntityWithPassengers(
				serverWorld, 
				entityType, 
				tag, 
				entity
					.getPos()
					.add(0, entity.getEyeHeight(entity.getPose()), 0), 
				yaw, 
				pitch
			)
			.orElse(null);

		if (entityToSpawn == null) return;

		Vec3d rotationVector = entity.getRotationVector();
		Vec3d velocity = entity.getVelocity();
		Random random = serverWorld.getRandom();

		if (entityToSpawn instanceof ProjectileEntity projectileToSpawn) {
			if (projectileToSpawn instanceof ExplosiveProjectileEntity explosiveProjectileToSpawn) {
				explosiveProjectileToSpawn.powerX = rotationVector.x * speed.floatValue();
				explosiveProjectileToSpawn.powerY = rotationVector.y * speed.floatValue();
				explosiveProjectileToSpawn.powerZ = rotationVector.z * speed.floatValue();
			}

			projectileToSpawn.setOwner(entity);
			projectileToSpawn.setVelocity(entity, pitch, yaw, 0F, speed.floatValue(), divergence.floatValue());
		} else {
			float f = 0.017453292F;
			double g = 0.007499999832361937D;

			float h = -MathHelper.sin(yaw * f) * MathHelper.cos(pitch * f);
			float i = -MathHelper.sin(pitch * f);
			float j =  MathHelper.cos(yaw * f) * MathHelper.cos(pitch * f);

			Vec3d vec3d = new Vec3d(h, i, j)
				.normalize()
				.add(
					random.nextGaussian() * g * divergence.floatValue(), 
					random.nextGaussian() * g * divergence.floatValue(), 
					random.nextGaussian() * g * divergence.floatValue()
				)
				.multiply(speed.floatValue());

			entityToSpawn.setVelocity(vec3d);
			entityToSpawn.addVelocity(velocity.x, entity.isOnGround() ? 0.0D : velocity.y, velocity.z);
		}

		if (tag.isEmpty()) {
			NbtCompound mergedTag = entityToSpawn.writeNbt(new NbtCompound());
			mergedTag.copyFrom(tag);

			entityToSpawn.readNbt(mergedTag);
		}

		serverWorld.spawnNewEntityAndPassengers(entityToSpawn);

		if (projectileAction != null) projectileAction.accept(entityToSpawn);

		if (shooterAction != null) shooterAction.accept(entity);
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("fire_projectile"),
			new SerializableData()
				.add("cooldown", SerializableDataTypes.INT, 1)
				.add("count", ResourceBacked.DataTypes.RESOURCE_BACKED_INT, ResourceBacked.fromNumber(1))
				.add("interval", ResourceBacked.DataTypes.RESOURCE_BACKED_INT, ResourceBacked.fromNumber(0))
				.add("start_delay", ResourceBacked.DataTypes.RESOURCE_BACKED_INT, ResourceBacked.fromNumber(0))
				.add("speed", ResourceBacked.DataTypes.RESOURCE_BACKED_FLOAT, ResourceBacked.fromNumber(1.5F))
				.add("divergence", ResourceBacked.DataTypes.RESOURCE_BACKED_FLOAT, ResourceBacked.fromNumber(1F))
				.add("sound", SerializableDataTypes.SOUND_EVENT, null)
				.add("entity_type", SerializableDataTypes.ENTITY_TYPE)
				.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
				.add("tag", SerializableDataTypes.NBT, new NbtCompound())
				.add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key())
				.add("projectile_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("shooter_action", ApoliDataTypes.ENTITY_ACTION, null),
			data -> (powerType, livingEntity) -> {
				FireProjectilePower fpp = new FireProjectilePower(
					powerType,
					livingEntity,
					data.get("cooldown"),
					data.get("hud_render"),
					data.get("entity_type"),
					data.get("count"),
					data.get("interval"),
					data.get("start_delay"),
					data.get("speed"),
					data.get("divergence"),
					data.get("sound"),
					data.get("tag"),
					data.get("projectile_action"),
					data.get("shooter_action")
				);

				fpp.setKey(data.get("key"));

				return fpp;
			}
		).allowCondition();
	}
}
