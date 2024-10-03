package org.black_matter.monospace.event;

public interface EventCallback<T extends Event> {
	
	void execute(T event);
}