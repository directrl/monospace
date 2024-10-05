package org.black_matter.monospace.util;

import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.input.Input;
import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.components.Renderable;
import org.black_matter.monospace.render.camera.Camera3D;
import org.black_matter.monospace.world.GameWorld;
import org.joml.*;

public class Ray {
	
	private Camera3D camera;
	private GameWorld world;
	
	public Ray(Camera3D camera, GameWorld world) {
		this.camera = camera;
		this.world = world;
	}
	
	public GameObject getHitObject() {
		var mousePosition = Input.getMousePosition();
		var windowDimensions = Monospace.window().getDimensions();
		
		float x = (2.0f * (float) mousePosition.x) / windowDimensions.x - 1.0f;
		float y = 1.0f - (2.0f * (float) mousePosition.y) / windowDimensions.y;
		float z = -1.0f;
		
		var mouseDirection = new Vector4f(x, y, z, 1.0f);
		
		mouseDirection.mul(camera.getInverseProjectionMatrix());
		mouseDirection.z = -1.0f;
		mouseDirection.w = 0.0f;
		
		mouseDirection.mul(camera.getInverseViewMatrix());
		
		var min = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
		var max = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
		
		float closestDistance = Float.POSITIVE_INFINITY;
		var eye = camera.getPosition();
		var result = new Vector2f();
		
		GameObject hitObject = null;
		
		for(var object : world.getObjectManager().get().values()) {
			if(object.aabb().size() <= 0) continue;
			
			var renderable = object.getComponent(Renderable.class);
			if(renderable == null) continue;
			
			renderable.computeRenderMatrix(object);
			var modelMatrix = renderable.renderMatrix;
			
			for(var aabb : object.aabb()) {
				var aabbMin = aabb.min();
				min.set(aabbMin.x, aabbMin.y, aabbMin.z, 1.0f);
				min.mul(modelMatrix);
				
				var aabMax = aabb.max();
				max.set(aabMax.x, aabMax.y, aabMax.z, 1.0f);
				max.mul(modelMatrix);
				
				if(Intersectionf.intersectRayAab(
					eye.x, eye.y, eye.z,
					mouseDirection.x, mouseDirection.y, mouseDirection.z,
					min.x, min.y, min.z,
					max.x, max.y, max.z,
					result
				) && result.x < closestDistance) {
					closestDistance = result.x;
					hitObject = object;
				}
			}
		}
		
		return hitObject;
	}
}
