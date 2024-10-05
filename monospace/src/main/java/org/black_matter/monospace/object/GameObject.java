package org.black_matter.monospace.object;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.black_matter.monospace.object.collision.AABB;
import org.black_matter.monospace.util.ListHashMap;
import org.black_matter.monospace.world.GameWorld;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public abstract class GameObject {
	
	private final HashMap<Class<? extends Component>, Component> components;
	
	@Getter @Setter(AccessLevel.PACKAGE) protected long id;
	@Getter @Setter protected GameWorld world;
	
	protected Vector3f position = new Vector3f();
	protected Quaternionf rotation = new Quaternionf();
	protected List<AABB> aabb = new ArrayList<>();
	
	protected float scale = 1;
	
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
	
	public Vector3f position() { return position; }
	public GameObject position(Vector3f position) { this.position = position; return this; }
	
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
	
	public List<AABB> aabb() { return Collections.unmodifiableList(this.aabb); }
	
	public float scale() { return this.scale; }
	public GameObject scale(float scale) { this.scale = scale; return this; }
	
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
