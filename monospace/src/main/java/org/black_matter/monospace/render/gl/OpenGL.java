package org.black_matter.monospace.render.gl;

import static org.lwjgl.opengl.GL11.*;

public class OpenGL {
	
	public static void setup() {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		
		glCullFace(GL_BACK);
	}
}
