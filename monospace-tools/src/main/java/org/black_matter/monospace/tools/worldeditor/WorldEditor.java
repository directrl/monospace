package org.black_matter.monospace.tools.worldeditor;

import imgui.ImGui;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.events.input.MouseMoveEvent;
import org.black_matter.monospace.events.render.gl.ShaderPassPreEvent;
import org.black_matter.monospace.events.world.WorldLoadEvent;
import org.black_matter.monospace.input.KeyBinding;
import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.render.camera.PerspectiveCamera;
import org.black_matter.monospace.tools.worldeditor.objects.CoordinateGrid;
import org.black_matter.monospace.tools.worldeditor.ui.InfoPanel;
import org.black_matter.monospace.tools.worldeditor.ui.ObjectEditor;
import org.black_matter.monospace.tools.worldeditor.ui.PrefabCollection;
import org.black_matter.monospace.tools.worldeditor.keybindings.CameraMovement;
import org.black_matter.monospace.tools.worldeditor.keybindings.EditorShortcuts;
import org.black_matter.monospace.tools.worldeditor.world.WorkspaceWorld;
import org.black_matter.monospace.ui.DebugUI;
import org.black_matter.monospace.util.Ray;
import org.black_matter.monospace.world.GameWorld;
import org.checkerframework.checker.units.qual.C;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class WorldEditor extends Monospace {
	
	private static CameraMovement cameraMovement;
	private static EditorShortcuts editorShortcuts;
	
	private static KeyBinding selectObject;
	
	private static PrefabCollection prefabCollection;
	private static ObjectEditor objectEditor;
	private static InfoPanel infoPanel;
	
	public static CoordinateGrid coordinateGrid;
	
	private static Ray worldRay;
	
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
		
		selectObject = keyBindings().registerBinding(new KeyBinding("object_select", GLFW.GLFW_MOUSE_BUTTON_LEFT, 0));
		
		coordinateGrid = new CoordinateGrid(40, 1.0f);
		
		objectEditor = new ObjectEditor();
		infoPanel = new InfoPanel();
		
		onEvent(WorldLoadEvent.class, null, e -> {
			worldRay = new Ray(camera, e.world());
			
			onEvent(ShaderPassPreEvent.class, e.world(), e1 -> {
				if(e1.program().getId() == GameWorld.WORLD_SHADER.getId()) {
					if(e1.parameter().equals(objectEditor.selectedObject)) {
						e1.program().getUniforms().load("selection", 1);
					} else {
						e1.program().getUniforms().load("selection", 0);
					}
				}
			});
		});
		
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
		
		if(selectObject.wasPressed()) {
			if(worldRay != null) objectEditor.selectedObject = worldRay.getHitObject();
		}
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
			
			if(ImGui.beginMenu("Windows")) {
				ImGui.menuItem("InfoPanel", "", infoPanel.open);
				
				ImGui.endMenu();
			}
			
			ImGui.endMainMenuBar();
		}
		
		if(prefabCollection != null) prefabCollection.ui();
		if(objectEditor != null) objectEditor.ui(prefabCollection);
		if(infoPanel != null) {
			infoPanel.ui();
			
			if(infoPanel.cGridLines[0] != coordinateGrid.getLines()
				|| infoPanel.cGridSpacing[0] != coordinateGrid.getSpacing()) {
				
				var newCoordinateGrid = new CoordinateGrid(
					infoPanel.cGridLines[0],
					infoPanel.cGridSpacing[0]
				);
				
				world.getObjectManager().replace(coordinateGrid, newCoordinateGrid);
				coordinateGrid = newCoordinateGrid;
			}
		}
		
		imgui().end();
	}
	
	public static void chooseAndSpawnModel(Vector3f position) {
		var modelPath = FileDialogs.openFileDialog(new String[] { "*.gltf", "*.glb" },
			"GLTF models (*.gltf, *.glb)");
		
		if(modelPath != null && !modelPath.isEmpty()) {
			((WorkspaceWorld) world).spawnCustomModel(modelPath).position(new Vector3f(position));
		}
	}
}
