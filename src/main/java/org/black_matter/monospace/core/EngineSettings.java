package org.black_matter.monospace.core;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.lwjgl.opengl.GL30;

@Getter
@Accessors(fluent = true)
public class EngineSettings {
	
	private Window window = new Window();
	private Graphics graphics = new Graphics();
	private Rendering rendering = new Rendering();
	
	@Getter @Setter
	@Accessors(fluent = false)
	public class Window {
		
		private String title = "Monospace Engine";
		private int width = 1280;
		private int height = 720;
	}
	
	@Getter @Setter
	@Accessors(fluent = false)
	public class Graphics {
		
		private boolean textureMipmapping = true;
		private int textureFilter = GL30.GL_NEAREST;
	}
	
	@Getter @Setter
	@Accessors(fluent = false)
	public class Rendering {
		
		private int fps = 60;
		private int vsync = 1;
	}
}
