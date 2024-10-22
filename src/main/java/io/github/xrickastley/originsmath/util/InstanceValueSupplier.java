package io.github.xrickastley.originsmath.util;

public interface InstanceValueSupplier<T> {
	public int supplyValue(T instance);
	public Number supplyAsNumber(T instance);
}
