package io.github.xrickastley.originsmath.mixins;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;

@Mixin(PowerFactory.class)
public interface PowerFactoryAccessor<P extends Power> {
	@Accessor(remap = false)
    public Function<SerializableData.Instance, BiFunction<PowerType<P>, LivingEntity, P>> getFactoryConstructor();
}
