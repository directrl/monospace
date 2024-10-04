package main;

import input.CameraMove;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.input.KeyAction;
import org.black_matter.monospace.input.KeyBinding;
import org.black_matter.monospace.object.objects.CubeObject;
import org.black_matter.monospace.render.camera.PerspectiveCamera;
import org.black_matter.monospace.world.GameWorld;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class Game extends Monospace {
	
	private static Random random = new Random();
	
	public Game() {
		super("test-game");
		
		engineSettings().setWindowTitle("Test main.Game");
		engineSettings().setFps(60);
	}
	
	CubeObject cube;

	KeyBinding testBinding;
	
	@Override
	public void init() {
		super.init();
		
		keyBindings().registerAction("meow", GLFW.GLFW_KEY_F, new KeyAction() {
			
			@Override
			public int mods() {
				return GLFW.GLFW_MOD_CONTROL;
			}
			
			@Override
			public void onPress() {
				if(world != null) {
					LOGGER.info("Spawning cube");
					
					world.getObjectManager().add(
						new CubeObject()
							.x(random.nextFloat(-10, 10))
							.y(random.nextFloat(-10, 10))
							.z(random.nextFloat(-10, 10))
					);
				}
			}
		});
		
		keyBindings().registerAction("cameraForward", GLFW.GLFW_KEY_W, new CameraMove.Forward());
		keyBindings().registerAction("cameraBackward", GLFW.GLFW_KEY_S, new CameraMove.Backward());
		keyBindings().registerAction("cameraLeft", GLFW.GLFW_KEY_A, new CameraMove.Left());
		keyBindings().registerAction("cameraRight", GLFW.GLFW_KEY_D, new CameraMove.Right());
		keyBindings().registerAction("cameraUp", GLFW.GLFW_KEY_SPACE, new CameraMove.Up());
		keyBindings().registerAction("cameraDown", GLFW.GLFW_KEY_LEFT_SHIFT, new CameraMove.Down());
		
		mouseBindings().registerAction("cameraMove", GLFW.GLFW_MOUSE_BUTTON_RIGHT, new CameraMove.Rotation());
		
		testBinding = keyBindings().registerBinding(new KeyBinding("test", GLFW.GLFW_KEY_H, 0));

		world = new GameWorld();
		world.load();
		
		camera = new PerspectiveCamera();
		camera.setFov(45f);
		
		cube = (CubeObject) new CubeObject().z(-5);
		world.getObjectManager().add(cube);
		
		window().show();
	}
	
	float rotation = 0;
	
	@Override
	public void update(float delta) {
		rotation += 1;
		if(rotation >= 360) rotation = 0;
		cube.rotation(1, 1, 1, (float) Math.toRadians(rotation));
		super.update(delta);

		if(testBinding.pressed) {
			System.out.println("hello!");
		}
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
