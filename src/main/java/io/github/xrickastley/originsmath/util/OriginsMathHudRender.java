package io.github.xrickastley.originsmath.util;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public final class OriginsMathHudRender extends HudRender {
    public static final OriginsMathHudRender DONT_RENDER = new OriginsMathHudRender(false, ResourceBacked.fromNumber(0), ResourceBacked.fromNumber(0), Apoli.identifier("textures/gui/resource_bar.png"), null, false);

	private final ResourceBacked<Integer> barIndex;
	private final ResourceBacked<Integer> iconIndex;

	public OriginsMathHudRender(boolean shouldRender, ResourceBacked<Integer> barIndex, ResourceBacked<Integer> iconIndex, Identifier spriteLocation, ConditionFactory<LivingEntity>.Instance condition, boolean inverted) {
		super(shouldRender, -1, -1, spriteLocation, condition, inverted);

		this.barIndex = barIndex;
		this.iconIndex = iconIndex;
	}

	@Override
	public int getBarIndex() {
		return barIndex.intValue();
	}

	@Override
	public int getIconIndex() {
		return iconIndex.intValue();
	}

	public void setTargetEntity(Entity entity) {
		this.barIndex.setTargetEntity(entity);
		this.iconIndex.setTargetEntity(entity);
	}

	public static final SerializableDataType<OriginsMathHudRender> DATA_TYPE = SerializableDataType.compound(
		OriginsMathHudRender.class,
		new SerializableData()
			.add("should_render", SerializableDataTypes.BOOLEAN, true)
			.add("bar_index", ResourceBacked.DataTypes.RESOURCE_BACKED_INT, ResourceBacked.fromNumber(0))
			.add("icon_index", ResourceBacked.DataTypes.RESOURCE_BACKED_INT, ResourceBacked.fromNumber(0))
			.add("sprite_location", SerializableDataTypes.IDENTIFIER, Identifier.of("apoli", "textures/gui/resource_bar.png"))
			.add("condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("inverted", SerializableDataTypes.BOOLEAN, false),
		dataInst -> new OriginsMathHudRender(
			dataInst.getBoolean("should_render"),
			dataInst.get("bar_index"),
			dataInst.get("icon_index"),
			dataInst.getId("sprite_location"),
			dataInst.get("condition"),
			dataInst.getBoolean("inverted")),
		(data, inst) -> {
			SerializableData.Instance dataInst = data.new Instance();
			dataInst.set("should_render", inst.shouldRender());
			dataInst.set("bar_index", inst.barIndex);
			dataInst.set("icon_index", inst.iconIndex);
			dataInst.set("sprite_location", inst.getSpriteLocation());
			dataInst.set("condition", inst.getCondition());
			dataInst.set("inverted", inst.isInverted());
			return dataInst;
		}
	);
}
