package org.black_matter.monospace.util;

import lombok.Getter;

public class DeltaTimer {

	private long start = 0;
	
	/**
	 * Delta time in milliseconds
	 */
	@Getter private float delta = 0;
	
	public void start() {
		start = System.nanoTime();
	}
	
	public void end() {
		delta = (System.nanoTime() - start) / 1_000_000f;
	}
}
