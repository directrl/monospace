package org.black_matter.monospace.world;

import lombok.Getter;
import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.GameObjectManager;
import org.black_matter.monospace.object.components.Renderable;
import org.black_matter.monospace.object.components.Tickable;

public class GameWorld {

	@Getter private final GameObjectManager objectManager = new GameObjectManager(this);
	
	public void update(float delta) {
		objectManager.get().forEach((id, object) -> {
			var tickable = object.getComponent(Tickable.class);
			if(tickable != null) tickable.tick(object, delta);
		});
	}
	
	public void render() {
		objectManager.get().forEach((id, object) -> {
			var renderable = object.getComponent(Renderable.class);
			if(renderable != null) renderable.render(object);
		});
	}
}
