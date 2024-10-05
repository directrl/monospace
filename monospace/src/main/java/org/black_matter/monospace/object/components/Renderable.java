package org.black_matter.monospace.object.components;

import org.black_matter.monospace.object.GameObject;
import org.joml.Matrix4f;

public interface Renderable<T extends GameObject> extends GameObject.Component {
	
	Matrix4f renderMatrix = new Matrix4f();
	
	void render(T o);
	
	default void computeRenderMatrix(T o) {
		renderMatrix.translationRotateScale(o.position(), o.rotation(), o.scale());
	}
}
