package io.github.xrickastley.originsmath.util.nbt;

import net.minecraft.command.argument.NbtPathArgumentType.NbtPath;

public class InvalidNbtPathException extends RuntimeException {
	private final String path;

	public InvalidNbtPathException(final NbtPath path, final String message) {
		this(path.toString(), message);
	}

	public InvalidNbtPathException(final String path, final String message) {
		super(message);

		this.path = path;
	}

	@Override
	public String getMessage() {
		String message = super.getMessage();
		
		message = message.substring(0, 1).toLowerCase() + message.substring(1);

		return String.format("For NBT path \"%s\", %s", path, message);
	}
}