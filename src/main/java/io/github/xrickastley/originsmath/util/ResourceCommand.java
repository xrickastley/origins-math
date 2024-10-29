package io.github.xrickastley.originsmath.util;

import java.util.Optional;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

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
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

// will this even overwrite or just add? edit: THIS IS POSSIBLE?!?!?!?!
public class ResourceCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("resource")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(literal("get")
                    .then(literal("absolute")
                        .then(argument("target", EntityArgumentType.entity())
                            .then(argument("power", PowerTypeArgumentType.power())
                                .executes((command) -> resource(command, ClassTinkerers.getEnum(SubCommand.class, "GET_ABSOLUTE")))))))
        );
    }

    // This is a cleaner method than sticking it into every subcommand
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
}