package io.github.xrickastley.originsmath.factories;

import com.mojang.brigadier.CommandDispatcher;

import io.github.apace100.apoli.command.ResourceCommand;
import io.github.xrickastley.originsmath.commands.ModifierCommand;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;

public class OriginsMathCommands implements CommandRegistrationCallback {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
		ModifierCommand.register(dispatcher);
		ResourceCommand.register(dispatcher);
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register(new OriginsMathCommands());
	}
}
