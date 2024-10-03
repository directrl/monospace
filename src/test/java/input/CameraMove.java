package input;

import main.Game;
import org.black_matter.monospace.event.EventCaller;
import org.black_matter.monospace.events.core.GameOptionChangeEvent;
import org.black_matter.monospace.events.input.MouseMoveEvent;
import org.black_matter.monospace.input.KeyAction;
import org.black_matter.monospace.input.MouseAction;

public class CameraMove {
	
	private static float CAMERA_SPEED = 0.02f;
	
	public static class Forward extends KeyAction {
		
		@Override
		public void onDown() {
			Game.camera().move(0, 0, 0, 0, CAMERA_SPEED * Game.timer().getDelta(), 0);
		}
	}
	
	public static class Backward extends KeyAction {
		
		@Override
		public void onDown() {
			Game.camera().move(0, 0, 0, 0, 0, CAMERA_SPEED * Game.timer().getDelta());
		}
	}
	
	public static class Left extends KeyAction {
		
		@Override
		public void onDown() {
			Game.camera().move(0, 0, CAMERA_SPEED * Game.timer().getDelta(), 0, 0, 0);
		}
	}
	
	public static class Right extends KeyAction {
		
		@Override
		public void onDown() {
			Game.camera().move(0, 0, 0, CAMERA_SPEED * Game.timer().getDelta(), 0, 0);
		}
	}
	
	public static class Up extends KeyAction {
		
		@Override
		public void onDown() {
			Game.camera().move(CAMERA_SPEED * Game.timer().getDelta(), 0, 0, 0, 0, 0);
		}
	}
	
	public static class Down extends KeyAction {
		
		@Override
		public void onDown() {
			Game.camera().move(0, CAMERA_SPEED * Game.timer().getDelta(), 0, 0, 0, 0);
		}
	}
	
	public static class Rotation extends MouseAction.Button implements EventCaller {
		
		float mouseSensitivity = Game.gameSettings().getOptionOrDefault("mouseSensitivity", 0.5f);
		
		public Rotation() {
			onEvent(GameOptionChangeEvent.class, Game.gameSettings(), e -> {
				if(e.name().equals("mouseSensitivity")) {
					mouseSensitivity = (float) e.to();
				}
			});
			
			onEvent(MouseMoveEvent.class, Game.input(), e -> {
				if(down) {
					Game.camera().getRotation().add(
						(float) -Math.toRadians(-e.dy() * mouseSensitivity),
						(float) -Math.toRadians(-e.dx() * mouseSensitivity)
					);
					Game.camera().computeViewMatrix();
				}
			});
		}
	}
}
