package org.black_matter.monospace.object;

import lombok.Getter;
import org.black_matter.monospace.world.GameWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameObjectManager {
	
	private final Map<Long, GameObject> objects = new HashMap<>();
	private final GameWorld world;
	
	@Getter private long lastId = 0;
	
	public GameObjectManager(GameWorld world) {
		this.world = world;
	}
	
	public Map<Long, GameObject> get() {
		return objects;
	}
	
	public void add(GameObject object) {
		object.id = ++lastId;
		objects.put(object.getId(), object);
		object.setWorld(world);
	}
	
	public void remove(GameObject object) {
		objects.remove(object.getId());
		object.setWorld(null);
		object.id = -1;
	}
	
	public <T extends GameObject> T query(long id) {
		return (T) objects.get(id);
	}
	
	public List<GameObject> query(Class<? extends GameObject.Component> component) {
		List<GameObject> out = new ArrayList<>();
		
		for(var o : objects.values()) {
			for(var c : o.getComponents()) {
				if(c.getClass() == component) {
					out.add(o);
					break;
				}
			}
		}
		
		return out;
	}
}
