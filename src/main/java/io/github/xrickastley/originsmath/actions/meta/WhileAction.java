package io.github.xrickastley.originsmath.actions.meta;

import java.util.function.Function;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.factories.OriginsMathMetaActions.MetaActionContext;

public class WhileAction {
	private static <T, U> void action(SerializableData.Instance data, T t, Function<T, U> actionToConditionTypeFunction) {
		final ConditionFactory<U>.Instance condition = data.get("condition");
		final ActionFactory<T>.Instance action = data.get("action");
		final U u = actionToConditionTypeFunction.apply(t);
		
		while (condition.test(u)) action.accept(t);
	}

	public static <T, U> ActionFactory<T> getFactory(MetaActionContext<T, U> actionContext) {
		return new ActionFactory<T>(OriginsMath.identifier("while"),
			new SerializableData()
				.add("condition", actionContext.getConditionDataType())
				.add("action", actionContext.getActionDataType()),
			(inst, t) -> action(inst, t, actionContext.getActionToConditionTypeFunction())
		);
	}
}
