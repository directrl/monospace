package org.black_matter.monospace.object;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.black_matter.monospace.util.ListHashMap;
import org.black_matter.monospace.world.GameWorld;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public abstract class GameObject {
	
	private final HashMap<Class<? extends Component>, Component>  components;
	
	@Getter @Setter(AccessLevel.PACKAGE) private long id;
	@Getter @Setter private GameWorld world;
	
	public Vector3f position = new Vector3f();
	public Quaternionf rotation = new Quaternionf();
	
	@Accessors(fluent = true)
	@Getter @Setter public float scale = 1;
	
	public GameObject(HashMap<Class<? extends Component>, Component> components) {
		this.components = components;
	}
	
	void destroy() { }
	
	public Set<Map.Entry<Class<? extends Component>, Component>> getComponents() {
		return Collections.unmodifiableSet(components.entrySet());
	}
	
	public boolean hasComponent(Class<? extends Component> componentClass) {
		return components.containsKey(componentClass);
	}
	
	public <T extends Component> T getComponent(Class<T> componentClass) {
		if(!hasComponent(componentClass)) return null;
		else return (T) components.get(componentClass);
	}
	
	public float x() { return position.x; }
	public float y() { return position.y; }
	public float z() { return position.z; }
	
	public GameObject x(float x) { position.x = x; return this; }
	public GameObject y(float y) { position.y = y; return this; }
	public GameObject z(float z) { position.z = z; return this; }
	
	public Quaternionf rotation() { return rotation; }
	public GameObject rotation(Quaternionf rotation) { this.rotation = rotation; return this; }
	
	public GameObject rotation(float x, float y, float z, float angle) {
		rotation.fromAxisAngleRad(x, y, z, angle);
		return this;
	}
	
	public GameObject rotate(float x, float y, float z, float angle) {
		rotation.set(
			rotation.x + x,
			rotation.y + y,
			rotation.z + z,
			rotation.w + angle
		);
		
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof GameObject) {
			return id == ((GameObject) obj).getId();
		} else {
			return obj.equals(this);
		}
	}
	
	public interface Component { }
}
