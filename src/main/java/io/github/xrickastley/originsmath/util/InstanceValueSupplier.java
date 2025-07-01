package io.github.xrickastley.originsmath.util;

public interface InstanceValueSupplier<T> {
	public Number supplyAsNumber(T instance);
	
	default int supplyValue(T instance) {
		return this
			.supplyAsNumber(instance)
			.intValue();
	}
}
