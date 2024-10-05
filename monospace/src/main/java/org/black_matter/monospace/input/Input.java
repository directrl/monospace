package org.black_matter.monospace.input;

import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.event.EventCaller;
import org.black_matter.monospace.events.input.MouseMoveEvent;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

public class Input implements EventCaller {
	
	private boolean mouseInWindow = true;
	
	private double lastCursorX = 0;
	private double lastCursorY = 0;
	
	public Input() {
		GLFW.glfwSetKeyCallback(Monospace.window().getHandle(), this::keyCallback);
		GLFW.glfwSetCursorPosCallback(Monospace.window().getHandle(), this::cursorPosCallback);
		GLFW.glfwSetMouseButtonCallback(Monospace.window().getHandle(), this::mouseButtonCallback);
		GLFW.glfwSetCursorEnterCallback(Monospace.window().getHandle(), (handle, entered) -> mouseInWindow = entered);
	}
	
	public Vector2d getMousePosition() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			var xBuffer = stack.mallocDouble(1);
			var yBuffer = stack.mallocDouble(1);
			
			GLFW.glfwGetCursorPos(Monospace.window().getHandle(), xBuffer, yBuffer);
			
			return new Vector2d(xBuffer.get(), yBuffer.get());
		}
	}
	
	public boolean isKeyDown(int key) {
		return GLFW.glfwGetKey(Monospace.window().getHandle(), key) == GLFW.GLFW_PRESS;
	}
	
	private void keyCallback(long window, int key, int scancode, int action, int mods) {
		Monospace.keyBindings().callback(window, key, scancode, action, mods);
	}
	
	private void cursorPosCallback(long window, double x, double y) {
		if(!mouseInWindow) return;
		
		callEvent(MouseMoveEvent.class, this, new MouseMoveEvent(x, y, x - lastCursorX, y - lastCursorY));
		
		lastCursorX = x;
		lastCursorY = y;
	}
	
	private void mouseButtonCallback(long window, int button, int action, int mods) {
		Monospace.keyBindings().callback(window, button, 0, action, mods);
	}
}
