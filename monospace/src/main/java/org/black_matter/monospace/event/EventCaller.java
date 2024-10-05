package org.black_matter.monospace.event;

import org.black_matter.monospace.util.ListHashMap;

import java.util.*;

// TODO global events?
public interface EventCaller {
	
	Map<Class<?>, Map<Object, EventCallback>> eventListeners = new HashMap<>();
	
	default <T extends Event> void onEvent(Class<T> cls, Object inst, EventCallback<T> callback) {
		if(eventListeners.containsKey(cls)) {
			var callbacks = eventListeners.get(cls);
			callbacks.put(inst, callback);
			
			//Engine.LOGGER.trace(STR."Registered event [\{cls.getSimpleName()}] for \{callback.toString()}");
		} else {
			eventListeners.put(cls, new HashMap<>() {{ put(inst, callback); }});
		}
	}
	
	default <T extends Event> void callEvent(Class<T> cls, Object inst, T event) {
		if(eventListeners.containsKey(cls)) {
			var callbacks = eventListeners.get(cls);
			
			//Engine.LOGGER.trace(STR."Calling event [\{cls.getSimpleName()}] for \{callbacks.size()} listener(s)");
			
			callbacks.forEach((o, callback) -> {
				if(inst.equals(o)) {
					callback.execute(event);
				}
			});
		}
	}
}
