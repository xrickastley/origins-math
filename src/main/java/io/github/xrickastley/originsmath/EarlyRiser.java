package io.github.xrickastley.originsmath;

import com.chocohead.mm.api.ClassTinkerers;

public class EarlyRiser implements Runnable {
	@Override
	public void run() {
		ClassTinkerers.enumBuilder("io.github.apace100.apoli.command.ResourceCommand$SubCommand")
			.addEnum("GET_ABSOLUTE")
			.build();
	}
}
