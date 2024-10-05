package org.black_matter.monospace.core;

import lombok.Getter;
import org.black_matter.monospace.event.EventCaller;
import org.black_matter.monospace.events.core.WindowResizeEvent;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.function.BiConsumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window implements EventCaller {
	
	private static long ctxShare = -1;
	
	@Getter private long handle;
	
	public Window(String title, Vector2i position, Vector2i dimensions,
	              boolean fullscreen, long monitor) {
		
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		
		if(monitor == MemoryUtil.NULL) {
			monitor = glfwGetPrimaryMonitor();
		}
		
		if(fullscreen) {
			var vmode = glfwGetVideoMode(monitor);
			dimensions = new Vector2i(vmode.width(), vmode.height());
		}
		
		handle = glfwCreateWindow(
			dimensions.x,
			dimensions.y,
			title,
			fullscreen ? monitor : MemoryUtil.NULL,
			ctxShare > 0 ? ctxShare : MemoryUtil.NULL
		);
		
		if(handle == MemoryUtil.NULL) {
			throw new RuntimeException("Unable to create a GLFW window");
		}
		
		if(ctxShare <= 0) {
			ctxShare = handle;
		}
		
		if(position.x == -1 || position.y == -1 && !fullscreen) {
			var vmode = glfwGetVideoMode(monitor);
			
			glfwSetWindowPos(
				handle,
				(vmode.width() / 2) - (dimensions.x / 2),
				(vmode.height() / 2) - (dimensions.y / 2)
			);
		} else if(!fullscreen) {
			glfwSetWindowPos(handle, position.x, position.y);
		}
		
		glfwMakeContextCurrent(handle);
		GL.createCapabilities();
		
		registerCallbacks();
	}
	
	public Window(String title, Vector2i position, Vector2i dimensions) {
		this(title, position, dimensions, false, MemoryUtil.NULL);
	}
	
	public void destroy() {
		glfwDestroyWindow(handle);
		handle = MemoryUtil.NULL;
	}
	
	public void show() {
		glfwShowWindow(handle);
	}
	
	public void hide() {
		glfwHideWindow(handle);
	}
	
	public void begin() {
		glfwMakeContextCurrent(handle);
		
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			
			glfwGetFramebufferSize(this.getHandle(), width, height);
			
			glViewport(0, 0, width.get(), height.get());
			glClearColor(0, 0, 0, 0);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		}
	}
	
	public void end() {
		glfwSwapBuffers(handle);
		glfwPollEvents();
	}
	
	public String getTitle() {
		return glfwGetWindowTitle(handle);
	}
	
	public void setTitle(String title) {
		glfwSetWindowTitle(handle, title);
	}
	
	public Vector2i getPosition() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer x = stack.mallocInt(1);
			IntBuffer y = stack.mallocInt(1);
			
			glfwGetWindowPos(handle, x, y);
			return new Vector2i(x.get(), y.get());
		}
	}
	
	public void setPosition(Vector2i position) {
		glfwSetWindowPos(handle, position.x, position.y);
	}
	
	public Vector2i getDimensions() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			
			glfwGetWindowSize(handle, width, height);
			return new Vector2i(width.get(), height.get());
		}
	}
	
	public void setDimensions(Vector2i dimensions) {
		glfwSetWindowSize(handle, dimensions.x, dimensions.y);
	}
	
	public void registerCallbacks() {
		glfwSetWindowSizeCallback(handle,
			(_handle, _width, _height) ->
				this.callEvent(WindowResizeEvent.class, this, new WindowResizeEvent(_handle, _width, _height)));
	}
}
