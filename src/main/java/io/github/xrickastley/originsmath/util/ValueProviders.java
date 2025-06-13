package io.github.xrickastley.originsmath.util;

import java.util.HashMap;
import java.util.Map;
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

	@SuppressWarnings("unchecked")
	public static <T extends Power> void registerProvider(Class<T> powerClass, ValueProvider<T> provider) {
		if (ValueProviders.PROVIDERS.containsKey(powerClass))
			throw new IllegalStateException("Registering duplicate Power class '" + powerClass.getName() + "' to this ValueProvider!");
			
		ValueProviders.PROVIDERS.put((Class<Power>) powerClass, (ValueProvider<Power>) provider);
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
		return ValueProviders.getProvider(powerType.get(entity));
	}

	public static ValueProvider<Power> getProviderOrThrow(Power power) {
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
		return ValueProviders.getProviderOrThrow(powerType.get(entity));
	}

	public static Number getValue(Power power) {
		return ValueProviders.getProvider(power).VALUE_PROVIDER.apply(power);
	}

	public static Number getValue(PowerType<?> powerType, Entity entity) {
		return ValueProviders.getValue(powerType.get(entity));
	}

	public static Number getValueOrThrow(Power power) {
		return ValueProviders.getProviderOrThrow(power).VALUE_PROVIDER.apply(power);
	}

	public static Number getValueOrThrow(PowerType<?> powerType, Entity entity) {
		return ValueProviders.getValueOrThrow(powerType.get(entity));
	}

	public static Number getMax(Power power) {
		return ValueProviders.getProvider(power).MAX_PROVIDER.apply(power);
	}

	public static Number getMax(PowerType<?> powerType, Entity entity) {
		return ValueProviders.getMax(powerType.get(entity));
	}

	public static Number getMaxOrThrow(Power power) {
		return ValueProviders.getProviderOrThrow(power).MAX_PROVIDER.apply(power);
	}

	public static Number getMaxOrThrow(PowerType<?> powerType, Entity entity) {
		return ValueProviders.getMaxOrThrow(powerType.get(entity));
	}

	public static Number getMin(Power power) {
		return ValueProviders.getProvider(power).MIN_PROVIDER.apply(power);
	}

	public static Number getMin(PowerType<?> powerType, Entity entity) {
		return ValueProviders.getMin(powerType.get(entity));
	}

	public static Number getMinOrThrow(Power power) {
		return ValueProviders.getProviderOrThrow(power).MIN_PROVIDER.apply(power);
	}

	public static Number getMinOrThrow(PowerType<?> powerType, Entity entity) {
		return ValueProviders.getMinOrThrow(powerType.get(entity));
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
		
		ValueProviders.registerProvider(
			CooldownPower.class, 
			new ValueProvider<>(CooldownPower::getRemainingTicks, p -> 0, p -> p.cooldownDuration)
		);
	}

	public static class ValueProvider<T extends Power> {
		public ValueProvider(Function<T, Number> valueProvider, Function<T, Number> minProvider, Function<T, Number> maxProvider) {
			this.MIN_PROVIDER = minProvider;
			this.VALUE_PROVIDER = valueProvider;
			this.MAX_PROVIDER = maxProvider;
		}

		private static final ValueProvider<Power> EMPTY = new ValueProvider<>(p -> 0, p -> 0, p -> 0);

		public final Function<T, Number> MIN_PROVIDER;
		public final Function<T, Number> VALUE_PROVIDER;
		public final Function<T, Number> MAX_PROVIDER;
	}
}
