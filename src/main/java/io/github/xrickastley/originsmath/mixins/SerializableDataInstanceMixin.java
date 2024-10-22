package io.github.xrickastley.originsmath.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.interfaces.SDIEntityInjection;
import io.github.xrickastley.originsmath.util.ResourceBacked;
import net.minecraft.entity.Entity;

@Mixin(SerializableData.Instance.class)
public abstract class SerializableDataInstanceMixin implements SDIEntityInjection {
	private Entity targetEntity = null;

	@Shadow(remap = false)
	public abstract <T> T get(String name);

	@Unique
	@Override
	public void setEntity(Entity entity) {
		this.targetEntity = entity;
	}

	@Inject(
		method = "getInt",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	public void injectResourceLinkToGetInt(String name, CallbackInfoReturnable<Integer> cir) {
		if (!(this.get(name) instanceof ResourceBacked rbi)) return;
		
		rbi.setTargetEntity(targetEntity);
		cir.setReturnValue(rbi.intValue());
	}

	@Inject(
		method = "getFloat",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	public void injectResourceLinkToGetFloat(String name, CallbackInfoReturnable<Float> cir) {
		if (!(this.get(name) instanceof ResourceBacked rbi)) return;
		
		rbi.setTargetEntity(targetEntity);
		cir.setReturnValue(rbi.floatValue());
	}

	@Inject(
		method = "getDouble",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	public void injectResourceLinkToGetDouble(String name, CallbackInfoReturnable<Double> cir) {
		if (!(this.get(name) instanceof ResourceBacked rbi)) return;
		
		rbi.setTargetEntity(targetEntity);
		cir.setReturnValue(rbi.doubleValue());
	}
}
