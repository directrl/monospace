package org.black_matter.monospace.core;

import org.black_matter.monospace.event.EventCaller;
import org.black_matter.monospace.events.core.GameOptionChangeEvent;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GameSettings implements Closeable, EventCaller {
	
	private File configFile;
	private JSONObject options;
	
	public GameSettings(File configFile) throws IOException {
		if(!configFile.exists()) {
			options = new JSONObject();
			
			configFile.getParentFile().mkdirs();
			if(configFile.createNewFile()) {
				this.configFile = configFile;
				Files.writeString(configFile.toPath(), options.toString());
			} else {
				throw new IOException("Unable to create config file " + configFile);
			}
		} else {
			this.configFile = configFile;
			options = new JSONObject(Files.readString(configFile.toPath()));
		}
	}
	
	public <T> T getOptionOrDefault(String name, T _default) {
		if(options.has(name)) {
			return (T) options.get(name);
		} else {
			options.put(name, _default);
			return _default;
		}
	}
	
	public <T> T getOptionOrNull(String name) {
		if(options.has(name)) {
			return (T) options.get(name);
		} else {
			return null;
		}
	}
	
	public void setOption(String name, Object value) {
		this.callEvent(GameOptionChangeEvent.class, this,
			new GameOptionChangeEvent(name, options.get(name), value));
		options.put(name, value);
	}
	
	@Override
	public void close() throws IOException {
		Files.writeString(configFile.toPath(), options.toString(2));
	}
}
