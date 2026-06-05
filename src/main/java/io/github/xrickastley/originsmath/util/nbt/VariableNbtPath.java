package io.github.xrickastley.originsmath.util.nbt;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.regex.Pattern;

import io.github.xrickastley.originsmath.util.VariableSerializer;
import io.github.xrickastley.originsmath.util.VariableStringUtil;

import net.minecraft.command.argument.NbtPathArgumentType.NbtPath;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.entity.Entity;

public class VariableNbtPath {
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$:.+?(?= |$)|\\$\\{.+\\}");
	
	private final String path;

	public VariableNbtPath(final String path) {
		final StringReader reader = new StringReader(VARIABLE_PATTERN.matcher(path).replaceAll("0"));

		try {
			NbtPathArgumentType.nbtPath().parse(reader);
		} catch (CommandSyntaxException e) {
			throw new InvalidNbtPathException(path, e.getMessage());
		}

		this.path = path;		
	}

	public NbtPath getPathFor(final VariableSerializer varSerializer, final Entity entity) {
		final String nbtPath = VariableStringUtil.parse(path, varSerializer, entity);
		final StringReader reader = new StringReader(nbtPath);

		try {
			return NbtPathArgumentType.nbtPath().parse(reader);
		} catch (CommandSyntaxException e) {
			throw new InvalidNbtPathException(nbtPath, e.getMessage());
		}
	}

	@Override
	public String toString() {
		return this.path;
	}
}
