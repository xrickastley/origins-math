package io.github.xrickastley.originsmath.mixins;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;

import io.github.apace100.apoli.component.PowerHolderComponent;
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
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getAbsorptionAmount()F",
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
}
