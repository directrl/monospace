package org.black_matter.monospace.tools.worldeditor;

import imgui.ImGui;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.render.camera.PerspectiveCamera;
import org.black_matter.monospace.tools.worldeditor.objects.CoordinateGrid;
import org.black_matter.monospace.tools.worldeditor.ui.ObjectEditor;
import org.black_matter.monospace.tools.worldeditor.ui.PrefabCollection;
import org.black_matter.monospace.tools.worldeditor.keybindings.CameraMovement;
import org.black_matter.monospace.tools.worldeditor.keybindings.EditorShortcuts;
import org.black_matter.monospace.tools.worldeditor.world.WorkspaceWorld;
import org.black_matter.monospace.ui.DebugUI;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class WorldEditor extends Monospace {
	
	private static CameraMovement cameraMovement;
	private static EditorShortcuts editorShortcuts;
	
	private static PrefabCollection prefabCollection;
	private static ObjectEditor objectEditor;
	
	private static CoordinateGrid coordinateGrid;
	
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
		
		objectEditor = new ObjectEditor();
		
		coordinateGrid = new CoordinateGrid(20, 1.0f);
		
		world = new WorkspaceWorld();
		world.load();
		
		world.getObjectManager().add(coordinateGrid);
		
		window().show();
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		cameraMovement.update(delta);
		editorShortcuts.update(delta);
		
		var gridPos = new Vector3f(camera.getPosition());
		gridPos.x -= coordinateGrid.getSize() / 2;
		gridPos.y = 0;
		gridPos.z -= coordinateGrid.getSize() / 2;
		gridPos.round();
		
		coordinateGrid.position(gridPos);
	}
	
	@Override
	public void render() {
		super.render();
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
			
			if(ImGui.beginMenu("Assets")) {
				if(ImGui.menuItem("Set external assets directory...")) {
					var assetsDirectory = FileDialogs.selectFolderDialog();
					prefabCollection = new PrefabCollection(assetsDirectory);
				}
				
				ImGui.endMenu();
			}
			
			ImGui.endMainMenuBar();
		}
		
		if(prefabCollection != null) prefabCollection.ui();
		if(objectEditor != null) objectEditor.ui(prefabCollection);
		
		imgui().end();
	}
	
	public static void chooseAndSpawnModel(Vector3f position) {
		var modelPath = FileDialogs.openFileDialog(new String[] { "*.gltf" }, "GLTF models (*.gltf)");
		
		if(modelPath != null && !modelPath.isEmpty()) {
			((WorkspaceWorld) world).spawnCustomModel(modelPath).position(new Vector3f(position));
		}
	}
}
