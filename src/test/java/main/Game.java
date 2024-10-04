package main;

import object.CustomModels;
import object.TestObject;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.events.input.MouseMoveEvent;
import org.black_matter.monospace.input.KeyBinding;
import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.objects.CubeObject;
import org.black_matter.monospace.render.camera.PerspectiveCamera;
import org.black_matter.monospace.world.GameWorld;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class Game extends Monospace {
	
	private static final Random RANDOM = new Random();
	private static final float CAMERA_SPEED = 0.02f;
	
	public Game() {
		super("test-game");
		
		engineSettings().setWindowTitle("Monospace test");
		engineSettings().setFps(60);
	}
	
	CubeObject cube;
	GameObject test;
	
	KeyBinding cameraForward;
	KeyBinding cameraBackward;
	KeyBinding cameraLeft;
	KeyBinding cameraRight;
	KeyBinding cameraUp;
	KeyBinding cameraDown;
	KeyBinding cameraMove;
	
	float mouseSensitivity;
	
	@Override
	public void init() {
		super.init();
		
		CustomModels.load();
		
		mouseSensitivity = Game.gameSettings().getOptionOrDefault("mouseSensitivity", 0.5f);
		
		cameraForward = keyBindings().registerBinding(new KeyBinding("cameraForward", GLFW.GLFW_KEY_W, 0));
		cameraBackward = keyBindings().registerBinding(new KeyBinding("cameraBackward", GLFW.GLFW_KEY_S, 0));
		cameraLeft = keyBindings().registerBinding(new KeyBinding("cameraLeft", GLFW.GLFW_KEY_A, 0));
		cameraRight = keyBindings().registerBinding(new KeyBinding("cameraRight", GLFW.GLFW_KEY_D, 0));
		cameraUp = keyBindings().registerBinding(new KeyBinding("cameraUp", GLFW.GLFW_KEY_SPACE, 0));
		cameraDown = keyBindings().registerBinding(new KeyBinding("cameraDown", GLFW.GLFW_KEY_LEFT_SHIFT, 0));
		
		cameraMove = keyBindings().registerBinding(new KeyBinding("cameraMove", GLFW.GLFW_MOUSE_BUTTON_RIGHT, 0));
		
		world = new GameWorld();
		world.load();
		
		camera = new PerspectiveCamera();
		camera.setFov(45f);
		
		test = new TestObject().z(-5).x(-2);
		cube = (CubeObject) new CubeObject().z(-5);
		world.getObjectManager().add(cube);
		world.getObjectManager().add(test);
		
		onEvent(MouseMoveEvent.class, Game.input(), e -> {
			if(cameraMove.isDown()) {
				Game.camera().getRotation().add(
					(float) -Math.toRadians(-e.dy() * mouseSensitivity),
					(float) -Math.toRadians(-e.dx() * mouseSensitivity)
				);
				Game.camera().computeViewMatrix();
			}
		});
		
		window().show();
	}
	
	float rotation = 0;
	
	@Override
	public void update(float delta) {
		rotation += 1;
		if(rotation >= 360) rotation = 0;
		//cube.rotation(1, 1, 1, (float) Math.toRadians(rotation));

		float[] cameraMoveVector = new float[6];
		
		if(cameraUp.isDown()) cameraMoveVector[0] = CAMERA_SPEED * delta;
		if(cameraDown.isDown()) cameraMoveVector[1] = CAMERA_SPEED * delta;
		if(cameraLeft.isDown()) cameraMoveVector[2] = CAMERA_SPEED * delta;
		if(cameraRight.isDown()) cameraMoveVector[3] = CAMERA_SPEED * delta;
		if(cameraForward.isDown()) cameraMoveVector[4] = CAMERA_SPEED * delta;
		if(cameraBackward.isDown()) cameraMoveVector[5] = CAMERA_SPEED * delta;
		
		camera.move(
			cameraMoveVector[0],
			cameraMoveVector[1],
			cameraMoveVector[2],
			cameraMoveVector[3],
			cameraMoveVector[4],
			cameraMoveVector[5]
		);
	}
	
	@Override
	public void render() {
		super.render();
	}
	
	@Override
	protected void shutdown() {
		if(world != null && world.isLoaded()) world.unload();
		
		super.shutdown();
	}
}
