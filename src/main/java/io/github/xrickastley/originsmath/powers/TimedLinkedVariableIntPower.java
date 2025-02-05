package io.github.xrickastley.originsmath.powers;

import java.util.function.Function;

import javax.annotation.Nullable;

import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;

/**
 * A subclass of {@link LinkedVariableIntPower} used for temporary values that are held
 * for a certain duration.
 */
public abstract class TimedLinkedVariableIntPower<T> extends LinkedVariableIntPower {
	// Is a long to prevent integer underflow from Integer.MAX_VALUE ("infinity") as the duration.
	private final long duration;
	private final Function<T, Number> converter;

	private int lastSetAt;
	private @Nullable T temporaryValue = null;

	/**
	 * Creates a {@link TimedLinkedVariableIntPower}.
	 * @param type The {@link PowerType} to use in creating this {@link SuppliedLinkedVariableIntPower}.
	 * @param entity The {@link LivingEntity} this {@link SuppliedLinkedVariableIntPower} belongs to.
	 * @param duration The duration in ticks that the {@code temporaryValue} can last for.
	 * @param converter A {@link Function} used for converting the {@code temporaryValue} to a {@link Number}. If the temporary value does not exist, the resulting value of this resource would be {@code 0}. 
	 */
	public TimedLinkedVariableIntPower(PowerType<?> type, LivingEntity entity, int duration, Function<T, Number> converter) {
		super(type, entity);

		this.duration = duration;
		this.converter = converter;
	}

	protected void setTemporaryValue(T value) {
		this.temporaryValue = value;

		this.lastSetAt = entity.age;
	}

	protected @Nullable T getTemporaryValue() {
		if (this.entity.age > (this.lastSetAt + this.duration)) this.temporaryValue = null;

		return this.temporaryValue;
	}

	private Number getNumberValue() {
		return this.getTemporaryValue() == null
			? 0 
			: this.converter.apply(this.getTemporaryValue());
	}

	@Override
	protected int supplyValue() {
		return this.getNumberValue().intValue();
	}

	@Override
	public double supplyDoubleValue() {
		return this.getNumberValue().doubleValue();
	}
}
