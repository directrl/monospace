package org.black_matter.monospace.tools.worldeditor;

import org.black_matter.monospace.core.Monospace;

public class WorldEditor extends Monospace {
	
	public WorldEditor() {
		super("monospace-worldeditor");
		
		engineSettings().window().setTitle("World Editor");
		engineSettings().rendering().setFps(60);
	}
	
	@Override
	public void init() {
		super.init();
		
		window().show();
	}
}
