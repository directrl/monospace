package org.black_matter.monospace.core;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.log4j.Logger;
import org.black_matter.monospace.event.EventCaller;
import org.black_matter.monospace.input.Input;
import org.black_matter.monospace.input.KeyBindings;
import org.black_matter.monospace.input.MouseBindings;
import org.black_matter.monospace.render.camera.Camera3D;
import org.black_matter.monospace.render.gl.Shader;
import org.black_matter.monospace.render.gl.ShaderProgram;
import org.black_matter.monospace.ui.DebugUI;
import org.black_matter.monospace.ui.imgui.ImGuiUI;
import org.black_matter.monospace.util.DeltaTimer;
import org.black_matter.monospace.world.GameWorld;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Accessors(fluent = true)
public abstract class Monospace implements EventCaller {
	
	public static Logger LOGGER;
	
	@Getter private static String id;
	
	@Getter private static Window window;
	@Getter private static ImGuiUI imgui;
	
	@Getter private static EngineSettings engineSettings;
	@Getter private static GameSettings gameSettings;
	@Getter private static MouseBindings mouseBindings;
	@Getter private static KeyBindings keyBindings;
	@Getter private static Input input;
	@Getter private static DeltaTimer timer;
	
	@Getter protected static Camera3D camera;
	@Getter protected static GameWorld world;
	
	@Getter @Setter
	private static boolean debug;
	
	public Monospace(String id) {
		Monospace.id = id;
		
		Configuration.initDirs(id);
		
		org.tinylog.configuration.Configuration.set(
			"writerf.file",
			Configuration.LOGS_PATH.resolve("{date}.log").toString()
		);
		
		org.tinylog.configuration.Configuration.set(
			"writerf.latest",
			Configuration.LOGS_PATH.resolve("latest.log").toString()
		);
		
		engineSettings = new EngineSettings();
		
		try {
			gameSettings = new GameSettings(Configuration.CONFIG_PATH.resolve("config.json").toFile());
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		debug = gameSettings.getOptionOrDefault("debug", false);
		
		org.tinylog.configuration.Configuration.set("writerc.level",
			gameSettings.getOptionOrDefault("logLevel", "info"));
		org.tinylog.configuration.Configuration.set("writerf.level",
			gameSettings.getOptionOrDefault("logLevel", "info"));
		
		LOGGER = Logger.getLogger("monospace");
		
		GLFWErrorCallback.createPrint(System.err);
		
		if(!GLFW.glfwInit()) {
			throw new RuntimeException("Could not initialize GLFW");
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
	}
	
	public void init() {
		window = new Window(
			engineSettings().getWindowTitle(),
			new Vector2i(-1, -1),
			new Vector2i(640, 480)
		);
		imgui = new ImGuiUI(window);
		
		GLFW.glfwSwapInterval(gameSettings.getOptionOrDefault("vsync", true) ? 1 : 0);
		
		OpenGL.setup();
		buildAllShaders();
		
		mouseBindings = new MouseBindings();
		keyBindings = new KeyBindings();
		input = new Input();
		timer = new DeltaTimer();
	}
	
	public void update(float delta) { }
	
	public void render() {
		GL30.glUseProgram(0);
	}
	
	public void ui() {
		imgui().begin();
		DebugUI.render();
		imgui().end();
	}
	
	protected void shutdown() {
		LOGGER.info("Saving game settings");
		
		try {
			gameSettings.close();
		} catch(IOException e) {
			LOGGER.fatal("Could not save game settings", e);
		}
	}
	
	public void launch(String... args) {
		this.init();
		
		while(!GLFW.glfwWindowShouldClose(window.getHandle())) {
			timer.start();
			
			keyBindings.update();
			mouseBindings.update();
			
			window.begin();
			{
				if(world != null) {
					GameWorld.WORLD_SHADER.bind();
					camera.render(GameWorld.WORLD_SHADER);
				}
				
				update(timer.getDelta());
				if(world != null) world.update(timer.getDelta());
				render();
				ui();
			}
			window.end();
			
			timer.end();
		}
	}
	
	// TODO separate into own class and make a cool loading UI with swing
	private void buildAllShaders() {
		var shaders = ShaderProgram.Registry.get().values();
		
		int i = 1;
		for(var shader : shaders) {
			LOGGER.info(String.format("Building shaders... [%d/%d]", i, shaders.size()));
			
			try {
				shader.build();
				shader.validate();
			} catch(Shader.CompilationException
			        | ShaderProgram.LinkingException
			        | ShaderProgram.ValidationException e) {
				
				LOGGER.fatal("Exception occurred when building shader", e);
			}
			
			i++;
		}
	}
	
	public static Monospace launch(Class<? extends Monospace> cls, String... args)
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		
		var inst = cls.getConstructor().newInstance();
		inst.launch(args);
		
		return inst;
	}
}
