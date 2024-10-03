package org.black_matter.monospace.core;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.log4j.Logger;
import org.black_matter.monospace.input.Input;
import org.black_matter.monospace.input.KeyBindings;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Accessors(fluent = true)
public abstract class Monospace {
	
	public static Logger LOGGER;
	
	@Getter private static String id;
	
	@Getter private static Window window;
	
	@Getter private static EngineSettings engineSettings;
	@Getter private static GameSettings gameSettings;
	@Getter private static KeyBindings keyBindings;
	@Getter private static Input input;
	
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
		
		org.tinylog.configuration.Configuration.set("writerc.level",
			gameSettings.getOptionOrDefault("logLevel", "info"));
		org.tinylog.configuration.Configuration.set("writerf.level",
			gameSettings.getOptionOrDefault("logLevel", "info"));
		
		LOGGER = Logger.getLogger("monospace");
		
		GLFWErrorCallback.createPrint(System.err);
		
		if(!GLFW.glfwInit()) {
			throw new RuntimeException("Could not initialize GLFW");
		}
		
		window = new Window(id, new Vector2i(0, 0), new Vector2i(640, 480));
		keyBindings = new KeyBindings();
		input = new Input();
		
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
		
		window.show();
	}
	
	public abstract void init();
	public abstract void update(float delta);
	public abstract void render();
	
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
			keyBindings.update();
			update(0);
			
			window.begin();
			
			render();
			
			window.end();
		}
	}
	
	public static Monospace launch(Class<? extends Monospace> cls, String... args)
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		
		var inst = cls.getConstructor().newInstance();
		inst.launch(args);
		
		return inst;
	}
}
