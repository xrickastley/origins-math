package io.github.xrickastley.originsmath.factories;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Triple;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.actions.meta.ForRangeAction;
import io.github.xrickastley.originsmath.actions.meta.WhileAction;
import io.github.xrickastley.originsmath.util.ClassInstanceUtil;
import io.github.xrickastley.originsmath.util.ResourceBackedInjector;

import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class OriginsMathMetaActions {
	private static final List<MetaActionContext<?, ?>> META_ACTION_CONTEXTS;

	public static void register() {
		register(ForRangeAction::getFactory);
		register(WhileAction::getFactory);

		OriginsMath
			.sublogger(OriginsMathPowers.class)
			.info("Registered all base meta actions!");
	}

	private static void register(Function<MetaActionContext<?, ?>, ActionFactory<?>> factorySupplier) {
		for (final MetaActionContext<?, ?> context : OriginsMathMetaActions.META_ACTION_CONTEXTS)
			context.register(ClassInstanceUtil.castInstance(factorySupplier));
	}

	public static class MetaActionContext<A, C> {
		private final SerializableDataType<ActionFactory<A>.Instance> actionDataType;
		private final SerializableDataType<ConditionFactory<C>.Instance> conditionDataType;
		private final SerializableDataType<List<ActionFactory<A>.Instance>> listActionDataType;
		private final SerializableDataType<List<ConditionFactory<C>.Instance>> listConditionDataType;
		private final Function<A, C> actionToConditionTypeFunction;
		private final Registry<ActionFactory<A>> registry;

		@SuppressWarnings("unchecked")
		MetaActionContext(
			SerializableDataType<ActionFactory<A>.Instance> actionDataType,
			SerializableDataType<ConditionFactory<C>.Instance> conditionDataType,
			SerializableDataType<List<ActionFactory<A>.Instance>> listActionDataType,
			SerializableDataType<List<ConditionFactory<C>.Instance>> listConditionDataType,
			Registry<ActionFactory<A>> registry
		) {
			this(actionDataType, conditionDataType, listActionDataType, listConditionDataType, registry, t -> (C) t);
		}

		MetaActionContext(
			SerializableDataType<ActionFactory<A>.Instance> actionDataType,
			SerializableDataType<ConditionFactory<C>.Instance> conditionDataType,
			SerializableDataType<List<ActionFactory<A>.Instance>> listActionDataType,
			SerializableDataType<List<ConditionFactory<C>.Instance>> listConditionDataType,
			Registry<ActionFactory<A>> registry,
			Function<A, C> actionToConditionTypeFunction
		) {
			this.actionDataType = actionDataType;
			this.conditionDataType = conditionDataType;
			this.listActionDataType = listActionDataType;
			this.listConditionDataType = listConditionDataType;
			this.actionToConditionTypeFunction = actionToConditionTypeFunction;
			this.registry = registry;
		}

		public SerializableDataType<ActionFactory<A>.Instance> getActionDataType() {
			return this.actionDataType;
		} 

		public SerializableDataType<ConditionFactory<C>.Instance> getConditionDataType() {
			return this.conditionDataType;
		}
		
		public SerializableDataType<List<ActionFactory<A>.Instance>> getListActionDataType() {
			return this.listActionDataType;
		}

		public SerializableDataType<List<ConditionFactory<C>.Instance>> getListConditionDataType() {
			return this.listConditionDataType;
		}
		
		public Function<A, C> getActionToConditionTypeFunction() {
			return this.actionToConditionTypeFunction;
		}

		public C getAsCondition(final A actionTypeInstance) {
			return this.actionToConditionTypeFunction.apply(actionTypeInstance);
		}

		private ActionFactory<A> register(Function<MetaActionContext<A, C>, ActionFactory<A>> factorySupplier) {
			final ActionFactory<A> actionFactory = ResourceBackedInjector.applyPossibleFactoryInjection(this.registry, factorySupplier.apply(this));

			return Registry.register(this.registry, actionFactory.getSerializerId(), actionFactory);
		}
	}

	static {
		META_ACTION_CONTEXTS = List.of(
			new MetaActionContext<Pair<Entity, Entity>, Pair<Entity, Entity>>(
				ApoliDataTypes.BIENTITY_ACTION, 
				ApoliDataTypes.BIENTITY_CONDITION, 
				ApoliDataTypes.BIENTITY_ACTIONS, 
				ApoliDataTypes.BIENTITY_CONDITIONS, 
				ApoliRegistries.BIENTITY_ACTION
			),
			new MetaActionContext<Triple<World, BlockPos, Direction>, CachedBlockPosition>(
				ApoliDataTypes.BLOCK_ACTION, 
				ApoliDataTypes.BLOCK_CONDITION, 
				ApoliDataTypes.BLOCK_ACTIONS, 
				ApoliDataTypes.BLOCK_CONDITIONS, 
				ApoliRegistries.BLOCK_ACTION,
				t -> new CachedBlockPosition(t.getLeft(), t.getMiddle(), true)
			),
			new MetaActionContext<Entity, Entity>(
				ApoliDataTypes.ENTITY_ACTION, 
				ApoliDataTypes.ENTITY_CONDITION, 
				ApoliDataTypes.ENTITY_ACTIONS, 
				ApoliDataTypes.ENTITY_CONDITIONS, 
				ApoliRegistries.ENTITY_ACTION
			),
			new MetaActionContext<Pair<World, ItemStack>, ItemStack>(
				ApoliDataTypes.ITEM_ACTION, 
				ApoliDataTypes.ITEM_CONDITION, 
				ApoliDataTypes.ITEM_ACTIONS, 
				ApoliDataTypes.ITEM_CONDITIONS, 
				ApoliRegistries.ITEM_ACTION,
				Pair::getRight
			)
		);
	}
}
