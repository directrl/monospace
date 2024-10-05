package org.black_matter.monospace.world;

import lombok.Getter;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.event.EventCaller;
import org.black_matter.monospace.events.render.gl.ShaderPassPostEvent;
import org.black_matter.monospace.events.render.gl.ShaderPassPreEvent;
import org.black_matter.monospace.events.world.WorldLoadEvent;
import org.black_matter.monospace.events.world.WorldUnloadEvent;
import org.black_matter.monospace.object.GameObjectManager;
import org.black_matter.monospace.object.components.Renderable;
import org.black_matter.monospace.object.components.Tickable;
import org.black_matter.monospace.render.gl.ShaderProgram;

public class GameWorld implements EventCaller {

	public static final ShaderProgram WORLD_SHADER = ShaderProgram.Registry.get("world");
	
	@Getter private final GameObjectManager objectManager = new GameObjectManager(this);
	@Getter private boolean loaded = false;
	
	public void load() {
		callEvent(WorldLoadEvent.class, null, new WorldLoadEvent(this));
		loaded = true;
	}
	
	public void unload() {
		callEvent(WorldUnloadEvent.class, null, new WorldUnloadEvent(this));
		loaded = false;
	}
	
	public void update(float delta) {
		objectManager.get().forEach((id, object) -> {
			var tickable = object.getComponent(Tickable.class);
			if(tickable != null) tickable.tick(object, delta);
			
			var renderable = object.getComponent(Renderable.class);
			if(renderable != null) {
				renderable.computeRenderMatrix(object);
				
				callEvent(ShaderPassPreEvent.class, this, new ShaderPassPreEvent(WORLD_SHADER, object));
				WORLD_SHADER.getUniforms().load("modelMatrix", renderable.renderMatrix);
				
				renderable.render(object);
				callEvent(ShaderPassPostEvent.class, this, new ShaderPassPostEvent(WORLD_SHADER, object));
			}
		});
	}
}
