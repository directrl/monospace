package org.black_matter.monospace.object.objects;

import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.components.Renderable;
import org.black_matter.monospace.render.Model;

import java.util.HashMap;

public class CubeObject extends GameObject implements Renderable {
	
	private static final HashMap<Class<? extends Component>, Component> COMPONENTS = new HashMap<>() {{
		put(Renderable.class, (Renderable<CubeObject>) o -> o.render(o));
	}};
	
	private static final Model MODEL = Model.Registry.MODELS.get("cube");
	
	public CubeObject() { super(COMPONENTS); }
	
	@Override
	public void render(GameObject o) {
		MODEL.render();
	}
}
