package io.github.xrickastley.originsmath.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class OriginsMathSavedConfig {
	public static final ConfigClassHandler<OriginsMathSavedConfig> HANDLER;

	@SerialEntry
	public boolean extended_compatibility_through_asm = true;

	static {
		if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
			HANDLER = ConfigClassHandler.createBuilder(OriginsMathSavedConfig.class)
						.id(new Identifier("origins-math", "config"))
						.serializer(config ->
							GsonConfigSerializerBuilder
								.create(config)
								.setPath(FabricLoader.getInstance().getConfigDir().resolve("origins-math.json"))
								.build()
						)
						.build();

			HANDLER.load();
		} else {
			HANDLER = null;
		}		
	}
}
