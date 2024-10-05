package org.black_matter.monospace.events.core;

import org.black_matter.monospace.event.Event;

public record GameOptionChangeEvent(String name, Object from, Object to) implements Event { }
