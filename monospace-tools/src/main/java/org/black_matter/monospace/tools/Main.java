package org.black_matter.monospace.tools;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.black_matter.monospace.tools.worldeditor.WorldEditor;

import java.util.ArrayList;
import java.util.List;

public class Main {
	
	private static final List<String> AVAILABLE_TOOLS = new ArrayList<>() {{
		add("worldeditor");
	}};
	
	@Parameter(names={ "--tool", "-t" }) String tool = "";
	
	public static void main(String... args) {
		Main main = new Main();
		
		JCommander.newBuilder()
			.addObject(main)
			.build()
			.parse(args);
		
		main.run();
	}
	
	public void run() {
		if(!AVAILABLE_TOOLS.contains(tool)) {
			System.out.println(String.format("Invalid tool: %s", tool));
			System.out.println("Available tools:");
			
			AVAILABLE_TOOLS.forEach(System.out::println);
			return;
		}
		
		switch(tool) {
			case "worldeditor":
				new WorldEditor().launch();
			default:
				throw new RuntimeException("Tool not implemented: " + tool);
		}
	}
}
