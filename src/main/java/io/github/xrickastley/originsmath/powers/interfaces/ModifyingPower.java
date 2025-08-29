package io.github.xrickastley.originsmath.powers.interfaces;

import java.util.ArrayList;
import java.util.List;

import io.github.apace100.apoli.util.modifier.Modifier;

/**
 * For powers that contain modifiers, but don't extend {@code ValueModifyingPower}. <br> <br>
 * 
 * This interface is implemented by Origins: Math on {@code ValueModifyingPower}, allowing you
 * to check for this simple interface rather than the {@code ValueModifyingPower} for powers that
 * don't extend it.
 */
public interface ModifyingPower {
	default List<Modifier> getModifiers() {
		return new ArrayList<>();
	}
}
