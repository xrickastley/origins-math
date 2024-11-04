package io.github.xrickastley.originsmath.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.text.DecimalFormat;

import io.github.apace100.apoli.command.PowerHolderArgumentType;
import io.github.apace100.apoli.command.PowerTypeArgumentType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.util.modifier.ModifierUtil;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModifierCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager
				.literal("modifier")
				.requires(cs -> cs.hasPermissionLevel(2))
				.then(
					CommandManager
						.literal("apply")
						.then(
							CommandManager
								.argument("target", PowerHolderArgumentType.holder())
								.then(
									CommandManager
										.argument("power", PowerTypeArgumentType.power())
										.then(
											CommandManager
												.argument("base", DoubleArgumentType.doubleArg())
												.executes(ModifierCommand::apply)
										)
								)
						)
				)
		);
	}

	private static int apply(final CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final ServerCommandSource source = context.getSource();
		final LivingEntity target = PowerHolderArgumentType.getHolder(context, "target");
		final PowerHolderComponent component = PowerHolderComponent.KEY.get(target);

		final double base = DoubleArgumentType.getDouble(context, "base");
		final PowerType<?> powerType = PowerTypeArgumentType.getPower(context, "power");
		final Power power = component.getPower(powerType);

		if (power == null) {
			final Text errorText = Text.of(String.format("Entity %s doesn't have the requested power: %s", target.getName().getString(), powerType.getIdentifier()));
			errorText.getStyle().withColor(Formatting.RED);

			source.sendError(errorText);

			return 0;
		}

		if (!(power instanceof final ValueModifyingPower vmp)) {
			final Text errorText = Text.of(String.format("Power type %s isn't a valid modifying power!", powerType.getIdentifier()));
			errorText.getStyle().withColor(Formatting.RED);

			source.sendError(errorText);

			return 0;
		}

		final DecimalFormat df = new DecimalFormat("#.#####");

		source.sendFeedback(() -> Text.of(String.format("Applied Modifier: %s\nBase value: %s\nModifier Result: %s", powerType.getIdentifier().toString(), df.format(base), df.format(ModifierUtil.applyModifiers(target, vmp.getModifiers(), base)))), false);

		return 1;
	}
}
