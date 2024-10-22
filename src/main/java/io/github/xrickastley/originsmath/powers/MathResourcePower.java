package io.github.xrickastley.originsmath.powers;

import org.mariuszgromada.math.mxparser.Expression;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.VariableSerializer;

import net.minecraft.entity.LivingEntity;

public class MathResourcePower extends LinkedVariableIntPower {
	private final Expression expression;
	private final VariableSerializer variables;

	private MathResourcePower(PowerType<?> type, LivingEntity entity, Expression expression, VariableSerializer variables) {
		super(type, entity);
		
		this.expression = expression;
		this.variables = variables;
	}

	@Override
	protected int supplyValue() {
		return (int) supplyDoubleValue();
	}

	@Override
	public double supplyDoubleValue() {
		return new Expression(expression.getExpressionString(), variables.getArgumentArray(this.entity, false))
			.calculate();
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("math_resource"),
			new SerializableData()
				.add("expression", MathResourcePower.EXPRESSION)
				.add("variables", VariableSerializer.SERIALIZABLE_DATATYPE, VariableSerializer.EMPTY),
			data -> (powerType, livingEntity) -> new MathResourcePower(powerType, livingEntity, data.get("expression"), data.get("variables"))
		);
	}

	public static final SerializableDataType<Expression> EXPRESSION = SerializableDataType.wrap(
		Expression.class,
		SerializableDataTypes.STRING,
		Expression::getExpressionString,
		Expression::new
	);
}
