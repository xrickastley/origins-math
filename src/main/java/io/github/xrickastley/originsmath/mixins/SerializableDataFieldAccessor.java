package io.github.xrickastley.originsmath.mixins;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.apace100.calio.data.SerializableData;

@Mixin(SerializableData.Field.class)
public interface SerializableDataFieldAccessor<T> {
	@Accessor(remap = false)
	T getDefaultValue();
	
	@Accessor(remap = false)
	Function<SerializableData.Instance, T> getDefaultFunction();
}
