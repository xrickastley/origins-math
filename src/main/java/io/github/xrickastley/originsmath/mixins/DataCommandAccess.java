package io.github.xrickastley.originsmath.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.command.DataCommandObject;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.DataCommand;

@Mixin(DataCommand.class)
public interface DataCommandAccess {
	@Invoker("getNbt")
	public static NbtElement invokeGetNbt(final NbtPathArgumentType.NbtPath path, final DataCommandObject object) {
		throw new AssertionError();
	}
}
