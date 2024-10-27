package io.github.xrickastley.originsmath.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.apace100.apoli.power.factory.Factory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.interfaces.SDIEntityInjection;
import io.github.xrickastley.originsmath.mixins.ActionFactoryAccessor;
import io.github.xrickastley.originsmath.mixins.ConditionFactoryAccessor;
import io.github.xrickastley.originsmath.mixins.SerializableDataAccessor;
import io.github.xrickastley.originsmath.mixins.SerializableDataFieldAccessor;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

/**
 * Utility class used for injecting "Resource-backed" versions into registries containing a {@link Factory}. <br> <br>
 * 
 * To create an ResourceBacked injection for a {@link Factory} {@link Registry}, simply register your injection
 * using the {@link #createFactoryInjection(Registry, BiFunction)} method.
 */
@SuppressWarnings("unchecked")
public class ResourceBackedInjector {
	private static final ArrayList<Pair<Registry<? extends Factory>, BiFunction<? extends Factory, SerializableData, ? extends Factory>>> injections = new ArrayList<>();

	/**
	 * Applies and clears all currently registered factory registry injections. 
	 */
	public static void applyInjections() {
		injections
			.stream()
			.forEach(pair -> injectToFactoryRegistry(ClassInstanceUtil.castClassInstance(pair.getLeft()), ClassInstanceUtil.castClassInstance(pair.getRight())));

		injections.clear();
	}

	/**
	 * Creates an injection for a {@link Registry} containing {@link Factory} instances.
	 * @param <T> The target {@link Factory} class.
	 * @param factoryRegistry The {@link Registry} containing values of {@link Factory} to inject to.
	 * @param transformationFunction The transformation function that takes the old {@link Factory} and the injected {@link SerializableData} and returns the "Resource-backed factory".
	 */
	public static <T extends Factory> void createFactoryInjection(final Registry<T> factoryRegistry, BiFunction<T, SerializableData, T> transformationFunction) {
		injections.add(new Pair<>(factoryRegistry, transformationFunction));
	}

	/**
	 * Injects a ResourceBacked creator to a Registry. This will create "Resource-backed" versions of existing and new values in the provided Registry.
	 * @param <T> The target {@link Factory} class.
	 * @param factoryRegistry The {@link Registry} containing values of {@link Factory} to target.
	 * @param transformationFunction The transformation function that takes the old {@link Factory} and the injected {@link SerializableData} and returns the "Resource-backed factory".
	 */
	private static <T extends Factory> void injectToFactoryRegistry(final Registry<T> factoryRegistry, BiFunction<T, SerializableData, T> transformationFunction) {
		final Consumer<T> resourceBackedFn = factory -> {
			// Avoid creating duplicates.
			if (factory.getSerializerId().getNamespace().equals("origins-math")) return;

			if (!shouldCreateInjection(factory.getSerializableData())) {
				OriginsMath
					.sublogger(ResourceBackedInjector.class)
					.info("Cancelling creation of ResourceBacked factory for {} (same SerializableData instances)", factory.getSerializerId());
				
				return;
			}

			final SerializableData sdCopy = factory.getSerializableData().copy();
			final LinkedHashMap<String, SerializableData.Field<?>> dataFields = ((SerializableDataAccessor) sdCopy).getDataFields();
		
			dataFields.replaceAll((_name, field) -> {
				final SerializableDataType<?> dataType = field.getDataType();

				if (dataType == SerializableDataTypes.INT) {
					return changeToResourceBacked(Integer.class, field, ResourceBacked.DataTypes.RESOURCE_BACKED_INT);
				} else if (dataType == SerializableDataTypes.FLOAT) {
					return changeToResourceBacked(Float.class, field, ResourceBacked.DataTypes.RESOURCE_BACKED_FLOAT);
				} else if (dataType == SerializableDataTypes.DOUBLE) {
					return changeToResourceBacked(Double.class, field, ResourceBacked.DataTypes.RESOURCE_BACKED_DOUBLE);
				} else {
					return field;
				}
			});
		
			T newFactory = transformationFunction.apply(factory, sdCopy);

			// Avoid duplicates.
			if (factoryRegistry.containsId(newFactory.getSerializerId())) return;
			
			OriginsMath
				.sublogger(ResourceBackedInjector.class)
				.info("Creating ResourceBacked factory ({}) for {}", newFactory.getSerializerId(), factory.getSerializerId());
		
			Registry.register(factoryRegistry, newFactory.getSerializerId(), newFactory);
		};

		factoryRegistry
			.streamEntries()
			.forEach(e -> resourceBackedFn.accept(e.value()));
		
		RegistryEntryAddedCallback
			.event(factoryRegistry)
			.register((rawId, id, factory) -> resourceBackedFn.accept(factory));
	}

	private static <T extends Number> SerializableData.Field<ResourceBacked<T>> changeToResourceBacked(Class<T> numberClass, SerializableData.Field<?> targetField, SerializableDataType<ResourceBacked<T>> dataType) {
		T defaultValue = ((SerializableDataFieldAccessor<T>) targetField).getDefaultValue();
		Function<SerializableData.Instance, T> defaultFunction = ((SerializableDataFieldAccessor<T>) targetField).getDefaultFunction();

		return defaultValue != null || defaultFunction != null
			? defaultValue != null
				? new SerializableData.Field<ResourceBacked<T>>(dataType, castToResourceBacked(numberClass, defaultValue))
				: new SerializableData.Field<ResourceBacked<T>>(dataType, castToResourceBacked(numberClass, defaultFunction))
			: new SerializableData.Field<ResourceBacked<T>>(dataType);
	}

	private static <T extends Number> ResourceBacked<T> castToResourceBacked(Class<T> numberClass, T value) {
		return ResourceBacked.fromNumber(value);
	}

	private static <T extends Number, U> Function<U, ResourceBacked<T>> castToResourceBacked(Class<T> numberClass, Function<U, T> value) {
		return u -> ResourceBacked.fromNumber(value.apply(u));
	}

	private static <T extends Factory> Identifier createSerializerId(T factory) {
		final Identifier id = factory.getSerializerId();

		return id.getNamespace().equals("apoli") || id.getNamespace().equals("origins")
			? OriginsMath.identifier(id.getPath())
			: OriginsMath.identifier(String.format("%s/%s", id.getNamespace(), id.getPath()));
	}

	private static boolean shouldCreateInjection(SerializableData a) {
		LinkedHashMap<String, SerializableData.Field<?>> aDataFields = ((SerializableDataAccessor) a).getDataFields();

		return aDataFields
			.values()
			.stream()
			.anyMatch(field -> {
				SerializableDataType<?> dataType = field.getDataType();

				return dataType == SerializableDataTypes.INT 
					|| dataType == SerializableDataTypes.FLOAT
					|| dataType == SerializableDataTypes.DOUBLE;
			});
	}

	static {
		createFactoryInjection(
			ApoliRegistries.ENTITY_ACTION,
			(oldFactory, newData) -> new ActionFactory<Entity>(
				createSerializerId(oldFactory),
				newData,
				(data, entity) -> {
					((SDIEntityInjection) data).setEntity(entity);

					((ActionFactoryAccessor<Entity>) oldFactory).getEffect().accept(data, entity);
				}
			)
		);

		createFactoryInjection(
			ApoliRegistries.ENTITY_CONDITION,
			(oldFactory, newData) -> new ConditionFactory<Entity>(
				createSerializerId(oldFactory),
				newData,
				(data, entity) -> {
					((SDIEntityInjection) data).setEntity(entity);

					return ((ConditionFactoryAccessor<Entity>) oldFactory).getCondition().apply(data, entity);
				}
			)
		);
	}
}
