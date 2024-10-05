package org.black_matter.monospace.render.camera;

public class OrthographicCamera extends Camera3D {
	
	@Override
	public void computeProjectionMatrix() {
		this.projectionMatrix.setOrtho(
			-(this.width / 2) * getFov(),
			(this.width / 2) * getFov(),
			-(this.height / 2) * getFov(),
			(this.height / 2) * getFov(),
			Z_NEAR,
			Z_FAR
		);
		
		this.inverseProjectionMatrix.set(this.projectionMatrix).invert();
	}
}
