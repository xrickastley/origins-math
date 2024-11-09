package io.github.xrickastley.originsmath.powers.apoli;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.ResourceBacked;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;

public class ToggleNightVisionPower extends NightVisionPower implements Active {
	private boolean isActive;
	private Key key;

	public ToggleNightVisionPower(PowerType<?> type, LivingEntity entity) {
		this(type, entity, ResourceBacked.fromNumber(1.0F), true);
	}

	public ToggleNightVisionPower(PowerType<?> type, LivingEntity entity, ResourceBacked<Float> strength, boolean activeByDefault) {
		super(type, entity, strength);

		this.isActive = activeByDefault;
	}

	@Override
	public void onUse() {
		this.isActive = !this.isActive;
		PowerHolderComponent.syncPower(entity, this.type);
	}

	public boolean isActive() {
		return this.isActive && super.isActive();
	}

	@Override
	public NbtElement toTag() {
		return NbtByte.of(isActive);
	}

	@Override
	public void fromTag(NbtElement tag) {
		if(tag instanceof NbtByte) {
			isActive = ((NbtByte)tag).byteValue() > 0;
		}
	}

	@Override
	public Key getKey() {
		return key;
	}

	@Override
	public void setKey(Key key) {
		this.key = key;
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(OriginsMath.identifier("toggle_night_vision"),
			new SerializableData()
				.add("active_by_default", SerializableDataTypes.BOOLEAN, false)
				.add("strength", SerializableDataTypes.FLOAT, 1.0F)
				.add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key()),
			data -> (type, entity) -> {
				ToggleNightVisionPower power = new ToggleNightVisionPower(
					type, 
					entity, 
					data.get("strength"), 
					data.getBoolean("active_by_default")
				);
				
				power.setKey((Active.Key)data.get("key"));
				
				return power;
			}
		).allowCondition();
	}
}