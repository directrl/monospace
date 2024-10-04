package org.black_matter.monospace.input;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

public class KeyBinding {

	@Getter private final String name;
	@Getter @Setter private int key;
	@Getter @Setter private int mods;

	public boolean down = false;
	public boolean pressed = false;
	public boolean released = false;

	public KeyBinding(String name, int key, int mods) {
		this.name = name;
		this.key = key;
		this.mods = mods;
	}

	public void fromOption(JSONObject o) {
		key = (int) o.get("key");
		mods = (int) o.get("mods");
	}

	public JSONObject toOption() {
		var o = new JSONObject();
		o.put("key", key);
		o.put("mods", mods);

		return o;
	}
}