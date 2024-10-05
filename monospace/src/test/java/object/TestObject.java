package object;

import main.Game;
import org.black_matter.monospace.model.ModelLoader;
import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.components.Renderable;
import org.black_matter.monospace.model.Model;
import org.black_matter.monospace.object.objects.ModelObject;
import org.black_matter.monospace.object.prefab.Prefab;
import org.black_matter.monospace.util.Resource;

import java.util.HashMap;

public class TestObject extends ModelObject {
	
	private static final HashMap<Class<? extends Component>, Component> COMPONENTS = new HashMap<>() {{
		put(Renderable.class, (Renderable<TestObject>) o -> o.render(o));
	}};
	
	public TestObject() {
		super(ModelLoader.load(Game.gameResources().get(Resource.Type.MODELS, "Untitled.gltf")));
		Game.LOGGER.info("I AM TestObject");
	}
	
	public TestObject(Prefab prefab) {
		super(prefab.getModel());
		Game.LOGGER.info("I AM TestObject 2");
	}
}
