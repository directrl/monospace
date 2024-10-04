package org.black_matter.monospace.input;

import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.events.core.GameOptionChangeEvent;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class KeyBindings {

	private static final String OPTION_PREFIX = "key.";
	
	// list of keys that are handled by GLFW in a stupid way
	private static final Set<Integer> STUPID_KEYS = Set.of(
		GLFW.GLFW_KEY_LEFT_SHIFT,
		GLFW.GLFW_KEY_RIGHT_SHIFT,
		GLFW.GLFW_KEY_LEFT_ALT,
		GLFW.GLFW_KEY_RIGHT_ALT,
		GLFW.GLFW_KEY_LEFT_CONTROL,
		GLFW.GLFW_KEY_RIGHT_CONTROL
	);

	private final Map<String, KeyBinding> keyBindings = new HashMap<>();
	
	public KeyBindings() {
		Monospace.gameSettings().onEvent(GameOptionChangeEvent.class, this, e -> {
			if(keyBindings.containsKey(e.name())) {
				var binding = keyBindings.get(e.name());
				binding.fromOption((JSONObject) e.to());
			}
		});
	}

	public KeyBinding registerBinding(KeyBinding binding) {
		binding.fromOption(
			Monospace.gameSettings().getOptionOrDefault(OPTION_PREFIX + binding.getName(), binding.toOption())
		);
		
		binding.stupid = STUPID_KEYS.contains(binding.getKey());

		keyBindings.put(binding.getName(), binding);
		return binding;
	}
	
	protected void callback(long window, int key, int scancode, int action, int mods) {
		for(var binding : keyBindings.values()) {
			boolean cond1 = (key == binding.getKey());
			boolean cond2 = (mods == binding.getMods());
			boolean cond3 = (binding.getMods() == 0) ? cond1 : (cond1 && cond2);
			
			if(cond3 && action == GLFW.GLFW_PRESS) {
				binding.pressed = true;
				binding.down = true;
			}
			
			if(cond1 && action == GLFW.GLFW_RELEASE) {
				binding.released = true;
				binding.down = false;
			}
		}
	}
}
