package org.black_matter.monospace.world;

import lombok.Getter;
import lombok.Setter;
import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.GameObjectManager;
import org.black_matter.monospace.object.components.Renderable;
import org.black_matter.monospace.object.components.Tickable;
import org.black_matter.monospace.render.gl.ShaderProgram;
import org.black_matter.monospace.util.DeltaTimer;

public class GameWorld {

	public static final ShaderProgram WORLD_SHADER = ShaderProgram.Registry.get("world");
	
	@Getter private final GameObjectManager objectManager = new GameObjectManager(this);
	@Getter private boolean loaded = false;
	
	public void load() {
		loaded = true;
	}
	
	public void unload() {
		loaded = false;
	}
	
	public void update(float delta) {
		objectManager.get().forEach((id, object) -> {
			var tickable = object.getComponent(Tickable.class);
			if(tickable != null) tickable.tick(object, delta);
			
			var renderable = object.getComponent(Renderable.class);
			if(renderable != null) {
				renderable.computeRenderMatrix(object);
				
				WORLD_SHADER.getUniforms().load("modelMatrix", renderable.renderMatrix);
				
				renderable.render(object);
			}
		});
	}
}
