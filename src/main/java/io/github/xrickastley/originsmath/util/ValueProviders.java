package io.github.xrickastley.originsmath.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.xrickastley.originsmath.powers.LinkedVariableIntPower;

import net.minecraft.entity.Entity;

/**
 * A utility class that allows the registering of "Value Providers" that convert a {@link Power}
 * into a {@link Number}. <br> <br>
 * 
 * A registered "Value Provider" is used based on the superclass hierarchy of the provided 
 * {@link Power} instance, where a {@link Class} instance that's higher in the hierarchy, i.e.
 * "most recently extended from", is prioritized over a {@link Class} instance lower in the 
 * hierarchy. <br> <br>
 * 
 * A value provider should, as much as possible, give the most <i>precise</i> value that the
 * corresponding {@link Power} subclass can hold, such as {@link LinkedVariableIntPower} using
 * {@link LinkedVariableIntPower#supplyDoubleValue()}, returning a {@code double}, the most
 * precise number-holding type out of all the primitive number types.
 */
public class ValueProviders {
	private static final Map<Class<Power>, ValueProvider<Power>> PROVIDERS = new HashMap<>();
	private static final Map<Class<Power>, ValueModifier<Power>> MODIFIERS = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <T extends Power> void registerProvider(Class<T> powerClass, ValueProvider<T> provider) {
		if (ValueProviders.PROVIDERS.containsKey(powerClass))
			throw new IllegalStateException("Registering another ValueProvider for duplicate Power class '" + powerClass.getName() + "' to this class!");
			
		ValueProviders.PROVIDERS.put((Class<Power>) powerClass, (ValueProvider<Power>) provider);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Power> void registerModifier(Class<T> powerClass, ValueModifier<T> provider) {
		if (ValueProviders.MODIFIERS.containsKey(powerClass))
			throw new IllegalStateException("Registering another ValueProvider for duplicate Power class '" + powerClass.getName() + "' to this class!");
			
		ValueProviders.MODIFIERS.put((Class<Power>) powerClass, (ValueModifier<Power>) provider);
	}

	public static boolean hasProvider(Class<Power> powerClass) {
		return PROVIDERS.keySet().contains(powerClass);
	}

	public static boolean hasProvider(Power power) {
		Class<?> superclass = power.getClass().getSuperclass();

		while (superclass != null) {
			if (PROVIDERS.keySet().contains(superclass)) {
				return true;
			} else {
				superclass = superclass.getSuperclass();
			}
		}

		return false;
	}

	public static ValueProvider<Power> getProvider(Power power) {
		try {
			return ValueProviders.getProviderOrThrow(power);
		} catch (IllegalArgumentException e) {
			return ValueProvider.EMPTY;
		}
	}
	
	public static ValueProvider<Power> getProvider(PowerType<?> powerType, Entity entity) {
		final Power power = powerType.get(entity);

		if (power == null) throw new IllegalArgumentException("The provided power type '" + powerType.getIdentifier() + "' does not exist for the provided entity: " + entity.toString() + "!");

		return ValueProviders.getProvider(power);
	}

	public static ValueProvider<Power> getProviderOrThrow(Power power) {
		if (power == null) throw new IllegalArgumentException("You cannot get the ValueProvider of a null power!");

		Class<?> superclass = power.getClass().getSuperclass();

		while (superclass != null) {
			if (PROVIDERS.keySet().contains(superclass)) {
				return ValueProviders.PROVIDERS.get(superclass);
			} else {
				superclass = superclass.getSuperclass();
			}
		}

		throw new IllegalArgumentException("The provided power '" + power.getType().getIdentifier() + "' doesn't have a registered ValueProvider!");
	}
	
	public static ValueProvider<Power> getProviderOrThrow(PowerType<?> powerType, Entity entity) {
		final Power power = powerType.get(entity);

		if (power == null) throw new IllegalArgumentException("The provided power type '" + powerType.getIdentifier() + "' does not exist for the provided entity: " + entity.toString() + "!");

		return ValueProviders.getProviderOrThrow(power);
	}

	

	public static boolean hasModifier(Class<Power> powerClass) {
		return PROVIDERS.keySet().contains(powerClass);
	}

	public static boolean hasModifier(Power power) {
		Class<?> superclass = power.getClass().getSuperclass();

		while (superclass != null) {
			if (PROVIDERS.keySet().contains(superclass)) {
				return true;
			} else {
				superclass = superclass.getSuperclass();
			}
		}

		return false;
	}

	public static ValueModifier<Power> getModifier(Power power) {
		try {
			return ValueProviders.getModifierOrThrow(power);
		} catch (IllegalArgumentException e) {
			return ValueModifier.EMPTY;
		}
	}
	
	public static ValueModifier<Power> getModifier(PowerType<?> powerType, Entity entity) {
		final Power power = powerType.get(entity);

		if (power == null) throw new IllegalArgumentException("The provided power type '" + powerType.getIdentifier() + "' does not exist for the provided entity: " + entity.toString() + "!");

		return ValueProviders.getModifier(power);
	}

	public static ValueModifier<Power> getModifierOrThrow(Power power) {
		if (power == null) throw new IllegalArgumentException("You cannot get the ValueModifier of a null power!");

		Class<?> superclass = power.getClass().getSuperclass();

		while (superclass != null) {
			if (MODIFIERS.keySet().contains(superclass)) {
				return ValueProviders.MODIFIERS.get(superclass);
			} else {
				superclass = superclass.getSuperclass();
			}
		}

		throw new IllegalArgumentException("The provided power '" + power.getType().getIdentifier() + "' doesn't have a registered ValueModifier!");
	}
	
	public static ValueModifier<Power> getModifierOrThrow(PowerType<?> powerType, Entity entity) {
		final Power power = powerType.get(entity);

		if (power == null) throw new IllegalArgumentException("The provided power type '" + powerType.getIdentifier() + "' does not exist for the provided entity: " + entity.toString() + "!");

		return ValueProviders.getModifierOrThrow(power);
	}



	public static Number getValue(Power power) {
		return ValueProviders.getProvider(power).VALUE_PROVIDER.apply(power);
	}

	public static Number getValue(PowerType<?> powerType, Entity entity) {
		final Power power = powerType.get(entity);

		if (power == null) throw new IllegalArgumentException("The provided power type '" + powerType.getIdentifier() + "' does not exist for the provided entity: " + entity.toString() + "!");

		return ValueProviders.getValue(power);
	}

	public static Number getValueOr(Power power, Number or) {
		return power != null
			? ValueProviders.getProvider(power).VALUE_PROVIDER.apply(power)
			: or;
	}

	public static Number getValueOr(PowerType<?> powerType, Entity entity, Number or) {
		return ValueProviders.getValueOr(powerType.get(entity), or);
	}

	public static Number getValueOrThrow(Power power) {
		return ValueProviders.getProviderOrThrow(power).VALUE_PROVIDER.apply(power);
	}

	public static Number getValueOrThrow(PowerType<?> powerType, Entity entity) {
		final Power power = powerType.get(entity);

		if (power == null) throw new IllegalArgumentException("The provided power type '" + powerType.getIdentifier() + "' does not exist for the provided entity: " + entity.toString() + "!");

		return ValueProviders.getValueOrThrow(power);
	}

	public static Number getMax(Power power) {
		return ValueProviders.getProvider(power).MAX_PROVIDER.apply(power);
	}

	public static Number getMax(PowerType<?> powerType, Entity entity) {
		final Power power = powerType.get(entity);

		if (power == null) throw new IllegalArgumentException("The provided power type '" + powerType.getIdentifier() + "' does not exist for the provided entity: " + entity.toString() + "!");

		return ValueProviders.getMax(power);
	}

	public static Number getMaxOrThrow(Power power) {
		return ValueProviders.getProviderOrThrow(power).MAX_PROVIDER.apply(power);
	}

	public static Number getMaxOrThrow(PowerType<?> powerType, Entity entity) {
		final Power power = powerType.get(entity);

		if (power == null) throw new IllegalArgumentException("The provided power type '" + powerType.getIdentifier() + "' does not exist for the provided entity: " + entity.toString() + "!");

		return ValueProviders.getMaxOrThrow(power);
	}

	public static Number getMin(Power power) {
		return ValueProviders.getProvider(power).MIN_PROVIDER.apply(power);
	}

	public static Number getMin(PowerType<?> powerType, Entity entity) {
		final Power power = powerType.get(entity);

		if (power == null) throw new IllegalArgumentException("The provided power type '" + powerType.getIdentifier() + "' does not exist for the provided entity: " + entity.toString() + "!");

		return ValueProviders.getMin(power);
	}

	public static Number getMinOrThrow(Power power) {
		return ValueProviders.getProviderOrThrow(power).MIN_PROVIDER.apply(power);
	}

	public static Number getMinOrThrow(PowerType<?> powerType, Entity entity) {
		final Power power = powerType.get(entity);

		if (power == null) throw new IllegalArgumentException("The provided power type '" + powerType.getIdentifier() + "' does not exist for the provided entity: " + entity.toString() + "!");

		return ValueProviders.getMinOrThrow(power);
	}

	static {
		ValueProviders.registerProvider(
			LinkedVariableIntPower.class, 
			new ValueProvider<>(LinkedVariableIntPower::supplyDoubleValue, LinkedVariableIntPower::getMin, LinkedVariableIntPower::getMax)
		);



		ValueProviders.registerProvider(
			VariableIntPower.class,
			new ValueProvider<>(VariableIntPower::getValue, VariableIntPower::getMin, VariableIntPower::getMax)
		);

		ValueProviders.registerModifier(
			VariableIntPower.class,
			new ValueModifier<>((p, v) -> p.setValue(v.intValue()), (p, v) -> p.setValue(p.getValue() + v.intValue()))
		);



		ValueProviders.registerProvider(
			CooldownPower.class, 
			new ValueProvider<>(CooldownPower::getRemainingTicks, p -> 0, p -> p.cooldownDuration)
		);

		ValueProviders.registerModifier(
			CooldownPower.class,
			new ValueModifier<>((p, v) -> p.modify(v.intValue()), (p, v) -> p.setCooldown(v.intValue()))
		);
	}

	public static class ValueProvider<T extends Power> {
		public ValueProvider(Function<T, Number> valueProvider, Function<T, Number> minProvider, Function<T, Number> maxProvider) {
			this.MIN_PROVIDER = minProvider.andThen(n -> n.doubleValue());
			this.VALUE_PROVIDER = valueProvider.andThen(n -> n.doubleValue());
			this.MAX_PROVIDER = maxProvider.andThen(n -> n.doubleValue());
		}

		private static final ValueProvider<Power> EMPTY = new ValueProvider<>(p -> 0, p -> 0, p -> 0);

		public final Function<T, Double> MIN_PROVIDER;
		public final Function<T, Double> VALUE_PROVIDER;
		public final Function<T, Double> MAX_PROVIDER;
	}

	public static class ValueModifier<T extends Power> {
		public ValueModifier(BiConsumer<T, Number> setModifier, BiConsumer<T, Number> addModifier) {
			this.SET_MODIFIER = setModifier;
			this.ADD_MODIFIER = addModifier;
		}

		private static final ValueModifier<Power> EMPTY = new ValueModifier<>((p, v) -> {}, (p, v) -> {});

		public final BiConsumer<T, Number> SET_MODIFIER;
		public final BiConsumer<T, Number> ADD_MODIFIER;
	}
}
