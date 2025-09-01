package io.github.xrickastley.originsmath.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.mojang.authlib.GameProfile;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.powers.DamageDealtLinkedResourcePower;
import io.github.xrickastley.originsmath.powers.DamageTakenLinkedResourcePower;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	public PlayerEntityMixin(final World world, final BlockPos pos, final float yaw, final GameProfile gameProfile) {
		super(EntityType.PLAYER, world);

		throw new AssertionError();
	}

	@Inject(
		method = "applyDamage",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lnet/minecraft/entity/player/PlayerEntity;modifyAppliedDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"
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

		OriginsMath.LOGGER.info("DMG Dealt Modifiers: {}", dmgDealtModifiers);

		double finalAmount = ModifierUtil.applyModifiers(source.getAttacker(), dmgDealtModifiers, amount.get());

		final List<Modifier> dmgTakenModifiers = dmgTakenLinked
			.stream()
			.<Modifier>mapMulti((power, consumer) -> power.getModifiers().forEach(consumer))
			.toList();

		OriginsMath.LOGGER.info("DMG Taken Modifiers: {}", dmgTakenModifiers);

		finalAmount = ModifierUtil.applyModifiers(this, dmgTakenModifiers, finalAmount);

		OriginsMath.LOGGER.info("Final amount: {}", finalAmount);

		amount.set((float) finalAmount);
	}
}
