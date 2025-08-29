package io.github.xrickastley.originsmath.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.interfaces.SDIEntityInjection;
import io.github.xrickastley.originsmath.mixins.PowerFactoryAccessor;
import io.github.xrickastley.originsmath.mixins.SerializableDataAccessor;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.registry.entry.RegistryEntry.Reference;

public class CustomHudRenderInjector {
	private static boolean INJECTED = false;

	/**
	 * Applies the currently power registry injection. 
	 */
	@SuppressWarnings("unchecked")
	public static void applyInjections() {
		if (CustomHudRenderInjector.INJECTED) return;
		
		ApoliRegistries.POWER_FACTORY
			.streamEntries()
			.forEach(CustomHudRenderInjector.compose(CustomHudRenderInjector::addCustomHudRender, Reference::value));
		
		RegistryEntryAddedCallback
			.event(ApoliRegistries.POWER_FACTORY)
			.register((rawId, id, factory) -> CustomHudRenderInjector.addCustomHudRender(factory));

		CustomHudRenderInjector.INJECTED = true;
	}

	private static void addCustomHudRender(PowerFactory<Power> factory) {
		if (!shouldCreateInjection(factory.getSerializableData())) {
			OriginsMath
				.sublogger(CustomHudRenderInjector.class)
				.info("Cancelling creation of custom HUD Render field for {} (same SerializableData instances)", factory.getSerializerId());
			
			return;
		}

		// Use this to prevent ConcurrentModificationException/entrySet returning new elements.
		final List<String> hudRenderFields = new ArrayList<>();
		final Map<String, SerializableData.Field<?>> dataFields = ((SerializableDataAccessor) factory.getSerializableData()).getDataFields();

		for (final var entry : dataFields.entrySet()) {
			final String name = entry.getKey();
			final SerializableData.Field<?> field = entry.getValue();
			
			if (field.getDataType() != ApoliDataTypes.HUD_RENDER || (name.startsWith("origins-math:") && field.getDataType() == OriginsMathHudRender.DATA_TYPE)) continue;

			hudRenderFields.add("origins-math:" + name);
		}

		final SerializableData data = factory.getSerializableData();

		for (final String field : hudRenderFields) data.add(field, OriginsMathHudRender.DATA_TYPE, null);

		CustomHudRenderInjector.injectToFactoryConstructor(factory);

		OriginsMath
			.sublogger(CustomHudRenderInjector.class)
			.info("Created custom HUD render fields ({}) for {}", hudRenderFields, factory.getSerializerId());
	}

	@SuppressWarnings("unchecked")
	private static void injectToFactoryConstructor(PowerFactory<Power> powerFactory) {
		final var fn = ((PowerFactoryAccessor<Power>) powerFactory).getFactoryConstructor();

		((PowerFactoryAccessor<Power>) powerFactory).setFactoryConstructor(data -> {
			return (powerType, livingEntity) -> {
				((SDIEntityInjection) data).setEntity(livingEntity);

				return fn.apply(data).apply(powerType, livingEntity);
			};
		});
	}

	private static boolean shouldCreateInjection(SerializableData a) {
		LinkedHashMap<String, SerializableData.Field<?>> aDataFields = ((SerializableDataAccessor) a).getDataFields();

		return aDataFields
			.values()
			.stream()
			.anyMatch(field -> field.getDataType() == ApoliDataTypes.HUD_RENDER);
	}

	private static <T, R> Consumer<T> compose(Consumer<R> consumer, Function<T, R> transformer) {
		return t -> consumer.accept(transformer.apply(t));
	}
}
