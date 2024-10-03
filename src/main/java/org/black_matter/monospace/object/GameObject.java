package org.black_matter.monospace.object;

import lombok.Getter;
import lombok.Setter;
import org.black_matter.monospace.world.GameWorld;
import org.joml.Vector3f;

import java.util.*;

public abstract class GameObject {
	
	@Getter long id;
	
	private final HashMap<Class<? extends Component>, Component>  components;
	
	@Getter @Setter private GameWorld world;
	
	/*private float x;
	private float y;
	private float z;*/
	protected Vector3f position = new Vector3f(0f, 0f, 0f);
	protected Vector3f rotation = new Vector3f(0f, 0f, 0f);
	
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
	
	public Vector3f rotation() { return this.rotation; }
	public GameObject rotation(Vector3f rotation) { this.rotation = rotation; return this; }
	
	public GameObject rotate(float x, float y, float z) {
		this.rotation.set(
			this.rotation.x + x,
			this.rotation.y + y,
			this.rotation.z + z
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
