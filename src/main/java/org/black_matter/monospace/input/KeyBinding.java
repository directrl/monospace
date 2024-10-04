package org.black_matter.monospace.input;

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
        key = o.get("key");
        mods = o.get("mods");
    }

    public JSONObject toOption() {
        var o = new JSONObject();
        o.set("key", key);
        o.set("mods", mods);

        return o;
    }
}