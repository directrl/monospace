package org.black_matter.monospace.core;

import java.nio.file.Path;

public class Configuration {
	
	public static Platform.OS SYSTEM;
	
	public static Path DATA_PATH;
	public static Path CONFIG_PATH;
	public static Path LOGS_PATH;
	
	public static void initDirs(String basename) {
		var osName = System.getProperty("os.name").toLowerCase();
		
		if(osName.contains("win")) {
			SYSTEM = Platform.OS.WINDOWS;
		} else if(osName.contains("mac") || osName.contains("darwin")) {
			SYSTEM = Platform.OS.MACOS;
		} else if(osName.contains("nux")) {
			SYSTEM = Platform.OS.LINUX;
		} else {
			SYSTEM = Platform.OS.OTHER;
		}
		
		switch(SYSTEM) {
			case WINDOWS:
				DATA_PATH = Path.of(System.getenv("APPDATA"), basename);
				break;
			case MACOS:
				DATA_PATH = Path.of(System.getenv("HOME"), "Library/Application Support", basename);
				break;
			case LINUX:
				var dataDir = System.getenv("XDG_DATA_HOME");
				
				if(dataDir == null || dataDir.isEmpty() || dataDir.isBlank()) {
					dataDir = System.getenv("HOME") + "/.local/share";
				}
				
				DATA_PATH = Path.of(dataDir, basename);
				break;
			default:
				DATA_PATH = Path.of(System.getenv("HOME"), basename);
				break;
		}
		
		resolvePaths();
	}
	
	public static void resolvePaths() {
		CONFIG_PATH = DATA_PATH.resolve("config");
		LOGS_PATH = DATA_PATH.resolve("logs");
	}
	
	public class Platform {
		
		public enum OS {
			
			WINDOWS,
			MACOS,
			LINUX,
			OTHER
		}
	}
}
