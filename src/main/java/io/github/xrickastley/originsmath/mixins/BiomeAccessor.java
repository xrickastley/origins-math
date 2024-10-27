package io.github.xrickastley.originsmath.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.biome.Biome;

@Mixin(Biome.class)
public interface BiomeAccessor {
    @Accessor
    Biome.Weather getWeather();
}
