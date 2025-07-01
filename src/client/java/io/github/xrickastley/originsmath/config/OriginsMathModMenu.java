package io.github.xrickastley.originsmath.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import io.github.xrickastley.originsmath.screen.RequiresYACLScreen;
import net.fabricmc.loader.api.FabricLoader;

public class OriginsMathModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")
			? OriginsMathConfig.createBuilder().generateScreen(screen)
			: new RequiresYACLScreen(screen);
	}
}
