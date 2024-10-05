package org.black_matter.monospace.tools.worldeditor;

import imgui.ImGui;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.render.camera.PerspectiveCamera;
import org.black_matter.monospace.tools.worldeditor.keybindings.CameraMovement;
import org.black_matter.monospace.tools.worldeditor.keybindings.EditorShortcuts;
import org.black_matter.monospace.tools.worldeditor.world.WorkspaceWorld;
import org.black_matter.monospace.ui.DebugUI;
import org.black_matter.monospace.world.GameWorld;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class WorldEditor extends Monospace {
	
	private static CameraMovement cameraMovement;
	private static EditorShortcuts editorShortcuts;
	
	public WorldEditor() {
		super("monospace-worldeditor");
		
		engineSettings().window().setTitle("World Editor");
		engineSettings().rendering().setFps(60);
	}
	
	@Override
	public void init() {
		super.init();
		
		camera = new PerspectiveCamera();
		
		cameraMovement = new CameraMovement();
		editorShortcuts = new EditorShortcuts();
		
		world = new WorkspaceWorld();
		world.load();
		
		window().show();
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		cameraMovement.update(delta);
		editorShortcuts.update(delta);
	}
	
	@Override
	public void ui() {
		imgui().begin();
		if(debug()) DebugUI.render();
		
		if(ImGui.beginMainMenuBar()) {
			if(ImGui.beginMenu("World")) {
				if(ImGui.menuItem("New")) {
					world.unload();
					world = new WorkspaceWorld();
					world.load();
					
					camera().setPosition(new Vector3f(0, 0, 0));
				}
				
				if(ImGui.beginMenu("Spawn reference voxel...")) {
					if(ImGui.menuItem("at [0, 0, 0]")) {
						((WorkspaceWorld) world).spawnVoxel().position(new Vector3f());
					}
					
					if(ImGui.menuItem("at camera position")) {
						((WorkspaceWorld) world).spawnVoxel().position(new Vector3f(camera().getPosition()));
					}
					
					ImGui.endMenu();
				}
				
				if(ImGui.beginMenu("Spawn model...")) {
					if(ImGui.menuItem("at [0, 0, 0]")) {
						chooseAndSpawnModel(new Vector3f());
					}
					
					if(ImGui.menuItem("at camera position")) {
						chooseAndSpawnModel(camera().getPosition());
					}
					
					ImGui.endMenu();
				}
				
				if(ImGui.menuItem("Quit")) {
					GLFW.glfwSetWindowShouldClose(window().getHandle(), true);
				}
				
				ImGui.endMenu();
			}
			ImGui.endMainMenuBar();
		}
		imgui().end();
	}
	
	public static void chooseAndSpawnModel(Vector3f position) {
		var modelPath = FileDialogs.openFileDialog(new String[] { "*.gltf" }, "GLTF models (*.gltf)");
		
		if(modelPath != null && !modelPath.isEmpty()) {
			((WorkspaceWorld) world).spawnCustomModel(modelPath).position(new Vector3f(position));
		}
	}
}
