package org.black_matter.monospace.object.components;

import org.black_matter.monospace.object.GameObject;

public interface Tickable<T extends GameObject> extends GameObject.Component {
	
	void tick(T o, float delta);
}
