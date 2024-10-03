package org.black_matter.monospace.input;

import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.events.core.GameOptionChangeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class KeyBindings {

	private static final String OPTION_PREFIX = "key.";
	
	private final Map<String, Integer> KEY_BINDS = new HashMap<>();
	private final Map<String, KeyAction> KEY_ACTIONS = new HashMap<>();
	
	public KeyBindings() {
		Monospace.gameSettings().onEvent(GameOptionChangeEvent.class, this, e -> {
			if(!KEY_BINDS.containsKey(e.name())) return;
			KEY_BINDS.replace(e.name(), (int) e.to());
		});
	}
	
	public void registerAction(String name, int defaultKey, KeyAction action) {
		if(action == null) {
			KEY_BINDS.remove(OPTION_PREFIX + name);
			KEY_ACTIONS.remove(OPTION_PREFIX + name);
		} else {
			KEY_BINDS.put(OPTION_PREFIX + name,
				Monospace.gameSettings().getOptionOrDefault(OPTION_PREFIX + name, defaultKey));
			KEY_ACTIONS.put(OPTION_PREFIX + name, action);
		}
	}
	
	protected void callback(long window, int key, int scancode, int action, int mods) {
		for(var entry : KEY_BINDS.entrySet()) {
			var boundKey = entry.getValue();
			var boundAction = KEY_ACTIONS.get(entry.getKey());
			
			if(key != boundKey) continue;
			
			// TODO this is truly awful
			/*if((key != GLFW.GLFW_KEY_LEFT_SHIFT
				|| key != GLFW.GLFW_KEY_LEFT_ALT
				|| key != GLFW.GLFW_KEY_LEFT_CONTROL
				|| key != GLFW.GLFW_KEY_RIGHT_SHIFT
				|| key != GLFW.GLFW_KEY_RIGHT_ALT
				|| key != GLFW.GLFW_KEY_RIGHT_SHIFT)
				&& mods != boundAction.mods()) continue;*/
			
			switch(action) {
				case GLFW.GLFW_PRESS:
					boundAction.onPress();
					boundAction.down = true;
					break;
				case GLFW.GLFW_RELEASE:
					boundAction.down = false;
					boundAction.onRelease();
					break;
			}
		}
	}
	
	public void update() {
		for(var entry : KEY_BINDS.entrySet()) {
			var action = KEY_ACTIONS.get(entry.getKey());
			if(action.down) action.onDown();
		}
	}
}
