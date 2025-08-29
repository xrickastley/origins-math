package io.github.xrickastley.originsmath.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.apace100.apoli.util.HudRender;
import net.minecraft.util.Identifier;

@Mixin(HudRender.class)
public interface HudRenderAccessor {
	@Accessor("children")
    public List<HudRender> getChildren();

	@Accessor("spriteLocation")
	public Identifier getSpriteLocation();
}
