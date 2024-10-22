package io.github.xrickastley.originsmath;

import org.mariuszgromada.math.mxparser.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.xrickastley.originsmath.factories.OriginsMathEntityActions;
import io.github.xrickastley.originsmath.factories.OriginsMathEntityConditions;
import io.github.xrickastley.originsmath.factories.OriginsMathPowers;
import io.github.xrickastley.originsmath.util.ResourceBackedInjector;
import io.github.xrickastley.originsmath.util.ResourceCommand;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;

public class OriginsMath implements ModInitializer {
	public static final String MOD_ID = "origins-math";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
		
	public void onInitialize() {
		LOGGER.info("Origins: Math Initialized!");

		// Origins: Math is a free-of-charge, non-profit mod project.
		License.iConfirmNonCommercialUse("_xRickAstley");

		OriginsMathEntityActions.register();
		OriginsMathEntityConditions.register();
		OriginsMathPowers.register();
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ResourceCommand.register(dispatcher);
		});
		
		ResourceBackedInjector.applyInjections();
	}

	public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}
	
	public static Logger sublogger(String sublogger) {
		return LoggerFactory.getLogger(MOD_ID + "/" + sublogger);
	}
		
	public static Logger sublogger(Class<?> sublogger) {
		return sublogger(sublogger.getSimpleName());
	}
		
	public static Logger sublogger(Object sublogger) {
		return sublogger(sublogger.getClass().getSimpleName());
	}
}
