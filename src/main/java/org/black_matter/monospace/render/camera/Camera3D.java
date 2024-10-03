package org.black_matter.monospace.render.camera;

import lombok.Getter;
import lombok.Setter;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.event.EventCaller;
import org.black_matter.monospace.events.core.WindowResizeEvent;
import org.black_matter.monospace.render.gl.ShaderProgram;
import org.black_matter.monospace.render.gl.UniformMap;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class Camera3D implements EventCaller {
	
	protected static float Z_NEAR = 0.01f;
	protected static float Z_FAR = 1000f;
	
	@Getter @Setter private Vector3f position;
	@Getter @Setter private Vector2f rotation;
	
	private Vector3f direction;
	private Vector3f right;
	private Vector3f up;
	
	@Getter protected Matrix4f projectionMatrix;
	@Getter private Matrix4f viewMatrix;
	
	@Getter @Setter private float fov = 1;
	
	@Getter protected float width;
	@Getter protected float height;
	
	public Camera3D() {
		this.direction = new Vector3f();
		this.right = new Vector3f();
		this.up = new Vector3f();
		this.position = new Vector3f();
		this.projectionMatrix = new Matrix4f();
		this.viewMatrix = new Matrix4f();
		this.rotation = new Vector2f();
		
		this.width = Monospace.window().getDimensions().x;
		this.height = Monospace.window().getDimensions().y;
		
		computeProjectionMatrix();
		computeViewMatrix();
		
		this.onEvent(WindowResizeEvent.class, Monospace.window(), e -> {
			this.width = e.width();
			this.height = e.height();
			computeProjectionMatrix();
		});
	}
	
	public void move(float up, float down, float left, float right, float forward, float backward) {
		if(up != 0) {
			viewMatrix.positiveY(this.up).mul(up);
			position.add(this.up);
		}
		
		if(down != 0) {
			viewMatrix.positiveY(this.up).mul(down);
			position.sub(this.up);
		}
		
		if(left != 0) {
			viewMatrix.positiveX(this.right).mul(left);
			position.sub(this.right);
		}
		
		if(right != 0) {
			viewMatrix.positiveX(this.right).mul(right);
			position.add(this.right);
		}
		
		if(forward != 0) {
			viewMatrix.positiveZ(direction).mul(forward);
			position.sub(direction);
		}
		
		if(backward != 0) {
			viewMatrix.positiveZ(direction).mul(backward);
			position.add(direction);
		}
		
		computeViewMatrix();
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
		computeViewMatrix();
	}
	
	public void setRotation(Vector2f rotation) {
		this.rotation = rotation;
		computeViewMatrix();
	}
	
	public void setFov(float fov) {
		this.fov = fov;
		computeProjectionMatrix();
	}
	
	public void render(ShaderProgram shader) {
		shader.getUniforms().load("projectionMatrix", projectionMatrix);
		shader.getUniforms().load("viewMatrix", viewMatrix);
	}
	
	public abstract void computeProjectionMatrix();
	
	public void computeViewMatrix() {
		viewMatrix.identity()
			.rotateX(rotation.x)
			.rotateY(rotation.y)
			.translate(-position.x, -position.y, -position.z);
	}
}
