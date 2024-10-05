package org.black_matter.monospace.tools.worldeditor.ui;

import imgui.ImGui;
import imgui.type.ImFloat;

import static org.black_matter.monospace.tools.worldeditor.WorldEditor.*;

public class ObjectEditor {
	
	private long selectedObjectId = -1;
	
	private final ImFloat objectX = new ImFloat(0);
	private final ImFloat objectY = new ImFloat(0);
	private final ImFloat objectZ = new ImFloat(0);
	
	private final ImFloat objectRotationX = new ImFloat(0);
	private final ImFloat objectRotationY = new ImFloat(0);
	private final ImFloat objectRotationZ = new ImFloat(0);
	private final ImFloat objectRotationW = new ImFloat(0);
	
	public void ui(PrefabCollection prefabCollection) {
		if(ImGui.begin("Objects")) {
			if(ImGui.beginListBox("Object listing")) {
				for(var object : world().getObjectManager().get().values()) {
					if(ImGui.selectable(String.format("ID: %d, %s", object.getId(),
						prefabCollection != null && prefabCollection.prefabObjects.containsKey(object.getId()) ?
							prefabCollection.prefabObjects.get(object.getId()).getId()
							: object.getClass().getSimpleName()))) {
						
						selectedObjectId = object.getId();
						
						objectX.set(object.x());
						objectY.set(object.y());
						objectZ.set(object.z());
						
						objectRotationX.set((float) Math.toDegrees(object.rotation().x));
						objectRotationY.set((float) Math.toDegrees(object.rotation().y));
						objectRotationZ.set((float) Math.toDegrees(object.rotation().z));
						objectRotationW.set((float) Math.toDegrees(object.rotation().w));
					}
				}
				
				ImGui.endListBox();
			}
			
			if(selectedObjectId >= 0) {
				var object = world().getObjectManager().query(selectedObjectId);
				
				/*ImGui.text(String.format("x: %.2f, y: %.2f, z: %.2f",
					object.x(), object.y(), object.z()));*/
				
				ImGui.separator();
				ImGui.text("Selected object " + object.getId());
				
				ImGui.separator();
				if(ImGui.button("Remove")) {
					world().getObjectManager().remove(object);
					selectedObjectId = -1;
				}
				
				ImGui.separator();
				ImGui.text("Position");
				ImGui.inputFloat("X", objectX, 1.0f, 10.0f);
				ImGui.inputFloat("Y", objectY, 1.0f, 10.0f);
				ImGui.inputFloat("Z", objectZ, 1.0f, 10.0f);
				
				object.x(objectX.get());
				object.y(objectY.get());
				object.z(objectZ.get());
				
				ImGui.separator();
				ImGui.text("Rotation");
				ImGui.inputFloat("X", objectRotationX, 15.0f, 45.0f);
				ImGui.inputFloat("Y", objectRotationY, 15.0f, 45.0f);
				ImGui.inputFloat("Z", objectRotationZ, 15.0f, 45.0f);
				ImGui.inputFloat("W", objectRotationW, 15.0f, 45.0f);
				
				// TODO why does rotation not work
				/*object.rotation(
					(float) Math.toRadians(objectRotationX.get()),
					(float) Math.toRadians(objectRotationY.get()),
					(float) Math.toRadians(objectRotationZ.get()),
					(float) Math.toRadians(objectRotationW.get())
				);*/
			}
			
			ImGui.end();
		}
	}
}
