package org.black_matter.monospace.ui;

import imgui.ImGui;
import org.black_matter.monospace.core.Monospace;

public class DebugUI {
	
	public static void render() {
		if(ImGui.begin("Engine Debug Info")) {
			ImGui.text(String.format("Engine delta time: %.2fms (%.2f FPS)",
				Monospace.timer().getDelta(),
				1000f / Monospace.timer().getDelta()));
			ImGui.text(String.format("ImGui delta time: %.2fms (%.2f FPS)",
				ImGui.getIO().getDeltaTime(),
				1000f / ImGui.getIO().getDeltaTime()));
			
			ImGui.spacing();
			
			if(Monospace.camera() != null) {
				var pos = Monospace.camera().getPosition();
				
				ImGui.text(String.format("Camera X: %.2f", pos.x));
				ImGui.text(String.format("Camera Y: %.2f", pos.y));
				ImGui.text(String.format("Camera Z: %.2f", pos.z));
			}
			
			ImGui.end();
		}
	}
}
