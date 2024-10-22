package io.github.xrickastley.originsmath.mixins;

import java.util.function.BiConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;

@Mixin(ActionFactory.class)
public interface ActionFactoryAccessor<T> {
	@Accessor(remap = false)
    public BiConsumer<SerializableData.Instance, T> getEffect();
}
