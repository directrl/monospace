package org.black_matter.monospace.object;

import lombok.Getter;
import org.black_matter.monospace.util.ListHashMap;
import org.black_matter.monospace.world.GameWorld;

import java.util.*;

public class GameObjectManager {
	
	private final Map<Long, GameObject> objects = new HashMap<>();
	private final GameWorld world;
	
	@Getter private long lastId = 0;
	
	public GameObjectManager(GameWorld world) {
		this.world = world;
	}
	
	public Map<Long, GameObject> get() {
		return Collections.unmodifiableMap(objects);
	}
	
	public void add(GameObject object) {
		object.setId(++lastId);
		objects.put(object.getId(), object);
		object.setWorld(world);
	}
	
	public void remove(GameObject object) {
		objects.remove(object.getId());
		object.setWorld(null);
		object.setId(-1);
	}
	
	public <T extends GameObject> T query(long id) {
		return (T) objects.get(id);
	}
}
