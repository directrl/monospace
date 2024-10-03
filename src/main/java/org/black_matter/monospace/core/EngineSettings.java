package org.black_matter.monospace.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EngineSettings {
	
	private String windowTitle = "Monospace Engine";
	private int windowWidth = 1280;
	private int windowHeight = 720;
	
	private int fps = 60;
}
