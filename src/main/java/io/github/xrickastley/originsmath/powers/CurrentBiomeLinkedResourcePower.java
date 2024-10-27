package io.github.xrickastley.originsmath.powers;

import java.util.function.Function;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.mixins.BiomeAccessor;
import io.github.xrickastley.originsmath.util.InstanceValueSupplier;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.biome.Biome;

public class CurrentBiomeLinkedResourcePower extends SuppliedLinkedVariableIntPower<Biome> {
	private CurrentBiomeLinkedResourcePower(PowerType<?> type, LivingEntity entity, BiomeProperty property) {
		super(type, entity, property, () -> entity.getWorld().getBiome(entity.getBlockPos()).value());
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("current_biome_linked_resource"),
			new SerializableData()
				.add("property", CurrentBiomeLinkedResourcePower.BIOME_PROPERTY),
			data -> (powerType, livingEntity) -> new CurrentBiomeLinkedResourcePower(powerType, livingEntity, data.get("property"))
		);
	}

	private static final SerializableDataType<BiomeProperty> BIOME_PROPERTY = SerializableDataType.enumValue(BiomeProperty.class);

	private static enum BiomeProperty implements InstanceValueSupplier<Biome> {
		TEMPERATURE		(biome -> biome.getTemperature()),
		HUMIDITY		(biome -> ((BiomeAccessor)(Object) biome).getWeather().downfall());

		private final Function<Biome, Number> supplier;

		BiomeProperty(Function<Biome, Number> supplier) {
			this.supplier = supplier;
		}

		public int supplyValue(Biome biome) {
			return supplier
				.apply(biome)
				.intValue();
		}

		public Number supplyAsNumber(Biome biome) {
			return supplier.apply(biome);
		}
	}
}
