package org.black_matter.monospace.tools.worldeditor.ui;

import imgui.ImGui;
import imgui.type.ImFloat;
import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.util.QuaternionUtils;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.black_matter.monospace.tools.worldeditor.WorldEditor.*;

public class ObjectEditor {
	
	public GameObject selectedObject = null;
	
	private final ImFloat objectX = new ImFloat(0);
	private final ImFloat objectY = new ImFloat(0);
	private final ImFloat objectZ = new ImFloat(0);
	
	/*private final ImFloat objectRotationX = new ImFloat(0);
	private final ImFloat objectRotationY = new ImFloat(0);
	private final ImFloat objectRotationZ = new ImFloat(0);*/
	
	private final float[] objectRotationX = new float[1];
	private final float[] objectRotationY = new float[1];
	private final float[] objectRotationZ = new float[1];
	
	public void ui(PrefabCollection prefabCollection) {
		if(ImGui.begin("Objects")) {
			if(ImGui.beginListBox("Object listing")) {
				for(var object : world().getObjectManager().get().values()) {
					if(object.equals(coordinateGrid)) continue;
					
					if(ImGui.selectable(String.format("ID: %d, %s", object.getId(),
						prefabCollection != null && prefabCollection.prefabObjects.containsKey(object.getId()) ?
							prefabCollection.prefabObjects.get(object.getId()).getId()
							: object.getClass().getSimpleName()))) {
						
						selectedObject = object;
						
						objectX.set(object.x());
						objectY.set(object.y());
						objectZ.set(object.z());
						
						var rot = QuaternionUtils.getRotationXYZ(object.rotation());
						
						objectRotationX[0] = ((float) Math.toDegrees(rot.x));
						objectRotationY[0] = ((float) Math.toDegrees(rot.y));
						objectRotationZ[0] = ((float) Math.toDegrees(rot.z));
					}
				}
				
				ImGui.endListBox();
			}
			
			if(selectedObject != null) {
				var object = selectedObject;
				
				/*ImGui.text(String.format("x: %.2f, y: %.2f, z: %.2f",
					object.x(), object.y(), object.z()));*/
				
				ImGui.separator();
				ImGui.text("Selected object " + object.getId());
				
				ImGui.separator();
				if(ImGui.button("Remove")) {
					world().getObjectManager().remove(object);
					selectedObject = null;
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
				ImGui.sliderFloat("rX", objectRotationX, 0, 360, "%.0f");
				ImGui.sliderFloat("rY", objectRotationY, 0, 360, "%.0f");
				ImGui.sliderFloat("rZ", objectRotationZ, 0, 360, "%.0f");
				
				// TODO why does rotation not work
				/*object.rotation().fromAxisAngleDeg(
					(float) objectRotationX.get(),
					(float) objectRotationY.get(),
					(float) objectRotationZ.get(),
					(float) objectRotationW.get()
				);*/
				
				object.rotation().rotationXYZ(
					(float) Math.toRadians(objectRotationX[0]),
					(float) Math.toRadians(objectRotationY[0]),
					(float) Math.toRadians(objectRotationZ[0])
				);
			}
			
			ImGui.end();
		}
	}
}
