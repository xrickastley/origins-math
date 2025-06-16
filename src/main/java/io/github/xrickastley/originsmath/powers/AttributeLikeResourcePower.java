package io.github.xrickastley.originsmath.powers;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.MathHelper;

/**
 * A Resource that "acts" as an attribute.
 */
public class AttributeLikeResourcePower extends VariableIntPower {
    protected final double absoluteMin, absoluteMax;
	protected double currentAbsoluteValue;

	private AttributeLikeResourcePower(PowerType<?> type, LivingEntity entity, double startValue, double min, double max) {
		super(type, entity, (int) startValue, (int) min, (int) max);	

		this.currentAbsoluteValue = startValue;
        this.absoluteMin = min;
        this.absoluteMax = max;
	}

    @Override
    public int getMin() {
        return min;
    }

    public double getAbsoluteMin() {
        return absoluteMin;
    }

    @Override
    public int getMax() {
        return max;
    }

    public double getAbsoluteMax() {
        return absoluteMax;
    }

	@Override
    public int getValue() {
        return (int) this.getAbsoluteValue();
    }

	public double getAbsoluteValue() {
		return currentAbsoluteValue;
	}

    @Override
    public int setValue(int newValue) {
        return (int) this.setAbsoluteValue(newValue);
    }

    public double setAbsoluteValue(double newValue) {
        System.out.println("Sss");

        return currentAbsoluteValue = MathHelper.clamp(newValue, min, max);
    }

    public int addValue(int addedValue) {
        return (int) this.addAbsoluteValue(addedValue);
    }

    public double addAbsoluteValue(double addedValue) {
        System.out.println("addedValue: " + addedValue);

		final double finalAddedValue = ResourceModifyingPower.applyModifiers(entity, ModifyAttributeLikeResourcePower.class, addedValue, this.type);

        System.out.println(finalAddedValue);

        return currentAbsoluteValue = MathHelper.clamp(this.currentAbsoluteValue + finalAddedValue, min, max);
    }

    @Override
    public int increment() {
        return setValue(getValue() + 1);
    }

    @Override
    public int decrement() {
        return setValue(getValue() - 1);
    }

    @Override
    public NbtElement toTag() {
        return NbtDouble.of(currentAbsoluteValue);
    }

    @Override
    public void fromTag(NbtElement tag) {
        currentAbsoluteValue = MathHelper.clamp(((NbtDouble) tag).doubleValue(), min, max);
    }

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("attribute_like_resource"),
			new SerializableData()
                .add("min", SerializableDataTypes.DOUBLE)
                .add("max", SerializableDataTypes.DOUBLE)
                .addFunctionedDefault("start_value", SerializableDataTypes.DOUBLE, data -> data.getDouble("min")),
			data -> (powerType, livingEntity) -> new AttributeLikeResourcePower(powerType, livingEntity, data.getDouble("start_value"), data.getDouble("min"), data.getDouble("max"))
		);
	}
}