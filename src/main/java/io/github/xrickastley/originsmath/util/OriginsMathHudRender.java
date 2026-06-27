package io.github.xrickastley.originsmath.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class OriginsMathHudRender extends HudRender {
	public static final OriginsMathHudRender DONT_RENDER = new OriginsMathHudRender(false, ResourceBacked.fromNumber(0), ResourceBacked.fromNumber(0), Apoli.identifier("textures/gui/resource_bar.png"), null, false, ResourceBacked.fromNumber(0));

	private final List<OriginsMathHudRender> children = new LinkedList<>();

	private final ResourceBacked<Integer> barIndex;
	private final ResourceBacked<Integer> iconIndex;
	private ResourceBacked<Integer> order;

	public OriginsMathHudRender(boolean shouldRender, ResourceBacked<Integer> barIndex, ResourceBacked<Integer> iconIndex, Identifier spriteLocation, Predicate<Entity> condition, boolean inverted, ResourceBacked<Integer> order) {
		super(shouldRender, -1, -1, spriteLocation, condition, inverted, 0);

		this.barIndex = barIndex;
		this.iconIndex = iconIndex;
		this.order = order;
	}

	@Override
	public int compareTo(@NotNull HudRender other) {
		int orderResult = Integer.compare(this.getOrder(), other.getOrder());
		
		return orderResult != 0 
			? orderResult 
			: this.getSpriteLocation().compareTo(other.getSpriteLocation());
	}

	@Override
	public int getBarIndex() {
		return barIndex.intValue();
	}

	@Override
	public int getIconIndex() {
		return iconIndex.intValue();
	}

	@ApiStatus.Internal
	public void setTargetEntity(Entity entity) {
		this.barIndex.setTargetEntity(entity);
		this.iconIndex.setTargetEntity(entity);
	}

	@Override
	public int getOrder() {
		return order.intValue();
	}

	@Override
	public void setOrder(int order) {
		this.setOrder(ResourceBacked.fromNumber(order));
	}

	public void setOrder(ResourceBacked<Integer> order) {
		this.order = order;
	}

	public void addChild(OriginsMathHudRender hudRender) {
		if (this == hudRender) return;

		if (hudRender.getOrder() == 0) hudRender.setOrder(this.order);

		this.children.add(hudRender);
	}

	@Override
	public Optional<HudRender> getChildOrSelf(Entity viewer) {
		if (this.shouldRender(viewer))
			return Optional.of(this);

		return ClassInstanceUtil.castInstance(
			children
				.stream()
				.filter(hudRender -> hudRender.shouldRender(viewer))
				.findFirst()
		);
	}

	public void send(PacketByteBuf buffer) {
		OriginsMathHudRender.SINGLE_DATA_TYPE.send(buffer, this);
		OriginsMathHudRender.MULTIPLE_DATA_TYPE.send(buffer, this.children);
	}

	public static OriginsMathHudRender receive(PacketByteBuf buffer) {
		OriginsMathHudRender parentHudRender = OriginsMathHudRender.SINGLE_DATA_TYPE.receive(buffer);
		OriginsMathHudRender.MULTIPLE_DATA_TYPE
			.receive(buffer)
			.forEach(parentHudRender::addChild);

		return parentHudRender;
	}

	public static final SerializableDataType<OriginsMathHudRender> SINGLE_DATA_TYPE = SerializableDataType.compound(
		OriginsMathHudRender.class,
		new SerializableData()
			.add("should_render", SerializableDataTypes.BOOLEAN, true)
			.add("bar_index", ResourceBacked.DataTypes.RESOURCE_BACKED_INT, ResourceBacked.fromNumber(0))
			.add("icon_index", ResourceBacked.DataTypes.RESOURCE_BACKED_INT, ResourceBacked.fromNumber(0))
			.add("sprite_location", SerializableDataTypes.IDENTIFIER, Identifier.of("apoli", "textures/gui/resource_bar.png"))
			.add("condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("inverted", SerializableDataTypes.BOOLEAN, false)
			.add("order", ResourceBacked.DataTypes.RESOURCE_BACKED_INT, ResourceBacked.fromNumber(0)),
		dataInst -> new OriginsMathHudRender(
			dataInst.getBoolean("should_render"),
			dataInst.get("bar_index"),
			dataInst.get("icon_index"),
			dataInst.getId("sprite_location"),
			dataInst.get("condition"),
			dataInst.getBoolean("inverted"),
			dataInst.get("order")
		),
		(data, inst) -> {
			SerializableData.Instance dataInst = data.new Instance();
			dataInst.set("should_render", inst.shouldRender());
			dataInst.set("bar_index", inst.barIndex);
			dataInst.set("icon_index", inst.iconIndex);
			dataInst.set("sprite_location", inst.getSpriteLocation());
			dataInst.set("condition", inst.getCondition());
			dataInst.set("inverted", inst.isInverted());
			dataInst.set("order", inst.order);
			return dataInst;
		}
	);

	public static final SerializableDataType<List<OriginsMathHudRender>> MULTIPLE_DATA_TYPE = SerializableDataType.list(SINGLE_DATA_TYPE);

	/**
	 *  <p>A HUD render data type that accepts either a single HUD render or multiple HUD renders. The first HUD render will be considered
	 *  the <b>"parent"</b> and the following HUD renders will be considered its <b>"children."</b></p>
	 *
	 *  <p>If the children don't specify an order value, the order value of the parent will be inherited instead.</p>
	 */
	public static final SerializableDataType<OriginsMathHudRender> DATA_TYPE = new SerializableDataType<OriginsMathHudRender>(
		OriginsMathHudRender.class,
		(buf, hudRender) -> hudRender.send(buf),
		OriginsMathHudRender::receive,
		jsonElement -> {
			LinkedList<OriginsMathHudRender> hudRenders = (LinkedList<OriginsMathHudRender>) MULTIPLE_DATA_TYPE.read(jsonElement);
			if (hudRenders.isEmpty()) {
				return OriginsMathHudRender.DONT_RENDER;
			}

			OriginsMathHudRender parentHudRender = hudRenders.removeFirst();

			for (OriginsMathHudRender hudRender : hudRenders) parentHudRender.addChild(hudRender);

			return parentHudRender;
		},
		SINGLE_DATA_TYPE::write
	);
}
