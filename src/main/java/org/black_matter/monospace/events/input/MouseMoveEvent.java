package org.black_matter.monospace.events.input;

import org.black_matter.monospace.event.Event;

public record MouseMoveEvent(double x, double y, double dx, double dy) implements Event { }
