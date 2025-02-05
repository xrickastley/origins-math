package io.github.xrickastley.originsmath.powers;

import java.util.function.Supplier;

import io.github.apace100.apoli.power.PowerType;
import io.github.xrickastley.originsmath.util.InstanceValueSupplier;

import net.minecraft.entity.LivingEntity;

/**
 * A subclass of {@link LinkedVariableIntPower} used for simple linked values that can be 
 * easily supplied through the {@code InstanceValueSupplier<T>}.
 */
public abstract class SuppliedLinkedVariableIntPower<T> extends LinkedVariableIntPower {
	private final InstanceValueSupplier<T> valueSupplier;
	private final Supplier<T> supplierFn;

	/**
	 * Creates a {@link SuppliedLinkedVariableIntPower}.
	 * @param type The {@link PowerType} to use in creating this {@link SuppliedLinkedVariableIntPower}.
	 * @param entity The {@link LivingEntity} this {@link SuppliedLinkedVariableIntPower} belongs to.
	 * @param valueSupplier A class instance (preferably an enum value) whose class or enum implements the {@link InstanceValueSupplier}.
	 * @param supplierFn A {@link Supplier} used for supplying the value to be used for the {@link valueSupplier}. If {@code null} is provided, the resulting value of this resource would be {@code 0}. 
	 */
	public SuppliedLinkedVariableIntPower(PowerType<?> type, LivingEntity entity, InstanceValueSupplier<T> valueSupplier, Supplier<T> supplierFn) {
		super(type, entity);

		this.valueSupplier = valueSupplier;
		this.supplierFn = supplierFn;
	}

	/**
	 * Gets the supplied value of this {@link LinkedVariableIntPower}.
	 * @return The linked value of this {@link LinkedVariableIntPower}.
	 */
	protected int supplyValue() {
		return supplierFn.get() == null
			? 0
			: valueSupplier.supplyValue(supplierFn.get());
	}

	/**
	 * Gets the value of this {@link LinkedVariableIntPower} for extra precision.
	 * @return The linked value of this {@link LinkedVariableIntPower} as a double.
	 */
	public double supplyDoubleValue() {
		return supplierFn.get() == null
			? 0
			: valueSupplier
				.supplyAsNumber(supplierFn.get())
				.doubleValue();
	};
}
