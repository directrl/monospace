import lombok.Getter;
import lombok.Setter;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.input.KeyAction;
import org.black_matter.monospace.object.objects.CubeObject;
import org.black_matter.monospace.world.GameWorld;
import org.lwjgl.glfw.GLFW;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class Game extends Monospace {
	
	@Getter @Setter private static GameWorld world;
	
	private static Random random = new Random();
	
	public Game() {
		super("test-game");
		
		engineSettings().setWindowTitle("Test Game");
		engineSettings().setFps(60);
		
		keyBindings().registerAction("meow", GLFW.GLFW_KEY_F, new KeyAction() {
			
			@Override
			public void onPress() {
				if(world != null) {
					LOGGER.info("Spawning cube");
					
					world.getObjectManager().add(
						new CubeObject()
							.x(random.nextFloat(-10, 10))
							.y(random.nextFloat(-10, 10))
							.z(random.nextFloat(-10, 10))
					);
				}
			}
		});
	}
	
	long start;
	long end;
	
	@Override
	public void init() {
		world = new GameWorld();
		
		start = System.currentTimeMillis();
		
		for(int i = 0; i < 1000000; i++) {
			world.getObjectManager().add(
				new CubeObject()
					.x(random.nextFloat(-10, 10))
					.y(random.nextFloat(-10, 10))
					.z(random.nextFloat(-10, 10))
			);
		}
		
		end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Override
	public void update(float delta) {
		if(world != null) {
			start = System.nanoTime();
			world.update(0);
			end = System.nanoTime();
			System.out.println("Update: " + (end - start)/1000000d);
		}
	}
	
	@Override
	public void render() {
		if(world != null) {
			start = System.nanoTime();
			world.render();
			end = System.nanoTime();
			System.out.println("Render: " + (end - start)/1000000d);
		}
	}
}
