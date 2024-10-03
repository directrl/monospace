package org.black_matter.monospace.object.components;

import org.black_matter.monospace.object.GameObject;

public interface Renderable<T extends GameObject> extends GameObject.Component {
	
	void render(T o);
}
