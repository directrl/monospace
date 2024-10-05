package object;

import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.components.Renderable;
import org.black_matter.monospace.model.Model;

import java.util.HashMap;

public class TestObject extends GameObject implements Renderable {
	
	private static final HashMap<Class<? extends Component>, Component> COMPONENTS = new HashMap<>() {{
		put(Renderable.class, (Renderable<TestObject>) o -> o.render(o));
	}};
	
	private Model model;
	
	public TestObject(Model model) {
		super(COMPONENTS);
		this.model = model;
	}
	
	@Override
	public void render(GameObject o) {
		if(model != null) model.render();
	}
}
