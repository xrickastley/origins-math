package io.github.xrickastley.originsmath.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;

import java.util.List;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.xrickastley.originsmath.powers.DamageDealtLinkedResourcePower;
import io.github.xrickastley.originsmath.powers.DamageTakenLinkedResourcePower;
import io.github.xrickastley.originsmath.powers.HealingLinkedResourcePower;
import io.github.xrickastley.originsmath.powers.ModifyKnockbackPower;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
@Debug(export = true)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(final EntityType<?> type, final World world) {
		super(type, world);

		throw new AssertionError();
	}

	@Inject(
		method = "applyDamage",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/LivingEntity;modifyAppliedDamage(Lnet/minecraft/entity/damage/DamageSource;F)F",
			shift = At.Shift.AFTER
		)
	)
	private void updateDamageResourcePowers(DamageSource source, float _amount, CallbackInfo ci, @Local(argsOnly = true) LocalFloatRef amount) {
		final List<DamageDealtLinkedResourcePower> dmgDealtLinked = PowerHolderComponent.getPowers(source.getAttacker(), DamageDealtLinkedResourcePower.class);
		final List<DamageTakenLinkedResourcePower> dmgTakenLinked = PowerHolderComponent.getPowers(this, DamageTakenLinkedResourcePower.class);

		dmgDealtLinked.forEach(power -> power.setDamageData(source, amount.get(), this));
		dmgTakenLinked.forEach(power -> power.setDamageData(source, amount.get()));

		final List<Modifier> dmgDealtModifiers = dmgDealtLinked
			.stream()
			.<Modifier>mapMulti((power, consumer) -> power.getModifiers().forEach(consumer))
			.toList();

		double finalAmount = ModifierUtil.applyModifiers(source.getAttacker(), dmgDealtModifiers, amount.get());

		final List<Modifier> dmgTakenModifiers = dmgTakenLinked
			.stream()
			.<Modifier>mapMulti((power, consumer) -> power.getModifiers().forEach(consumer))
			.toList();

		finalAmount = ModifierUtil.applyModifiers(this, dmgTakenModifiers, finalAmount);

		amount.set((float) finalAmount);
	}

	@ModifyArgs(
		method = "takeKnockback",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V"
		)
	)
	private void applyKnockbackModifiers(Args args) {
		final List<ModifyKnockbackPower> powers = PowerHolderComponent.getPowers(this, ModifyKnockbackPower.class);

		args.set(0, ModifyKnockbackPower.applyModifiers(Axis.X, powers, this, args.get(0)));
		args.set(1, ModifyKnockbackPower.applyModifiers(Axis.Y, powers, this, args.get(1)));
		args.set(2, ModifyKnockbackPower.applyModifiers(Axis.Z, powers, this, args.get(2)));
	}

	@Inject(
		method = "heal",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V",
			shift = At.Shift.BEFORE
		)
	)
	private void updateHealResourcePowers(float amount, CallbackInfo ci) {
		PowerHolderComponent
			.getPowers(this, HealingLinkedResourcePower.class)
			.forEach(power -> power.setHealingAmount(amount));
	}
}
