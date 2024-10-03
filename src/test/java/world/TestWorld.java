package world;

import org.black_matter.monospace.object.objects.CubeObject;
import org.black_matter.monospace.world.GameWorld;

import java.util.Random;

public class TestWorld extends GameWorld {
	
	private final static Random RANDOM = new Random();
	
	@Override
	public void load() {
		for(int i = 0; i < 1000000; i++) {
			this.getObjectManager().add(
				new CubeObject()
					.x(RANDOM.nextFloat(-10, 10))
					.y(RANDOM.nextFloat(-10, 10))
					.z(RANDOM.nextFloat(-10, 10))
			);
		}
		
		super.load();
	}
}
