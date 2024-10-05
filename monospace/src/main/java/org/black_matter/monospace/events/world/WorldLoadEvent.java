package org.black_matter.monospace.events.world;

import org.black_matter.monospace.event.Event;
import org.black_matter.monospace.world.GameWorld;

public record WorldLoadEvent(GameWorld world) implements Event { }
