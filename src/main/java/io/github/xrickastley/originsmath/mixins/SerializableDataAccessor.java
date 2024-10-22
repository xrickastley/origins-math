package io.github.xrickastley.originsmath.mixins;

import java.util.LinkedHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.apace100.calio.data.SerializableData;

@Mixin(SerializableData.class)
public interface SerializableDataAccessor {
	@Accessor(remap = false)
	public LinkedHashMap<String, SerializableData.Field<?>> getDataFields();
}
