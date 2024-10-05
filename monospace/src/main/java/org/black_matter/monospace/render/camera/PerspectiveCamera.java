package org.black_matter.monospace.render.camera;

public class PerspectiveCamera extends Camera3D {
	
	@Override
	public void computeProjectionMatrix() {
		this.projectionMatrix.setPerspective(
			this.getFov(),
			(this.width / this.height),
			Z_NEAR,
			Z_FAR
		);
	}
}
