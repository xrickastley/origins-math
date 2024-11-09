package io.github.xrickastley.originsmath.powers.apoli;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.ClassDataRegistry;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.ResourceBacked;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.List;

public class AttributeModifyTransferPower extends Power {

	private final Class<?> modifyClass;
	private final EntityAttribute attribute;
	private final ResourceBacked<Double> valueMultiplier;

	public AttributeModifyTransferPower(PowerType<?> type, LivingEntity entity, Class<?> modifyClass, EntityAttribute attribute, ResourceBacked<Double> valueMultiplier) {
		super(type, entity);
		this.modifyClass = modifyClass;
		this.attribute = attribute;
		this.valueMultiplier = valueMultiplier;
	}

	public boolean doesApply(Class<?> cls) {
		return cls.equals(modifyClass);
	}

	public void addModifiers(List<Modifier> modifiers) {
		AttributeContainer attrContainer = entity.getAttributes();
		
		if (attrContainer.hasAttribute(attribute)) return;
		
		attrContainer
			.getCustomInstance(attribute)
			.getModifiers()
			.forEach(mod -> {
				EntityAttributeModifier transferMod = new EntityAttributeModifier(
					mod.getName(), 
					mod.getValue() * valueMultiplier.doubleValue(), 
					mod.getOperation()
				);
				
				modifiers.add(ModifierUtil.fromAttributeModifier(transferMod));
			});
	}

	public void apply(List<EntityAttributeModifier> modifiers) {
		AttributeContainer attrContainer = entity.getAttributes();

		if (!attrContainer.hasAttribute(attribute)) return;

		attrContainer
			.getCustomInstance(attribute)
			.getModifiers()
			.forEach(mod -> {
				EntityAttributeModifier transferMod = new EntityAttributeModifier(
					mod.getName(), 
					mod.getValue() * valueMultiplier.doubleValue(), 
					mod.getOperation()
				);
			
				modifiers.add(transferMod);
			});
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(OriginsMath.identifier("attribute_modify_transfer"),
			new SerializableData()
				.add("class", ClassDataRegistry.get(Power.class).get().getDataType())
				.add("attribute", SerializableDataTypes.ATTRIBUTE)
				.add("multiplier", ResourceBacked.DataTypes.RESOURCE_BACKED_DOUBLE, ResourceBacked.fromNumber(1.0)),
			data -> (type, player) -> new AttributeModifyTransferPower(
				type, 
				player, 
				data.get("class"), 
				data.get("attribute"), 
				data.get("multiplier")
			)
		).allowCondition();
	}
}
