package org.black_matter.monospace.ui.imgui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.internal.ImGuiContext;
import org.black_matter.monospace.core.Configuration;
import org.black_matter.monospace.core.Window;

public class ImGuiUI {
	
	private final ImGuiContext imCtx;
	private final ImGuiIO imIo;
	
	private final ImGuiImplGl3 imGl3;
	public final ImGuiImplGlfwMulti imGlfw;
	
	public ImGuiUI(Window window) {
		imCtx = ImGui.createContext();
		imIo = ImGui.getIO();
		
		imIo.setConfigFlags(ImGuiConfigFlags.DockingEnable);
		
		imGl3 = new ImGuiImplGl3();
		imGlfw = new ImGuiImplGlfwMulti(window, imCtx);
		
		imGlfw.init(window.getHandle(), true);
		imGl3.init();
		
		imIo.setIniFilename(Configuration.DATA_PATH.resolve("imgui.ini").toString());
	}
	
	public void begin() {
		ImGui.setCurrentContext(imCtx);
		
		imGl3.newFrame();
		imGlfw.newFrame();
		ImGui.newFrame();
	}
	
	public void end() {
		ImGui.render();
		imGl3.renderDrawData(ImGui.getDrawData());
	}
}
