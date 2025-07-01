package io.github.xrickastley.originsmath.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.apace100.apoli.command.PowerHolderArgumentType;
import io.github.apace100.apoli.command.PowerTypeArgumentType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.xrickastley.originsmath.util.ValueProviders.ValueModifier;
import io.github.xrickastley.originsmath.util.ValueProviders.ValueProvider;
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
											   .executes(ResourceCommand::absoluteResource)
										)
								)
						)
				)
				.then(
					CommandManager
						.literal("change")
						.then(
							CommandManager
								.literal("absolute")
								.then(
									CommandManager
										.argument("target", PowerHolderArgumentType.holder())
										.then(
											CommandManager
												.argument("power", PowerTypeArgumentType.power())
												.then(
													CommandManager
														.argument("value", DoubleArgumentType.doubleArg())
														.executes(ResourceCommand::absoluteChange)
												)
										)
								)
						)
				)
		);
	}

	private static int absoluteResource(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final Entity entity = EntityArgumentType.getEntity(context, "target");

		final ServerCommandSource source = context.getSource();
		final PowerType<?> powerType = PowerTypeArgumentType.getPower(context, "power");
		final PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);

		final Power power = component.getPower(powerType);
		final double value = ValueProviders.getValueOr(power, 0).doubleValue();

		source.sendFeedback(() -> Text.translatable("commands.scoreboard.players.get.success", entity.getName().getString(), value, powerType.getIdentifier()), true);

		return (int) value;
	}

	private static int absoluteChange(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final Entity entity = EntityArgumentType.getEntity(context, "target");

		final ServerCommandSource source = context.getSource();
		final double value = DoubleArgumentType.getDouble(context, "value");
		final PowerType<?> powerType = PowerTypeArgumentType.getPower(context, "power");
		final PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);

		final Power power = component.getPower(powerType);
		final ValueProvider<Power> provider = ValueProviders.getProvider(powerType, entity);
		final ValueModifier<Power> modifier = ValueProviders.getModifier(powerType, entity);

		if (modifier == null) {
			source.sendError(Text.literal(String.format("The %s power is not modifiable!", powerType.getIdentifier())));

			return 0;
		}
		
		modifier.ADD_MODIFIER.accept(power, value);

		PowerHolderComponent.syncPower(entity, powerType);

		String total = provider != null
			? provider.VALUE_PROVIDER.apply(power).toString()
			: "?";

		source.sendFeedback(() -> Text.translatable("commands.scoreboard.players.add.success.single", value, powerType.getIdentifier(), entity.getName().getString(), total), true);
		
		return 1;
	}
}