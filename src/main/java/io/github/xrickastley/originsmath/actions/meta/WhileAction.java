package io.github.xrickastley.originsmath.actions.meta;

import java.util.function.Function;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.factories.OriginsMathMetaActions.MetaActionContext;

public class WhileAction {
	private static final int MAX_REPETITIONS = 100_000;

	private static <T, U> void action(SerializableData.Instance data, T t, Function<T, U> actionToConditionTypeFunction) {
		final ConditionFactory<U>.Instance condition = data.get("condition");
		final ActionFactory<T>.Instance action = data.get("action");
		final U u = actionToConditionTypeFunction.apply(t);
		
		int repetitions = 0;
		while (condition.test(u)) 
			if (repetitions++ < WhileAction.MAX_REPETITIONS) action.accept(t);
			else throw new UnterminatedWhileLoopException();
	}

	public static <T, U> ActionFactory<T> getFactory(MetaActionContext<T, U> actionContext) {
		return new ActionFactory<T>(OriginsMath.identifier("while"),
			new SerializableData()
				.add("condition", actionContext.getConditionDataType())
				.add("action", actionContext.getActionDataType()),
			(inst, t) -> action(inst, t, actionContext.getActionToConditionTypeFunction())
		);
	}

	private static class UnterminatedWhileLoopException extends RuntimeException {
		UnterminatedWhileLoopException() {
			super("An \"origins-math:while\" loop was not terminated within " + MAX_REPETITIONS + " loops!");
		}
	}
}
