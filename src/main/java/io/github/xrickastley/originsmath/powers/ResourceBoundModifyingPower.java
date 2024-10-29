package io.github.xrickastley.originsmath.powers;

import java.util.List;

import org.apache.commons.lang3.function.TriFunction;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

/**
 * Represents a power than can modify the "bounds" of a modifiable resource.
 */
public abstract class ResourceBoundModifyingPower extends ValueModifyingPower {
	protected final PowerType<?> resource;

	public ResourceBoundModifyingPower(PowerType<?> type, LivingEntity entity, PowerType<?> resource) {
		super(type, entity);

		this.resource = resource;
	}

	public PowerType<?> getResource() {
		return this.resource;
	}

	public boolean appliesToResource(PowerType<?> powerType) {
		return this.resource.getIdentifier().equals(powerType.getIdentifier());
	}

	public boolean appliesToResource(Identifier id) {
		return this.resource.getIdentifier().equals(id);
	}

	public static <T extends ResourceBoundModifyingPower> int applyModifiers(Entity entity, Class<T> powerClass, int baseValue, PowerType<?> resource) {
		return (int) PowerHolderComponent.modify(
            entity, 
            powerClass, 
            baseValue,
            power -> power.appliesToResource(resource)
        );
	}

	public static PowerFactory<?> createResourceModifyingFactory(Identifier id, TriFunction<PowerType<?>, LivingEntity, PowerType<?>, ResourceBoundModifyingPower> powerConstructor) {
		return new PowerFactory<>(
			id,
			new SerializableData()
				.add("resource", ApoliDataTypes.POWER_TYPE)
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null),
			data -> (type, player) -> {
				PowerType<?> power2 = data.get("resource");

				ResourceBoundModifyingPower power = powerConstructor.apply(type, player, power2);

				data.ifPresent("modifier", power::addModifier);
				data.<List<Modifier>>ifPresent("modifiers", mods -> mods.forEach(power::addModifier));
				
				return power;
			}
		).allowCondition();
	}
}
