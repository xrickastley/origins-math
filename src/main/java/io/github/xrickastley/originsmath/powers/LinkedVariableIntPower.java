package io.github.xrickastley.originsmath.powers;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;

/**
 * A {@link VariableIntPower} for linking various Minecraft values through a value supplier, either
 * through {@code supplyValue()} or {@code supplyDoubleValue()}. <br> <br>
 * 
 * Since values are based on Minecraft values, modifying this resource will have no effect on it or
 * it's linked values, as it's values are obtained through {@link LinkedVariableIntPower#supplyValue()}.
 */
public abstract class LinkedVariableIntPower extends VariableIntPower {
	public LinkedVariableIntPower(PowerType<?> type, LivingEntity entity) {
		super(type, entity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Gets the supplied value of this {@code LinkedVariableIntPower}. This value is used
	 * as the supplier for the various {@code VariableIntPower} methods, so an {@code int}
	 * must be returned.
	 * @return The linked value of this {@code LinkedVariableIntPower}.
	 */
	protected abstract int supplyValue();

	/**
	 * Gets the value of this {@code LinkedVariableIntPower}. This more-precise value is 
	 * used in the {@code VariableSerializer}, where values of {@code Argument}s use a 
	 * {@code double} instead of the value supplied by {@link LinkedVariableIntPower#supplyValue()} 
	 * @return The linked value of this {@code LinkedVariableIntPower}, with extra precision as a double.
	 */
	public abstract double supplyDoubleValue();

	@Override
	public int getMin() {
		return this.min;
	}
	
	@Override
	public int getMax() {
		return this.max;
	}

	@Override
	public int getValue() {
		return supplyValue();
	}

	@Override
    public int setValue(int newValue) {
        return supplyValue();
    }

	@Override
    public int increment() {
        return supplyValue();
    }

	@Override
    public int decrement() {
        return supplyValue();
    }

    @Override
    public NbtElement toTag() {
        return NbtInt.of(supplyValue());
    }

    @Override
    public void fromTag(NbtElement tag) {
        currentValue = (int) Math.floor(supplyValue());
    }
}
