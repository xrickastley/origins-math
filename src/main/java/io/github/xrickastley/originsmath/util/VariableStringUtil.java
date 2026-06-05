package io.github.xrickastley.originsmath.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.Entity;

public final class VariableStringUtil {
	private static final Pattern JAVASCRIPT_INTERPOLATION_PATTERN = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)(:%(?:d|\\.([0-9])f)(?!.))?");

	public static String parse(String variableString, VariableSerializer variableSerializer, Entity valueHolder) {
		try {
			final StringBuffer result = new StringBuffer();
			final StringReader reader = new StringReader(variableString);

			while (reader.canRead()) {
				result.append(readStringUntilOrEnd(reader, '$'));
				
				if (reader.canRead()) result.append(
					reader.peek() == ':'
						? parseOriginsMathInterpolation(reader, variableSerializer, valueHolder)
						: parseJavaScriptInterpolation(reader, variableSerializer, valueHolder)
				);
			}

			return result.toString();
		} catch (CommandSyntaxException e) {
			throw new VariableStringParseException(variableString, e);
		}
	}

	private static String readStringUntilOrEnd(StringReader reader, char terminator) throws CommandSyntaxException {
		final int cursor = reader.getCursor();

		try {
			return reader.readStringUntil(terminator);
		} catch (CommandSyntaxException e) {
			if (e.getMessage().startsWith("Unclosed quoted string")) 
				return reader.getString().substring(cursor);
			else throw e;
		}
	}

	private static String readStringWhileMatches(StringReader reader, Pattern pattern) throws CommandSyntaxException {
		final StringBuffer result = new StringBuffer();
		
		while (pattern.matcher(result.toString() + reader.peek()).matches())
			result.append(reader.read());

		return result.toString();
	}

	private static String parseOriginsMathInterpolation(StringReader reader, VariableSerializer variableSerializer, Entity valueHolder) throws CommandSyntaxException {
		reader.expect(':');

		final String variable = readStringWhileMatches(reader, VariableSerializer.VARIABLE_REGEX);

		VariableStringUtil.validateVariable(variable, reader, variableSerializer);

		return String.valueOf(variableSerializer.getVariableValue(variable, valueHolder));
	}

	private static String parseJavaScriptInterpolation(StringReader reader, VariableSerializer variableSerializer, Entity valueHolder) throws CommandSyntaxException {
		reader.expect('{');

		final String interpolation = reader.readStringUntil('}');
		final Matcher matcher = VariableStringUtil.JAVASCRIPT_INTERPOLATION_PATTERN.matcher(interpolation);

		if (!matcher.find() || !matcher.matches())
			throw new VariableStringParseException("Invalid variable interpolation syntax: " + interpolation);

		final String variable = matcher.group(1);

		VariableStringUtil.validateVariable(variable, reader, variableSerializer);

		if (matcher.group(3) != null) {
			final int precision = Integer.parseInt(matcher.group(3));

			if (precision < 0 || precision > 17)
				throw new VariableStringParseException("The precision for a floating-point must be in the range of [0-17]!");
		}

		return matcher.group(3) != null
			? String.format("%." + matcher.group(3) + "f", variableSerializer.getVariable(variable, valueHolder).getArgumentValue())
			: String.valueOf(variableSerializer.getVariableValue(variable, valueHolder));
	}

	private static void validateVariable(String variableName, StringReader reader, VariableSerializer variableSerializer) {
		if (variableName.isEmpty()) {
			final String varString = reader.getString().substring(reader.getCursor() - 5);

			throw new VariableStringParseException("No variable can be determined at point: " + varString.substring(0, 5) + "<here>" + varString.substring(5));
		}

		if (!VariableSerializer.isValidVariableName(variableName)) 
			throw new VariableStringParseException("Invalid variable name: " + variableName);

		if (!variableSerializer.hasVariable(variableName))
			throw new VariableStringParseException("Non-existent variable: " + variableName + "! If this is not intended as a variable, prepend the \"$\" with \\");
	}

	private static class VariableStringParseException extends RuntimeException {
		VariableStringParseException(final String variableString, final CommandSyntaxException e) {
			super("An error occured while parsing the variable string: \"" + variableString +"\"");

			this.addSuppressed(e);
		}

		VariableStringParseException(final String message) {
			super(message);
		}
	}
}
