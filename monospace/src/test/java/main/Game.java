package main;

import object.TestObject;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.events.input.MouseMoveEvent;
import org.black_matter.monospace.input.KeyBinding;
import org.black_matter.monospace.model.Model;
import org.black_matter.monospace.model.ModelLoader;
import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.objects.ModelObject;
import org.black_matter.monospace.render.camera.PerspectiveCamera;
import org.black_matter.monospace.render.gl.Shader;
import org.black_matter.monospace.render.gl.ShaderProgram;
import org.black_matter.monospace.util.Ray;
import org.black_matter.monospace.util.Resource;
import org.black_matter.monospace.world.GameWorld;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

import java.util.Random;

public class Game extends Monospace {
	
	private static final Random RANDOM = new Random();
	private static final float CAMERA_SPEED = 0.02f;
	
	public Game() {
		super("test-game");
		
		engineSettings().window().setTitle("Monospace test");
		//engineSettings().graphics().setTextureFilter(GL30.GL_LINEAR);
	}
	
	ModelObject model;
	GameObject test;
	GameObject test2;
	GameObject test3;
	
	KeyBinding cameraForward;
	KeyBinding cameraBackward;
	KeyBinding cameraLeft;
	KeyBinding cameraRight;
	KeyBinding cameraUp;
	KeyBinding cameraDown;
	KeyBinding cameraMove;
	
	KeyBinding objectPick;
	
	float mouseSensitivity;
	
	Model monument;
	Model untitled;
	Model knight;
	
	Ray ray;
	
	ShaderProgram selectionShader;
	GameObject selectedObject;
	
	@Override
	public void init() {
		super.init();
		
		selectionShader = ShaderProgram.Registry.get("selection");
		
		//monument = ModelLoader.load(gameResources().get(Resource.Type.MODELS, "monument.obj"));
		untitled = ModelLoader.load(gameResources().get(Resource.Type.MODELS, "Untitled.gltf"));
		//knight = ModelLoader.load(gameResources().get(Resource.Type.MODELS, "knight/chr_knight.obj"));
		
		mouseSensitivity = Game.gameSettings().getOptionOrDefault("mouseSensitivity", 0.5f);
		
		cameraForward = keyBindings().registerBinding(new KeyBinding("cameraForward", GLFW.GLFW_KEY_W, 0));
		cameraBackward = keyBindings().registerBinding(new KeyBinding("cameraBackward", GLFW.GLFW_KEY_S, 0));
		cameraLeft = keyBindings().registerBinding(new KeyBinding("cameraLeft", GLFW.GLFW_KEY_A, 0));
		cameraRight = keyBindings().registerBinding(new KeyBinding("cameraRight", GLFW.GLFW_KEY_D, 0));
		cameraUp = keyBindings().registerBinding(new KeyBinding("cameraUp", GLFW.GLFW_KEY_SPACE, 0));
		cameraDown = keyBindings().registerBinding(new KeyBinding("cameraDown", GLFW.GLFW_KEY_LEFT_SHIFT, 0));
		
		cameraMove = keyBindings().registerBinding(new KeyBinding("cameraMove", GLFW.GLFW_MOUSE_BUTTON_RIGHT, 0));
		
		objectPick = keyBindings().registerBinding(new KeyBinding("objectPick", GLFW.GLFW_MOUSE_BUTTON_LEFT, 0));
		
		world = new GameWorld();
		world.load();
		
		camera = new PerspectiveCamera();
		camera.setFov(45f);
		
		test = new TestObject(knight).z(-5).x(-5);
		test2 = new TestObject(untitled).z(-5).x(0);
		test3 = new TestObject(monument).z(-5).x(5);
		
		world.getObjectManager().add(new ModelObject(untitled).z(-10));
		world.getObjectManager().add(new ModelObject(untitled).x(-10).z(-10));
		world.getObjectManager().add(new ModelObject(untitled).x(10).z(-10));
		
		ray = new Ray(camera, world);
		
		onEvent(MouseMoveEvent.class, Game.input(), e -> {
			if(cameraMove.isDown()) {
				Game.camera().getRotation().add(
					(float) -Math.toRadians(-e.dy() * mouseSensitivity),
					(float) -Math.toRadians(-e.dx() * mouseSensitivity)
				);
				Game.camera().computeViewMatrix();
			}
			
			selectedObject = ray.getHitObject();
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
		
		if(objectPick.wasPressed()) {
			var o = ray.getHitObject();
			
			if(o != null) {
				System.out.println("hit object! " + o);
				//o.rotate(0, 0, 0, (float) Math.toRadians(15));
				//o.scale(2);
			}
		}
	}
	
	@Override
	public void render() {
		//GameWorld.WORLD_SHADER.getUniforms().load("selection", selectedObject == null ? 0 : 1);
		super.render();
	}
	
	@Override
	protected void shutdown() {
		if(world != null && world.isLoaded()) world.unload();
		
		super.shutdown();
	}
}
