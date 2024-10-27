package io.github.xrickastley.originsmath.powers;

import java.util.function.Consumer;

import io.github.apace100.apoli.component.PowerHolderComponent;
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

public class ModifiableResourcePower extends ResourcePower {
    private ModifiableResourcePower(PowerType<?> type, LivingEntity entity, HudRender hudRender, int startValue, int min, int max, Consumer<Entity> actionOnMin, Consumer<Entity> actionOnMax) {
        super(type, entity, hudRender, startValue, min, max, actionOnMin, actionOnMax);
    }

    public int getMin() {
        return (int) PowerHolderComponent.modify(entity, ModifyResourceMinimum.class, min);
    }

    public int getMax() {
        return (int) PowerHolderComponent.modify(entity, ModifyResourceMaximum.class, max);
    }

    public int setValue(int newValue) {
        return currentValue = MathHelper.clamp(newValue, this.getMin(), this.getMax());
    }

    @Override
    public void fromTag(NbtElement tag) {
        currentValue = MathHelper.clamp(((NbtInt) tag).intValue(), this.getMin(), this.getMax());
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(OriginsMath.identifier("modifiable_resource"),
            new SerializableData()
                .add("min", SerializableDataTypes.INT)
                .add("max", SerializableDataTypes.INT)
                .addFunctionedDefault("start_value", SerializableDataTypes.INT, data -> data.getInt("min"))
                .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                .add("min_action", ApoliDataTypes.ENTITY_ACTION, null)
                .add("max_action", ApoliDataTypes.ENTITY_ACTION, null),
            data -> (type, player) -> new ResourcePower(
				type,
				player,
                data.get("hud_render"),
                data.getInt("start_value"),
                data.getInt("min"),
                data.getInt("max"),
                data.get("min_action"),
                data.get("max_action")
			)
		).allowCondition();
    }
}
