package object;

import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.components.Renderable;
import org.black_matter.monospace.render.Model;

import java.util.HashMap;

public class TestObject extends GameObject implements Renderable {
	
	private static final HashMap<Class<? extends Component>, Component> COMPONENTS = new HashMap<>() {{
		put(Renderable.class, (Renderable<TestObject>) o -> o.render(o));
	}};
	
	private static Model model;
	
	public TestObject() {
		super(COMPONENTS);
		if(model == null) model = Model.Registry.MODELS.get("test");
	}
	
	@Override
	public void render(GameObject o) {
		model.render();
	}
}
