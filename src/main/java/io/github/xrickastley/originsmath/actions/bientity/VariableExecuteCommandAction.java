package io.github.xrickastley.originsmath.actions.bientity;

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
import net.minecraft.util.Pair;

public class VariableExecuteCommandAction {
	private static void action(SerializableData.Instance data, Pair<Entity, Entity> entity) {
		final Entity actor = entity.getLeft();
		final Entity target = entity.getRight();
		final MinecraftServer server = actor.getWorld().getServer();

		if (server == null) return;
		
		final boolean validOutput = !(target instanceof final ServerPlayerEntity serverTarget) || serverTarget.networkHandler != null;
		final ServerCommandSource source = new ServerCommandSource(
			Apoli.config.executeCommand.showOutput && validOutput 
				? actor 
				: CommandOutput.DUMMY,
			actor.getPos(),
			actor.getRotationClient(),
			actor.getWorld() instanceof final ServerWorld serverWorld
				? serverWorld
				: null,
			Apoli.config.executeCommand.permissionLevel,
			actor.getName().getString(),
			actor.getDisplayName(),
			server,
			actor
		);

		final VariableSerializer varSerializer = data.get("variables");
		final String actorSelector = data.getString("actor_selector");
		final String targetSelector = data.getString("target_selector");
		
		String commandString = data.getString("command")
			.replace(actorSelector, actor.getUuid().toString())
			.replace(targetSelector, target.getUuid().toString());

		for (String variable : varSerializer.getVariableMap().keySet()) {
			int value = varSerializer.getVariableValue(variable, actor);
			
			commandString = commandString
				.replace(String.format("$:%s", variable), String.valueOf(value))
				.replace(String.format("${%s}", variable), String.valueOf(value));
		}

		server
			.getCommandManager()
			.executeWithPrefix(source, commandString);
	}

	public static ActionFactory<Pair<Entity, Entity>> getFactory() {
		return new ActionFactory<>(
			OriginsMath.identifier("variable_execute_command"),
			new SerializableData()
				.add("command", SerializableDataTypes.STRING)
				.add("actor_selector", SerializableDataTypes.STRING, "%a")
				.add("target_selector", SerializableDataTypes.STRING, "%t")
				.add("variables", VariableSerializer.SERIALIZABLE_DATATYPE, VariableSerializer.EMPTY),
			VariableExecuteCommandAction::action
		);
	}
}
