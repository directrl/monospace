package org.black_matter.monospace.tools.worldeditor.world;

import org.black_matter.monospace.model.Model;
import org.black_matter.monospace.model.ModelLoader;
import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.objects.ModelObject;
import org.black_matter.monospace.util.Resource;
import org.black_matter.monospace.world.GameWorld;

import static org.black_matter.monospace.tools.worldeditor.WorldEditor.*;

public class WorkspaceWorld extends GameWorld {

	public GameObject spawnVoxel() {
		var object = new ModelObject(ModelLoader.load(
			gameResources().get(Resource.Type.MODELS, "voxel.gltf")
		));
		
		getObjectManager().add(object);
		return object;
	}
	
	public GameObject spawnCustomModel(String modelPath) {
		var object = new ModelObject(ModelLoader.load(
			new Resource(Resource.Type.EXTERNAL, modelPath, modelPath)
		));
		
		getObjectManager().add(object);
		return object;
	}
}
