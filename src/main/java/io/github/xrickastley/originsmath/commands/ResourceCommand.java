package io.github.xrickastley.originsmath.commands;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Optional;

import io.github.apace100.apoli.command.PowerTypeArgumentType;
import io.github.apace100.apoli.command.ResourceCommand.SubCommand;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.xrickastley.originsmath.util.ValueProviders;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ResourceCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager
				.literal("resource")
				.requires(cs -> cs.hasPermissionLevel(2))
				.then(
					CommandManager
						.literal("get")
						.then(
							CommandManager
								.literal("absolute")
								.then(
									CommandManager
										.argument("target", EntityArgumentType.entity())
										.then(
											CommandManager
											   .argument("power", PowerTypeArgumentType.power())
											   .executes(command -> resource(command, ClassTinkerers.getEnum(SubCommand.class, "GET_ABSOLUTE")))
										)
								)
						)
				)
		);
	}

	private static int resource(CommandContext<ServerCommandSource> context, SubCommand subCommand) throws CommandSyntaxException {
		final SubCommand GetAbsolute = ClassTinkerers.getEnum(SubCommand.class, "GET_ABSOLUTE");

		if (subCommand != GetAbsolute) return 0;
		
		final Entity entity = EntityArgumentType.getEntity(context, "target");

		final ServerCommandSource source = context.getSource();
		final PowerType<?> powerType = PowerTypeArgumentType.getPower(context, "power");
		final Optional<PowerHolderComponent> phc = PowerHolderComponent.KEY.maybeGet(entity);
		
		if (phc.isEmpty()) {
			source.sendError(Text.translatable("commands.apoli.resource.invalid_entity"));
			
			return 0;
		}

		final Power power = PowerHolderComponent.KEY.get(entity).getPower(powerType);
		final double value = ValueProviders.getValue(power).doubleValue();

		source.sendFeedback(() -> Text.translatable("commands.scoreboard.players.get.success", entity.getName().getString(), value, powerType.getIdentifier()), true);

		return (int) value;
	}
}