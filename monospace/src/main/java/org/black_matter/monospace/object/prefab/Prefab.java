package org.black_matter.monospace.object.prefab;

import lombok.Getter;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.model.Model;
import org.black_matter.monospace.model.ModelLoader;
import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.util.Resource;
import org.black_matter.monospace.util.Resources;
import org.json.JSONObject;

import java.lang.reflect.Constructor;

public class Prefab {

	@Getter private final String id;
	@Getter private final Model model;
	
	private Class<? extends GameObject> objectClass;
	
	public Prefab(String targetNamespace, String prefabName) {
		var res = new Resources(targetNamespace);
		var json = new JSONObject(res.get(Resource.Type.PREFABS, prefabName).readAsString());
		
		this.id = json.getString("id");
		
		var modelPath = json.getString("model");
		this.model = ModelLoader.load(res.get(Resource.Type.MODELS, modelPath));
		
		var objectClassName = json.getString("class");
		
		try {
			this.objectClass = (Class<? extends GameObject>) Class.forName(objectClassName);
		} catch(ClassNotFoundException e) {
			Monospace.LOGGER.warn(String.format("Unable to get object class for prefab [%s]. We won't be able to instantiate it",
				id));
		}
	}
	
	public GameObject instantiate() {
		if(objectClass == null) return null;
		
		Constructor<?> ctor = null;
		
		try {
			ctor = objectClass.getDeclaredConstructor(Prefab.class);
		} catch(Exception e) { }
		
		try {
			if(ctor == null) {
				try {
					ctor = objectClass.getDeclaredConstructor();
				} catch(NoSuchMethodException e) {
					Monospace.LOGGER.warn(String.format("Unable to instantiate prefab [%s]", id), e);
				}
				
				return (GameObject) ctor.newInstance();
			} else {
				return (GameObject) ctor.newInstance(this);
			}
		} catch(Exception e) {
			Monospace.LOGGER.warn(String.format("Unable to instantiate prefab [%s]", id), e);
		}
		
		return null;
	}
}
