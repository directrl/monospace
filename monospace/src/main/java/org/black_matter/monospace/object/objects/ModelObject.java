package org.black_matter.monospace.object.objects;

import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.components.Renderable;
import org.black_matter.monospace.model.Model;

import java.util.HashMap;

public class ModelObject extends GameObject implements Renderable {
	
	private static final HashMap<Class<? extends Component>, Component> COMPONENTS = new HashMap<>() {{
		put(Renderable.class, (Renderable<ModelObject>) o -> o.render(o));
	}};
	
	private final Model model;
	
	public ModelObject(Model model) {
		super(COMPONENTS);
		
		this.model = model;
		this.aabb = model.getAabb();
	}
	
	@Override
	public void render(GameObject o) {
		model.render();
	}
}
