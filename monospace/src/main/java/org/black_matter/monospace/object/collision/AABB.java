package org.black_matter.monospace.object.collision;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joml.Vector3f;

@Accessors(fluent = true)
public class AABB {

	@Getter @Setter private Vector3f min;
	@Getter @Setter private Vector3f max;
	
	public AABB() {
		this.min = new Vector3f();
		this.max = new Vector3f();
	}
	
	public AABB(Vector3f min, Vector3f max) {
		this.min = min;
		this.max = max;
	}
	
	public AABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.min = new Vector3f(minX, minY, minZ);
		this.max = new Vector3f(maxX, maxY, maxZ);
	}
	
	public float minX() { return min.x; }
	public float minY() { return min.y; }
	public float minZ() { return min.z; }
	
	public float maxX() { return max.x; }
	public float maxY() { return max.y; }
	public float maxZ() { return max.z; }
	
	public AABB minX(float x) { min.x = x; return this; }
	public AABB minY(float y) { min.y = y; return this; }
	public AABB minZ(float z) { min.z = z; return this; }
	
	public AABB maxX(float x) { max.x = x; return this; }
	public AABB maxY(float y) { max.y = y; return this; }
	public AABB maxZ(float z) { max.z = z; return this; }
}
