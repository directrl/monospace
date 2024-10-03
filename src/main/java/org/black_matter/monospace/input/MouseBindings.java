package org.black_matter.monospace.input;

import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.events.core.GameOptionChangeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class MouseBindings {
	
	private static final String OPTION_PREFIX = "mouseBtn.";
	
	private final Map<String, Integer> BUTTON_BINDS = new HashMap<>();
	private final Map<String, MouseAction.Button> BUTTON_ACTIONS = new HashMap<>();
	
	public MouseBindings() {
		Monospace.gameSettings().onEvent(GameOptionChangeEvent.class, this, e -> {
			if(!BUTTON_BINDS.containsKey(e.name())) return;
			BUTTON_BINDS.replace(e.name(), (int) e.to());
		});
	}
	
	public void registerAction(String name, int defaultButton, MouseAction.Button action) {
		if(action == null) {
			BUTTON_BINDS.remove(OPTION_PREFIX + name);
			BUTTON_ACTIONS.remove(OPTION_PREFIX + name);
		} else {
			BUTTON_BINDS.put(OPTION_PREFIX + name,
				Monospace.gameSettings().getOptionOrDefault(OPTION_PREFIX + name, defaultButton));
			BUTTON_ACTIONS.put(OPTION_PREFIX + name, action);
		}
	}
	
	protected void callback(long window, int button, int action, int mods) {
		for(var entry : BUTTON_BINDS.entrySet()) {
			var boundButton = entry.getValue();
			var boundAction = BUTTON_ACTIONS.get(entry.getKey());
			
			if(button != boundButton) continue;
			if(mods != boundAction.mods()) continue;
			
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
		for(var entry : BUTTON_BINDS.entrySet()) {
			var action = BUTTON_ACTIONS.get(entry.getKey());
			if(action.down) action.onDown();
		}
	}
}
