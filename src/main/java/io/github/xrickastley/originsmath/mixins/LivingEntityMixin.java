package io.github.xrickastley.originsmath.mixins;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.xrickastley.originsmath.powers.DamageDealtLinkedResourcePower;
import io.github.xrickastley.originsmath.powers.DamageTakenLinkedResourcePower;
import io.github.xrickastley.originsmath.powers.HealingLinkedResourcePower;

import com.llamalad7.mixinextras.sugar.Local;

@Debug(export = true)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(final EntityType<?> type, final World world) {
		super(type, world);

		throw new AssertionError();
	}

	@Inject(
		method = "applyDamage",
		at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getAbsorptionAmount()F",
			ordinal = 0
        )
	)
	private void updateDamageResourcePowers(DamageSource source, float amount, CallbackInfo ci, @Local(ordinal = 1) float finalDMG) {
		PowerHolderComponent
			.getPowers(source.getAttacker(), DamageDealtLinkedResourcePower.class)
			.forEach(power -> power.setDamageData(source, finalDMG, this));

		PowerHolderComponent
			.getPowers(this, DamageTakenLinkedResourcePower.class)
			.forEach(power -> power.setDamageData(source, finalDMG));
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
