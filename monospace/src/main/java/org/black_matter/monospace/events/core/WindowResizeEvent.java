package org.black_matter.monospace.events.core;

import org.black_matter.monospace.event.Event;

public record WindowResizeEvent(long handle, int width, int height) implements Event { }
