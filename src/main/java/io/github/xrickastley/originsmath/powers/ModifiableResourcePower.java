package io.github.xrickastley.originsmath.powers;

import java.util.function.Consumer;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ResourcePower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.util.math.MathHelper;

/**
 * A resource power whose maximum and minimum value can be modified.
 */
public class ModifiableResourcePower extends ResourcePower {
	
    private final Consumer<Entity> actionOnMin;
    private final Consumer<Entity> actionOnMax;
	private final boolean enforceLimits;
    private final boolean retainValue;

    private ModifiableResourcePower(PowerType<?> type, LivingEntity entity, HudRender hudRender, int startValue, int min, int max, boolean enforceLimits, boolean retainValue, Consumer<Entity> actionOnMin, Consumer<Entity> actionOnMax) {
        super(type, entity, hudRender, startValue, min, max, null, null);

		this.actionOnMin = actionOnMin;
		this.actionOnMax = actionOnMax;
		this.enforceLimits = enforceLimits;
        this.retainValue = retainValue;
	}

    @Override
    public int getMin() {
        return ResourceBoundModifyingPower.applyModifiers(
            entity, 
            ModifyResourceMinimum.class, 
            min, 
            this.getType()
        );
    }

    @Override
    public int getMax() {
        return ResourceBoundModifyingPower.applyModifiers(
            entity, 
            ModifyResourceMinimum.class, 
            max, 
            this.getType()
        );
    }

	@Override
	public int getValue() {
		return enforceLimits
			? !retainValue
                // Don't retain current value, and enforce the limits.
                ? this.currentValue = MathHelper.clamp(this.currentValue, this.getMin(), this.getMax())
                // Retain the current value, but enforce the limits.
                : MathHelper.clamp(this.currentValue, this.getMin(), this.getMax())
            // Don't enforce the limits (no need for retaining the value).
			: this.currentValue;
	}

	@Override
	public int setValue(int newValue) {
		System.out.println("value set!");

        // If the current value is to be retained and the new value isn't bounded by min and max, return the current value. 
        if (retainValue && (newValue < this.getMin() || newValue > this.getMax())) return this.currentValue;

        int oldValue = this.currentValue;
        int actualNewValue = (this.currentValue = (int) MathHelper.clamp(newValue, this.getMin(), this.getMax()));

		System.out.printf("MathHelper.clamp(%d, %d, %d) = %d\n", newValue, this.getMin(), this.getMax(), (int) MathHelper.clamp(newValue, this.getMin(), this.getMax()));
		System.out.println(actualNewValue);

		if (oldValue != actualNewValue) {
            if (this.actionOnMin != null && actualNewValue == min) actionOnMin.accept(entity);
            if (this.actionOnMax != null && actualNewValue == max) actionOnMax.accept(entity);
        }

		return actualNewValue;
	}

    @Override
    public void fromTag(NbtElement tag) {
        currentValue = MathHelper.clamp(((NbtInt) tag).intValue(), this.getMin(), this.getMax());
    }

    @Override
    public float getFill() {
        return MathHelper.clamp((this.getValue() - this.getMin()) / (float)(this.getMax() - this.getMin()), 0, 1);
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(OriginsMath.identifier("modifiable_resource"),
            new SerializableData()
                .add("min", SerializableDataTypes.INT)
                .add("max", SerializableDataTypes.INT)
                .addFunctionedDefault("start_value", SerializableDataTypes.INT, data -> data.getInt("min"))
				.add("enforce_limits", SerializableDataTypes.BOOLEAN, true)
				.add("retain_value", SerializableDataTypes.BOOLEAN, true)
                .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                .add("min_action", ApoliDataTypes.ENTITY_ACTION, null)
                .add("max_action", ApoliDataTypes.ENTITY_ACTION, null),
            data -> (type, player) -> new ModifiableResourcePower(
				type,
				player,
                data.get("hud_render"),
                data.getInt("start_value"),
                data.getInt("min"),
                data.getInt("max"),
				data.getBoolean("enforce_limits"),
				data.getBoolean("retain_value"),
                data.get("min_action"),
                data.get("max_action")
			)
		).allowCondition();
    }
}
