package io.github.xrickastley.originsmath.mixins;

import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;

@Mixin(ConditionFactory.class)
public interface ConditionFactoryAccessor<T> {
	@Accessor(remap = false)
    public BiFunction<SerializableData.Instance, T, Boolean> getCondition();
}
