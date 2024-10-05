package org.black_matter.monospace.tools.worldeditor.keybindings;

import org.black_matter.monospace.input.KeyBinding;
import org.lwjgl.glfw.GLFW;

import static org.black_matter.monospace.tools.worldeditor.WorldEditor.*;

public class EditorShortcuts {
	
	public KeyBinding spawnCustomModel;
	
	public EditorShortcuts() {
		spawnCustomModel = keyBindings().registerBinding(new KeyBinding(
			"spawnCustomModel", GLFW.GLFW_KEY_A, GLFW.GLFW_MOD_SHIFT | GLFW.GLFW_MOD_CONTROL));
	}
	
	public void update(float delta) {
		if(spawnCustomModel.wasPressed()) {
			chooseAndSpawnModel(camera().getPosition());
		}
	}
}
