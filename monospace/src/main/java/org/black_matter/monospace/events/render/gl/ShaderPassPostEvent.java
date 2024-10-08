package org.black_matter.monospace.events.render.gl;

import org.black_matter.monospace.event.Event;
import org.black_matter.monospace.render.gl.ShaderProgram;

public record ShaderPassPostEvent(ShaderProgram program, Object parameter) implements Event { }
