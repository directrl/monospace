package org.black_matter.monospace.object.objects;

import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.components.Renderable;

import java.util.HashMap;
import java.util.Random;

public class CubeObject extends GameObject implements Renderable {
	
	public static final HashMap<Class<? extends Component>, Component> COMPONENTS = new HashMap<>() {{
		put(Renderable.class, (Renderable<CubeObject>) o -> o.render(o));
	}};
	
	public CubeObject() {
		super(COMPONENTS);
	}
	
	@Override
	public void render(GameObject o) {
		//System.out.println(this.getId());
		//throw new RuntimeException("Not implemented");
	}
}
