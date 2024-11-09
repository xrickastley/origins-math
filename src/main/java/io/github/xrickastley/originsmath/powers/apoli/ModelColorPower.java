package io.github.xrickastley.originsmath.powers.apoli;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.ResourceBacked;
import net.minecraft.entity.LivingEntity;

public class ModelColorPower extends Power {

	private final ResourceBacked<Float> red;
	private final ResourceBacked<Float> green;
	private final ResourceBacked<Float> blue;
	private final ResourceBacked<Float> alpha;

	public ModelColorPower(PowerType<?> type, LivingEntity entity, ResourceBacked<Float> red, ResourceBacked<Float> green, ResourceBacked<Float> blue, ResourceBacked<Float> alpha) {
		super(type, entity);
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public float getRed() {
		return red.floatValue();
	}

	public float getGreen() {
		return green.floatValue();
	}

	public float getBlue() {
		return blue.floatValue();
	}

	public float getAlpha() {
		return alpha.floatValue();
	}

	public boolean isTranslucent() {
		return alpha.floatValue() < 1.0F;
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(OriginsMath.identifier("model_color"),
			new SerializableData()
				.add("red", SerializableDataTypes.FLOAT, 1.0F)
				.add("green", SerializableDataTypes.FLOAT, 1.0F)
				.add("blue", SerializableDataTypes.FLOAT, 1.0F)
				.add("alpha", SerializableDataTypes.FLOAT, 1.0F),
			data -> (type, player) -> new ModelColorPower(
				type, 
				player, 
				data.get("red"), 
				data.get("green"), 
				data.get("blue"), 
				data.get("alpha")
			)
		).allowCondition();
	}
}
