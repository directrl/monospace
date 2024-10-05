package org.black_matter.monospace.tools.worldeditor;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.*;

public class FileDialogs {
	
	public static String openFileDialog(String[] filters, String filterDescription) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			var filterBuffer = stack.mallocPointer(filters.length);
			
			for(var filter : filters) {
				filterBuffer.put(stack.UTF8(filter));
			}
			filterBuffer.flip();
			
			return tinyfd_openFileDialog("Open file...", null, filterBuffer, filterDescription, false);
		}
	}
}
