package io.github.xrickastley.originsmath.mixins;

import org.spongepowered.asm.mixin.Mixin;

import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.xrickastley.originsmath.powers.interfaces.ModifyingPower;

@Mixin(ValueModifyingPower.class)
public abstract class ValueModifyingPowerMixin implements ModifyingPower {}
