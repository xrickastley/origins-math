package io.github.xrickastley.originsmath.actions.meta;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.factories.OriginsMathMetaActions.MetaActionContext;
import io.github.xrickastley.originsmath.util.ResourceBacked;

public class ForRangeAction {
	private static <T, U> void action(SerializableData.Instance data, T t) {
		final ActionFactory<T>.Instance action = data.get("action");
		final ResourceBacked<?> range = data.get("range");
		
		for (int i = 0; i < range.intValue(); i++) action.accept(t);
	}

	public static <T, U> ActionFactory<T> getFactory(MetaActionContext<T, U> actionContext) {
		return new ActionFactory<T>(OriginsMath.identifier("for_range"),
			new SerializableData()
				.add("range", ResourceBacked.DataTypes.RESOURCE_BACKED_INT)
				.add("action", actionContext.getActionDataType()),
			(inst, t) -> action(inst, t)
		);
	}
}
