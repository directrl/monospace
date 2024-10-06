package org.black_matter.monospace.tools.worldeditor.ui;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;

import static org.black_matter.monospace.tools.worldeditor.WorldEditor.*;

public class InfoPanel {
	
	public ImBoolean open = new ImBoolean(false);
	
	/*public final ImInt cGridLines = new ImInt(20);
	public final ImFloat cGridSpacing = new ImFloat(1.0f);*/
	
	public int[] cGridLines = new int[] { coordinateGrid.getLines() };
	public float[] cGridSpacing = new float[] { coordinateGrid.getSpacing() };
	
	public void ui() {
		if(!open.get()) return;
		
		if(ImGui.begin("Information", open)) {
			ImGui.text(String.format("X: %.2f", camera().getPosition().x));
			ImGui.text(String.format("Y: %.2f", camera().getPosition().y));
			ImGui.text(String.format("Z: %.2f", camera().getPosition().z));
			
			ImGui.separator();
			
			ImGui.text("Coordinate grid");
			/*ImGui.inputInt("Lines", cGridLines);
			ImGui.inputFloat("Spacing", cGridSpacing, 0.5f, 1.0f);*/
			ImGui.sliderInt("Lines", cGridLines, 3, 50);
			ImGui.sliderFloat("Spacing", cGridSpacing, 0.1f, 10f, "%.1f");
			
			ImGui.end();
		}
	}
}
