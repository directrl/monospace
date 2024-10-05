package org.black_matter.monospace.tools.worldeditor.ui;

import imgui.ImGui;
import org.black_matter.monospace.object.objects.ModelObject;
import org.black_matter.monospace.object.prefab.Prefab;
import org.black_matter.monospace.tools.worldeditor.WorldEditor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.black_matter.monospace.tools.worldeditor.WorldEditor.*;

public class PrefabCollection {
	
	public List<Prefab> prefabs = new ArrayList<>();
	public Map<Long, Prefab> prefabObjects = new HashMap<>();
	
	public PrefabCollection(String assetDirectory) {
		for(var prefabFile : Path.of(assetDirectory, "prefabs").toFile().listFiles()) {
			if(!prefabFile.getName().endsWith(".json")) continue;
			
			WorldEditor.LOGGER.info("Located prefab file: " + prefabFile);
			
			var prefabName = prefabFile.getName().replace(".json", "");
			var prefab = Prefab.fromExternal(assetDirectory, prefabName);
			prefabs.add(prefab);
		}
		
		WorldEditor.LOGGER.info("Prefab count: " + prefabs.size());
	}
	
	public void ui() {
		if(ImGui.begin("Prefabs")) {
			for(var prefab : prefabs) {
				if(ImGui.button(prefab.getId())) {
					var object = new ModelObject(prefab.getModel())
						.x(camera().getPosition().x)
						.y(camera().getPosition().y)
						.z(camera().getPosition().z - 2);
					
					world().getObjectManager().add(object);
					prefabObjects.put(object.getId(), prefab);
				}
			}
			
			ImGui.end();
		}
	}
}
