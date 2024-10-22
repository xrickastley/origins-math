package io.github.xrickastley.originsmath.actions.entity;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.VariableSerializer;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class VariableExecuteCommandAction {
	private static void action(SerializableData.Instance data, Entity entity) {
        final MinecraftServer server = entity.getWorld().getServer();

		if (server == null) return;
		
        final boolean validOutput = !(entity instanceof ServerPlayerEntity) || ((ServerPlayerEntity) entity).networkHandler != null;
		final VariableSerializer varSerializer = data.get("variables");
        final ServerCommandSource source = new ServerCommandSource(
            Apoli.config.executeCommand.showOutput && validOutput 
				? entity 
				: CommandOutput.DUMMY,
            entity.getPos(),
            entity.getRotationClient(),
            entity.getWorld() instanceof ServerWorld 
				? (ServerWorld) entity.getWorld() 
				: null,
            Apoli.config.executeCommand.permissionLevel,
            entity.getName().getString(),
            entity.getDisplayName(),
            entity.getWorld().getServer(),
            entity
		);

		String commandString = data.getString("command");

		for (String variable : varSerializer.getVariableMap().keySet()) {
			int value = varSerializer.getVariableValue(variable, entity);
			
			commandString = commandString
				.replace(String.format("$:%s", variable), String.valueOf(value))
				.replace(String.format("${%s}", variable), String.valueOf(value));
		}

		server
			.getCommandManager()
			.executeWithPrefix(source, commandString);
	}

	public static ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsMath.identifier("variable_execute_command"),
			new SerializableData()
	            .add("command", SerializableDataTypes.STRING)
				.add("variables", VariableSerializer.SERIALIZABLE_DATATYPE, VariableSerializer.EMPTY),
			VariableExecuteCommandAction::action
		);
	}
}
