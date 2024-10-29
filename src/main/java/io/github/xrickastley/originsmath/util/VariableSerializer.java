package io.github.xrickastley.originsmath.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.mariuszgromada.math.mxparser.Argument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.xrickastley.originsmath.powers.LinkedVariableIntPower;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

public class VariableSerializer {
	public static final VariableSerializer EMPTY = new VariableSerializer();
	public static final SerializableDataType<VariableSerializer> SERIALIZABLE_DATATYPE = new SerializableDataType<VariableSerializer>(
		VariableSerializer.class,
		VariableSerializer::send,
		VariableSerializer::recieve,
		VariableSerializer::read,
		VariableSerializer::write
	);

	private final HashMap<String, PowerType<?>> variableMap = new HashMap<>();
	
	private static void send(PacketByteBuf packet, VariableSerializer serializer) {
		packet.writeInt(serializer.variableMap.size());
		
		serializer.variableMap.forEach((key, value) -> {
			packet.writeString(key);
			ApoliDataTypes.POWER_TYPE.send(packet, value);
		});
	}
	
	private static VariableSerializer recieve(PacketByteBuf packet) {
		final VariableSerializer serializer = new VariableSerializer();
		final int size = packet.readInt();

		for (int i = 0; i < size; i++) {
			serializer.variableMap.put(
				packet.readString(),
				ApoliDataTypes.POWER_TYPE.receive(packet)
			);
		}

		return serializer;
	}

	private static VariableSerializer read(JsonElement json) {
		final VariableSerializer serializer = new VariableSerializer();

		if (!(json instanceof final JsonObject jsonObject)) throw new JsonParseException("Expected a JSON Object for argument serializer!");

		for (final Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			serializer.variableMap.put(
				entry.getKey(), 
				ApoliDataTypes.POWER_TYPE.read(entry.getValue())
			);
		}

		return serializer;
	}

	private static JsonObject write(final VariableSerializer serializer) {
		final JsonObject json = new JsonObject();

		for (final Map.Entry<String, PowerType<?>> entry : serializer.variableMap.entrySet()) {
			json.add(
				entry.getKey(), 
				ApoliDataTypes.POWER_TYPE.write(((PowerTypeReference<?>) entry.getValue()))
			);
		}

		return json;
	}
	
	private VariableSerializer() {}
	
	/**
	 * Checks if a variable is declared in this {@code VariableSerializer}. 
	 * @param variable The variable name to check for in this {@code VariableSerializer}. 
	 * @return A {@code boolean} representing whether or not the value is declared in this {@code VariableSerializer}.
	 */
	public boolean hasVariable(String variable) {
		return variableMap.containsKey(variable);
	}

	/**
	 * Gets a declared variable from this {@code VariableSerializer}. 
	 * @param variable The variable name of the power in this {@code VariableSerializer}. 
	 * @param entity The entity to use in getting the power's "value".
	 * @return An {@code Argument} with {@code variable} as the {@code argumentDefinitionString} with it's value being the power value of {@code entity}.
	 */
	public Argument getVariable(String variable, Entity entity) {
		if (!variableMap.containsKey(variable)) throw new RuntimeException(String.format("Attempted to find invalid variable: \"%s\"!", variable));

		final PowerType<?> powerType = variableMap.get(variable);
		final Power power = powerType.get(entity);
		final Argument argument = new Argument(variable);

		if (power == null) throw new RuntimeException(String.format("Attempted to serialize invalid power: \"%s\" as variable!", powerType.getIdentifier().toString()));

		if (power instanceof final LinkedVariableIntPower lvip) {
			argument.setArgumentValue(lvip.supplyDoubleValue());
		} else if (power instanceof final VariableIntPower vip) {
			argument.setArgumentValue(vip.getValue());
		} else if (power instanceof final CooldownPower cp) {
			argument.setArgumentValue(cp.getRemainingTicks());
		} else throw new RuntimeException(String.format("Attempted to use invalid power type \"%s\" as a variable!", power.getType().getIdentifier().toString()));
	
		return argument;
	}

	/**
	 * Gets the value of a declared variable from this {@code VariableSerializer} as an {@code int}. 
	 * @param variable The variable name of the power in this {@code VariableSerializer}. 
	 * @param entity The entity to use in getting the power's "value".
	 * @return The power value of {@code entity}, as an {@code int}.
	 */
	public int getVariableValue(String variable, Entity entity) {
		return (int) getVariable(variable, entity).getArgumentValue();
	}

	/**
	 * Adds a {@code PowerType} with it's value serving as the value for {@code variable}. 
	 * @param variable The variable name of the power in this {@code VariableSerializer}. 
	 * @param powerType The {@code PowerType} serving as the value for {@code variable}.
	 * @return This object.
	 */
	public VariableSerializer addVariable(String variable, PowerType<?> powerType) {
		this.variableMap.put(variable, powerType);

		return this;
	}

	/**
	 * Gets the variable map used in this {@code VariableSerializer}.
	 * Note that the returned variable map is a <i>copy</i>, meaning that any changes
	 * made to it will not apply to the current variable map, and that any changes made
	 * after the copy was created will also not apply.
	 * 
	 * @return The variable map used for this {@code VariableSerializer}.
	 */
	public HashMap<String, PowerType<?>> getVariableMap() {
		HashMap<String, PowerType<?>> copyMap = new HashMap<>();
		copyMap.putAll(variableMap);

		return copyMap;
	}


	/**
	 * Parses all variables and values in the {@code variableMap} for {@code entity} and returns them as an array of {@code Argument}.
	 * @param entity The entity to use in getting all the power values.
	 * @param strict Whether or not strict mode is enabled. Strict mode will throw an error when a variable cannot be parsed. If strict mode is disabled, variables that cannot be parsed will have a value of {@code 0}.
	 * @return An array of {@code Argument} with variables and values from {@code variableMap}.
	 */
	public Argument[] getArgumentArray(Entity entity) {
		return getArgumentArray(entity, true);
	}

	/**
	 * Parses all variables and values in the {@code variableMap} for {@code entity} and returns them as an array of {@code Argument}.
	 * @param entity The entity to use in getting all the power values.
	 * @param strict Whether or not strict mode is enabled. Strict mode will throw an error when a variable cannot be parsed. If strict mode is disabled, variables that cannot be parsed will have a value of {@code 0}.
	 * @return An array of {@code Argument} with variables and values from {@code variableMap}.
	 */
	public Argument[] getArgumentArray(Entity entity, boolean strict) {
		final ArrayList<Argument> arguments = new ArrayList<>();

		variableMap.forEach((variable, powerType) -> {
			Argument argument;
			
			try {
				argument = getVariable(variable, entity);
			} catch (Exception e) {
				if (strict) throw e;

				argument = new Argument(variable, 0);
			}

			arguments.add(argument);
		});

		Argument[] argumentArray = new Argument[arguments.size()];

		arguments.toArray(argumentArray);

		return argumentArray;
	}
}
