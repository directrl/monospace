package org.black_matter.monospace.tools.worldeditor.keybindings;

import org.black_matter.monospace.event.EventCaller;
import org.black_matter.monospace.events.input.MouseMoveEvent;
import org.black_matter.monospace.input.KeyBinding;
import org.black_matter.monospace.tools.worldeditor.WorldEditor;
import org.lwjgl.glfw.GLFW;

import static org.black_matter.monospace.tools.worldeditor.WorldEditor.*;

public class CameraMovement implements EventCaller {
	
	public static final float CAMERA_SPEED = 0.02f;
	
	public KeyBinding cameraForward;
	public KeyBinding cameraBackward;
	public KeyBinding cameraLeft;
	public KeyBinding cameraRight;
	public KeyBinding cameraUp;
	public KeyBinding cameraDown;
	public KeyBinding cameraMove;
	
	public CameraMovement() {
		cameraForward = keyBindings().registerBinding(new KeyBinding("cameraForward", GLFW.GLFW_KEY_W, 0));
		cameraBackward = keyBindings().registerBinding(new KeyBinding("cameraBackward", GLFW.GLFW_KEY_S, 0));
		cameraLeft = keyBindings().registerBinding(new KeyBinding("cameraLeft", GLFW.GLFW_KEY_A, 0));
		cameraRight = keyBindings().registerBinding(new KeyBinding("cameraRight", GLFW.GLFW_KEY_D, 0));
		cameraUp = keyBindings().registerBinding(new KeyBinding("cameraUp", GLFW.GLFW_KEY_SPACE, 0));
		cameraDown = keyBindings().registerBinding(new KeyBinding("cameraDown", GLFW.GLFW_KEY_LEFT_SHIFT, 0));
		cameraMove = keyBindings().registerBinding(new KeyBinding("cameraMove", GLFW.GLFW_MOUSE_BUTTON_RIGHT, 0));
		
		onEvent(MouseMoveEvent.class, input(), e -> {
			if(cameraMove.isDown()) {
				camera().getRotation().add(
					(float) -Math.toRadians(-e.dy() * 1),
					(float) -Math.toRadians(-e.dx() * 1)
				);
				camera().computeViewMatrix();
			}
		});
	}
	
	public void update(float delta) {
		float[] cameraMoveVector = new float[6];
		
		if(cameraUp.isDown()) camera().getPosition().y += CAMERA_SPEED * delta;
		if(cameraDown.isDown()) camera().getPosition().y -= CAMERA_SPEED * delta;
		if(cameraLeft.isDown()) cameraMoveVector[2] = CAMERA_SPEED * delta;
		if(cameraRight.isDown()) cameraMoveVector[3] = CAMERA_SPEED * delta;
		if(cameraForward.isDown()) cameraMoveVector[4] = CAMERA_SPEED * delta;
		if(cameraBackward.isDown()) cameraMoveVector[5] = CAMERA_SPEED * delta;
		
		camera().move(
			cameraMoveVector[0],
			cameraMoveVector[1],
			cameraMoveVector[2],
			cameraMoveVector[3],
			cameraMoveVector[4],
			cameraMoveVector[5]
		);
	}
}
