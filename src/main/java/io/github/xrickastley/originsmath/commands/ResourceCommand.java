package io.github.xrickastley.originsmath.commands;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Optional;

import io.github.apace100.apoli.command.PowerTypeArgumentType;
import io.github.apace100.apoli.command.ResourceCommand.SubCommand;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.xrickastley.originsmath.powers.LinkedVariableIntPower;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
        
        Entity player = EntityArgumentType.getEntity(context, "target");
        
        if (!(player instanceof LivingEntity)) {}

        ServerCommandSource source = context.getSource();
        PowerType<?> powerType = PowerTypeArgumentType.getPower(context, "power");
        Optional<PowerHolderComponent> phc = PowerHolderComponent.KEY.maybeGet(player);
        
        if (phc.isEmpty()) {
            source.sendError(Text.translatable("commands.apoli.resource.invalid_entity"));
            
            return 0;
        }

        Power power = PowerHolderComponent.KEY.get(player).getPower(powerType);

		double value = getAbsoluteValue(power);

		source.sendFeedback(() -> Text.translatable("commands.scoreboard.players.get.success", player.getName().getString(), value, powerType.getIdentifier()), true);

        return (int) value;
    }

    public static double getAbsoluteValue(Power power) {
		if (power instanceof LinkedVariableIntPower lvip) return lvip.supplyDoubleValue();
        else if (power instanceof VariableIntPower vip) return vip.getValue();
        else if (power instanceof CooldownPower cp) return cp.getRemainingTicks();
        else return 0;
    }

    public static double obtainResourceValue(Entity entity, PowerType<?> powerType) {
		final Power power = powerType.get(entity);

		if (power instanceof final LinkedVariableIntPower lvip) return lvip.supplyDoubleValue();
		else if (power instanceof final VariableIntPower vip) return vip.getValue();
		else if (power instanceof final CooldownPower cp) return cp.getRemainingTicks();
		else throw new RuntimeException(String.format("Attempted to use invalid power type \"%s\" as a resource!", power.getType().getIdentifier().toString()));
	}
}